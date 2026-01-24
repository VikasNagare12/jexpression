package com.example.jexpression.droolsfeel.model;

import java.time.Duration;
import java.time.Instant;

/**
 * Structured result of rule evaluation.
 * 
 * <p>
 * Provides rich context for evaluation outcome:
 * <ul>
 * <li>{@code PASSED} - Validation succeeded</li>
 * <li>{@code FAILED} - Business rule violated</li>
 * <li>{@code ERROR} - Technical error during evaluation</li>
 * <li>{@code SKIPPED} - Rule was invalid/disabled</li>
 * </ul>
 */
public record EvaluationResult(
        String ruleCode,
        String ruleName,
        String expression,
        Status status,
        String errorMessage,
        long evaluationTimeNanos) {

    public enum Status {
        PASSED, FAILED, ERROR, SKIPPED
    }

    // ─────────────────────────────────────────────────────────────
    // Factory Methods
    // ─────────────────────────────────────────────────────────────

    public static EvaluationResult passed(FeelRule rule, long nanos) {
        return new EvaluationResult(
                rule.code(), rule.name(), rule.expression(),
                Status.PASSED, null, nanos);
    }

    public static EvaluationResult failed(FeelRule rule, long nanos) {
        return new EvaluationResult(
                rule.code(), rule.name(), rule.expression(),
                Status.FAILED, null, nanos);
    }

    public static EvaluationResult error(FeelRule rule, String error, long nanos) {
        return new EvaluationResult(
                rule.code(), rule.name(), rule.expression(),
                Status.ERROR, error, nanos);
    }

    public static EvaluationResult skipped(FeelRule rule, String reason) {
        return new EvaluationResult(
                rule.code(), rule.name(), rule.expression(),
                Status.SKIPPED, reason, 0);
    }

    // ─────────────────────────────────────────────────────────────
    // Convenience Methods
    // ─────────────────────────────────────────────────────────────

    public boolean passed() {
        return status == Status.PASSED;
    }

    public boolean failed() {
        return status == Status.FAILED;
    }

    public boolean isError() {
        return status == Status.ERROR;
    }

    public boolean isSkipped() {
        return status == Status.SKIPPED;
    }

    /**
     * Evaluation time in milliseconds.
     */
    public double evaluationTimeMs() {
        return evaluationTimeNanos / 1_000_000.0;
    }
}
