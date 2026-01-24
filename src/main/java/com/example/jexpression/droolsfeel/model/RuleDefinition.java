package com.example.jexpression.droolsfeel.model;

import java.util.List;

/**
 * Rule definition from JSON configuration.
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
    List<RuleCondition> conditions
) {
    
    public boolean isEnabled() {
        return "Enabled".equalsIgnoreCase(status);
    }
    
    public boolean hasConditions() {
        return conditions != null && !conditions.isEmpty();
    }
}
