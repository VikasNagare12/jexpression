package com.example.jexpression.droolsfeel.model;

import java.util.List;

/**
 * Single condition within a rule.
 * 
 * @param field  Field path (e.g., "transaction.amount")
 * @param type   Data type: "string", "number", "date", "boolean"
 * @param op     Operator name (e.g., "Equals", "GreaterOrEqual")
 * @param source Value source: "static", "prdm", "config"
 * @param values Comparison values
 */
public record RuleCondition(
        String field,
        String type,
        String op,
        String source,
        List<String> values) {

    public boolean isDate() {
        return "date".equalsIgnoreCase(type);
    }

    public boolean isNumber() {
        return "number".equalsIgnoreCase(type);
    }

    public boolean isString() {
        return "string".equalsIgnoreCase(type) || type == null;
    }

    public String firstValue() {
        return values != null && !values.isEmpty() ? values.get(0) : null;
    }

    public String secondValue() {
        return values != null && values.size() > 1 ? values.get(1) : null;
    }
}
