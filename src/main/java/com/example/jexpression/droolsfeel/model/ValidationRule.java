package com.example.jexpression.droolsfeel.model;

import java.util.List;

/**
 * Raw validation rule from JSON configuration.
 * 
 * <p>
 * Represents input format, converted to {@link FeelRule} for evaluation.
 * 
 * @param code        Unique rule identifier
 * @param name        Human-readable description
 * @param status      "Enabled" or "Disabled"
 * @param validations Validation conditions
 */
public record ValidationRule(
    String code,
    String name,
    String status,
    List<Validation> validations
) {

    /**
     * Check if rule should be processed.
     */
    public boolean isEnabled() {
        return "Enabled".equalsIgnoreCase(status);
    }

    /**
     * Check if rule has validations.
     */
    public boolean hasValidations() {
        return validations != null && !validations.isEmpty();
    }
}
