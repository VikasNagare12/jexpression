package com.example.jexpression.service;

import com.example.jexpression.model.EvaluationResult;
import com.example.jexpression.model.FeelRule;
import org.apache.commons.lang3.Validate;
import org.kie.dmn.feel.FEEL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Production-ready FEEL Rule Evaluation Service.
 * Thread-safe, with timing metrics.
 */
@Service
public class FeelRuleService {

    private static final Logger log = LoggerFactory.getLogger(FeelRuleService.class);

    // Thread-safe FEEL instance per thread
    private static final ThreadLocal<FEEL> FEEL_INSTANCE = ThreadLocal.withInitial(FEEL::newInstance);

    // ─────────────────────────────────────────────────────────────
    // Public API
    // ─────────────────────────────────────────────────────────────

    /**
     * Evaluate all rules and return structured results.
     */
    public List<EvaluationResult> evaluate(List<FeelRule> rules, Object dto, String contextName) {
        Validate.notNull(rules, "rules must not be null");
        Validate.notNull(dto, "dto must not be null");
        Validate.notBlank(contextName, "contextName must not be blank");

        var context = Map.of(contextName, dto);
        var results = new ArrayList<EvaluationResult>(rules.size());

        for (var rule : rules) {
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
        return evaluateRule(rule, Map.of(contextName, dto));
    }

    // ─────────────────────────────────────────────────────────────
    // Internal
    // ─────────────────────────────────────────────────────────────

    private EvaluationResult evaluateRule(FeelRule rule, Map<String, Object> context) {
        if (!rule.canEvaluate()) {
            log.warn("Skipping invalid rule: {}", rule.code());
            return EvaluationResult.skipped(rule, "Rule compilation failed");
        }

        var startNanos = System.nanoTime();

        try {
            var feel = FEEL_INSTANCE.get();
            var result = feel.evaluate(rule.compiledExpression(), context);
            var elapsed = System.nanoTime() - startNanos;

            var passed = Boolean.TRUE.equals(result);

            if (log.isDebugEnabled()) {
                log.debug("Rule [{}] → {} ({}ms)",
                        rule.code(), passed ? "PASS" : "FAIL", elapsed / 1_000_000.0);
            }

            return passed
                    ? EvaluationResult.passed(rule, elapsed)
                    : EvaluationResult.failed(rule, elapsed);

        } catch (Exception e) {
            var elapsed = System.nanoTime() - startNanos;
            log.error("Rule [{}] error: {}", rule.code(), e.getMessage());
            return EvaluationResult.error(rule, e.getMessage(), elapsed);
        }
    }

    private void logSummary(List<EvaluationResult> results) {
        var passed = results.stream().filter(EvaluationResult::passed).count();
        var failed = results.stream().filter(EvaluationResult::failed).count();
        var errors = results.stream().filter(EvaluationResult::isError).count();
        var skipped = results.stream().filter(EvaluationResult::isSkipped).count();

        log.info("Evaluation complete: {} passed, {} failed, {} errors, {} skipped",
                passed, failed, errors, skipped);
    }
}
