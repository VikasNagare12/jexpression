package com.example.jexpression.droolsfeel.model;

import java.util.List;

/**
 * Validation rule from JSON.
 */
public record ValidationRule(
    String code,
    String name,
    String status,
    List<Validation> validations
) {
    public boolean isEnabled() {
        return "Enabled".equalsIgnoreCase(status);
    }
}
