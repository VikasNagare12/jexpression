package com.example.jexpression.service;

import com.example.jexpression.model.Rule;
import com.example.jexpression.model.Transaction;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.ReadContext;
import io.github.jamsesso.jsonlogic.JsonLogic;
import io.github.jamsesso.jsonlogic.JsonLogicException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service responsible for evaluating business rules against transaction data.
 * <p>
 * This service implements a two-phase evaluation:
 * 1. Index Lookup: A fast, pre-filter check using JsonPath to verify simple
 * field matches.
 * 2. Logic Evaluation: A comprehensive evaluation using JsonLogic for complex
 * conditions.
 */
@Service
public class RuleService {

    private static final Logger logger = LoggerFactory.getLogger(RuleService.class);

    private final JsonLogic jsonLogic;
    private final ObjectMapper objectMapper;
    private final List<DataEnricher> enrichers;

    // Cache compiled JsonPaths to improve performance for repeated logic executions
    private final Map<String, JsonPath> pathCache = new ConcurrentHashMap<>();

    public RuleService(ObjectMapper objectMapper, List<DataEnricher> enrichers) {
        this.jsonLogic = new JsonLogic();
        this.objectMapper = objectMapper;
        this.enrichers = enrichers;
    }

    /**
     * Evaluates a Rule against a Transaction.
     *
     * @param rule The rule definition containing index filters and logic.
     * @param data The transaction data to evaluate.
     * @return true if the rule applies and evaluates to true; false otherwise.
     */
    public boolean evaluate(Rule rule, Transaction data) {
        if (rule == null || data == null) {
            logger.warn("Attempted evaluation with null Rule or Transaction. Returning false.");
            return false;
        }

        try {
            // Optimization: Serialize once for JsonPath context
            String dataJson = objectMapper.writeValueAsString(data);
            ReadContext readContext = JsonPath.parse(dataJson);

            // Phase 1: Fail fast using the Index Table (Lookup)
            if (!checkIndexMatches(rule.getIndex(), readContext)) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Rule [{}] skipped due to index mismatch.", rule.getRuleId());
                }
                return false;
            }

            // Phase 2: Evaluate complex business logic
            return evaluateLogic(rule, data);

        } catch (JsonProcessingException e) {
            logger.error("Serialization failed for rule [{}]. Skipping evaluation.", rule.getRuleId(), e);
            throw new RuntimeException("Data serialization failed", e); // Or custom RuleEvaluationException
        }
    }

    /**
     * Checks if the transaction data matches the rule's index criteria.
     *
     * @param index The index criteria (Map of Path -> Allowed Values).
     * @param ctx   The JsonPath read context for the transaction data.
     * @return true if all index criteria are met (or if index is empty).
     */
    private boolean checkIndexMatches(Map<String, List<String>> index, ReadContext ctx) {
        if (index == null || index.isEmpty()) {
            return true; // Global rule
        }

        for (Map.Entry<String, List<String>> entry : index.entrySet()) {
            String pathKey = entry.getKey();
            List<String> allowedValues = entry.getValue();

            // Normalize path: Ensure it starts with "$." or "$"
            String normalizedPath = pathKey.startsWith("$") ? pathKey : "$." + pathKey;

            try {
                // Use cached JsonPath or compile and cache it
                JsonPath jsonPath = pathCache.computeIfAbsent(normalizedPath, JsonPath::compile);

                Object rawValue = ctx.read(jsonPath);
                String valueStr = String.valueOf(rawValue);

                // Null or Non-matching value checks
                if (rawValue == null || !allowedValues.contains(valueStr)) {
                    if (logger.isTraceEnabled()) {
                        logger.trace("Index Mismatch - Path: [{}], Found: [{}], Allowed: {}", normalizedPath, valueStr,
                                allowedValues);
                    }
                    return false;
                }

            } catch (PathNotFoundException e) {
                // The path defined in the rule does not exist in the data. Treat as mismatch.
                logger.warn("Index path not found in data: [{}]", normalizedPath);
                return false;
            } catch (Exception e) {
                logger.error("Unexpected error evaluating index path: [{}]", normalizedPath, e);
                return false;
            }
        }
        return true;
    }

    /**
     * Executes the JsonLogic evaluation.
     */
    private boolean evaluateLogic(Rule rule, Transaction data) {
        if (rule.getLogic() == null) {
            return false;
        }

        try {
            // 1. Convert Transaction to Map
            @SuppressWarnings("unchecked")
            Map<String, Object> dataMap = objectMapper.convertValue(data, Map.class);

            // 2. Run Enrichers (Fetch Database Data)
            for (DataEnricher enricher : enrichers) {
                Map<String, Object> enrichedData = enricher.enrich(data);
                if (enrichedData != null) {
                    dataMap.putAll(enrichedData);
                }
            }

            // Debug: print map to see what JsonLogic sees

            // Debug: print map to see what JsonLogic sees
            // System.out.println("DEBUG Data Map: " + dataMap);

            String logicJson = objectMapper.writeValueAsString(rule.getLogic());

            Object result = jsonLogic.apply(logicJson, dataMap);

            if (result instanceof Boolean) {
                return (Boolean) result;
            }
            logger.warn("Rule [{}] logic did not return a boolean result. Result: {}", rule.getRuleId(), result);
            return false;

        } catch (JsonLogicException | JsonProcessingException | IllegalArgumentException e) {
            logger.error("Error evaluating logic for rule [{}]", rule.getRuleId(), e);
            throw new RuntimeException("Rule Logic Evaluation failed", e);
        }
    }
}
