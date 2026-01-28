package com.example.jexpression.model;

import java.util.List;

/**
 * Single condition within a rule.
 * Dumb data carrier only - no business logic.
 */
public record RuleCondition(
        String field,
        String type,
        String op,
        String source,
        List<String> values) {
}
