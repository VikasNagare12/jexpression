package com.example.jexpression.droolsfeel.model;

import java.util.List;

/**
 * Rule definition from JSON configuration.
 * 
 * <p>
 * Represents input format, converted to {@link FeelRule} for evaluation.
 * 
 * @param code       Unique rule identifier
 * @param name       Human-readable description
 * @param status     "Enabled" or "Disabled"
 * @param conditions List of conditions to evaluate
 */
public record RuleDefinition(
        String code,
        String name,
        String status,
        List<Condition> conditions) {

    /**
     * Check if rule should be processed.
     */
    public boolean isEnabled() {
        return "Enabled".equalsIgnoreCase(status);
    }

    /**
     * Check if rule has conditions.
     */
    public boolean hasConditions() {
        return conditions != null && !conditions.isEmpty();
    }
}
