package com.example.jexpression.service;

import com.example.jexpression.model.Action;
import com.example.jexpression.model.Rule;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import io.github.jamsesso.jsonlogic.JsonLogic;
import io.github.jamsesso.jsonlogic.JsonLogicException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Rule Engine - returns Action DTO when rule triggers.
 */
@Service
public class RuleService {

    private final JsonLogic jsonLogic = new JsonLogic();
    private final ObjectMapper objectMapper;

    public RuleService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Evaluate a rule against data.
     * 
     * @return Optional.empty() = SKIP or logic didn't match
     *         Optional.of(Action) = Action to execute
     */
    public Optional<Action> evaluate(Rule rule, Object data) throws JsonProcessingException {
        String dataJson = objectMapper.writeValueAsString(data);

        // STEP 1: Check filter
        if (!checkFilter(rule.getIndex(), dataJson)) {
            return Optional.empty(); // SKIP - rule doesn't apply
        }

        // STEP 2: Evaluate logic
        if (evaluateLogic(rule.getLogic(), data)) {
            return Optional.ofNullable(rule.getAction()); // Return Action
        }

        return Optional.empty(); // Logic didn't match
    }

    private boolean checkFilter(Map<String, List<String>> filter, String dataJson) {
        if (filter == null || filter.isEmpty()) {
            return true;
        }

        for (Map.Entry<String, List<String>> entry : filter.entrySet()) {
            String path = "$." + entry.getKey();
            List<String> allowedValues = entry.getValue();

            try {
                Object value = JsonPath.read(dataJson, path);
                if (!allowedValues.contains(String.valueOf(value))) {
                    return false;
                }
            } catch (PathNotFoundException e) {
                return false;
            }
        }
        return true;
    }

    private boolean evaluateLogic(Map<String, Object> logic, Object data) {
        if (logic == null) {
            return true;
        }

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> dataMap = objectMapper.convertValue(data, Map.class);
            String logicJson = objectMapper.writeValueAsString(logic);

            Object result = jsonLogic.apply(logicJson, dataMap);
            return Boolean.TRUE.equals(result);

        } catch (JsonLogicException | JsonProcessingException e) {
            return false;
        }
    }
}
