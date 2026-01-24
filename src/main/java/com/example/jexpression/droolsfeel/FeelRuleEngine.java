package com.example.jexpression.droolsfeel;

import com.example.jexpression.droolsfeel.model.EvaluationResult;
import com.example.jexpression.droolsfeel.model.FeelRule;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.lang.CompiledExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Production-ready FEEL Rule Evaluation Engine.
 * 
 * <h3>Features:</h3>
 * <ul>
 * <li>Thread-safe evaluation</li>
 * <li>Pre-compiled expressions for performance</li>
 * <li>Structured results with timing</li>
 * <li>Batch and single-rule evaluation</li>
 * </ul>
 * 
 * <h3>Usage:</h3>
 * 
 * <pre>{@code
 * List<EvaluationResult> results = engine.evaluate(rules, transaction, "transaction");
 * List<String> failedCodes = engine.getFailedCodes(rules, transaction, "transaction");
 * }</pre>
 */
@Service
public class FeelRuleEngine {

    private static final Logger log = LoggerFactory.getLogger(FeelRuleEngine.class);

    // Thread-safe FEEL instance per thread
    private static final ThreadLocal<FEEL> FEEL_INSTANCE = ThreadLocal.withInitial(FEEL::newInstance);

    // ─────────────────────────────────────────────────────────────
    // Public API
    // ─────────────────────────────────────────────────────────────

    /**
     * Evaluate all rules and return structured results.
     * 
     * @param rules       Pre-compiled FEEL rules
     * @param dto         DTO object for context
     * @param contextName Context key (e.g., "transaction")
     * @return List of evaluation results for all rules
     */
    public List<EvaluationResult> evaluate(List<FeelRule> rules, Object dto, String contextName) {
        Objects.requireNonNull(rules, "rules must not be null");
        Objects.requireNonNull(dto, "dto must not be null");
        Objects.requireNonNull(contextName, "contextName must not be null");

        Map<String, Object> context = buildContext(dto, contextName);
        List<EvaluationResult> results = new ArrayList<>(rules.size());

        for (FeelRule rule : rules) {
            results.add(evaluateRule(rule, context));
        }

        logSummary(results);
        return results;
    }

    /**
     * Evaluate rules and return only failed rule codes.
     */
    public List<String> getFailedCodes(List<FeelRule> rules, Object dto, String contextName) {
        return evaluate(rules, dto, contextName).stream()
                .filter(EvaluationResult::failed)
                .map(EvaluationResult::ruleCode)
                .toList();
    }

    /**
     * Evaluate rules and return only failures (including errors).
     */
    public List<EvaluationResult> getFailures(List<FeelRule> rules, Object dto, String contextName) {
        return evaluate(rules, dto, contextName).stream()
                .filter(r -> !r.passed())
                .toList();
    }

    /**
     * Evaluate a single rule.
     */
    public EvaluationResult evaluateSingle(FeelRule rule, Object dto, String contextName) {
        Map<String, Object> context = buildContext(dto, contextName);
        return evaluateRule(rule, context);
    }

    // ─────────────────────────────────────────────────────────────
    // Internal
    // ─────────────────────────────────────────────────────────────

    private EvaluationResult evaluateRule(FeelRule rule, Map<String, Object> context) {
        // Skip invalid rules
        if (!rule.canEvaluate()) {
            log.warn("Skipping invalid rule: {}", rule.code());
            return EvaluationResult.skipped(rule, "Rule compilation failed");
        }

        long startNanos = System.nanoTime();

        try {
            FEEL feel = FEEL_INSTANCE.get();
            Object result = feel.evaluate(rule.compiledExpression(), context);
            long elapsed = System.nanoTime() - startNanos;

            boolean passed = Boolean.TRUE.equals(result);

            if (log.isDebugEnabled()) {
                log.debug("Rule [{}] → {} ({}ms)",
                        rule.code(), passed ? "PASS" : "FAIL", elapsed / 1_000_000.0);
            }

            return passed
                    ? EvaluationResult.passed(rule, elapsed)
                    : EvaluationResult.failed(rule, elapsed);

        } catch (Exception e) {
            long elapsed = System.nanoTime() - startNanos;
            log.error("Rule [{}] error: {}", rule.code(), e.getMessage());
            return EvaluationResult.error(rule, e.getMessage(), elapsed);
        }
    }

    private Map<String, Object> buildContext(Object dto, String contextName) {
        Map<String, Object> context = new HashMap<>();
        context.put(contextName, dto);
        return context;
    }

    private void logSummary(List<EvaluationResult> results) {
        long passed = results.stream().filter(EvaluationResult::passed).count();
        long failed = results.stream().filter(EvaluationResult::failed).count();
        long errors = results.stream().filter(EvaluationResult::isError).count();
        long skipped = results.stream().filter(EvaluationResult::isSkipped).count();

        log.info("Evaluation complete: {} passed, {} failed, {} errors, {} skipped",
                passed, failed, errors, skipped);
    }
}
