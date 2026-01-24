package com.example.jexpression.droolsfeel.model;

import java.util.List;

/**
 * Single validation condition within a rule.
 * 
 * @param field  Field path (e.g., "transaction.amount")
 * @param type   Data type: "string", "number", "date", "boolean", "list"
 * @param op     Operator name (e.g., "Equals", "GreaterOrEqual")
 * @param source Value source: "static", "prdm", "config"
 * @param values Comparison values
 */
public record Validation(
    String field,
    String type,
    String op,
    String source,
    List<String> values
) {

    /**
     * Get the operator enum.
     */
    public FeelOperator operator() {
        return FeelOperator.fromJsonName(op);
    }

    /**
     * Check if this is a date field.
     */
    public boolean isDate() {
        return "date".equalsIgnoreCase(type);
    }

    /**
     * Check if this is a number field.
     */
    public boolean isNumber() {
        return "number".equalsIgnoreCase(type);
    }

    /**
     * Check if this is a string field.
     */
    public boolean isString() {
        return "string".equalsIgnoreCase(type) || type == null;
    }

    /**
     * Get first value (for single-value operators).
     */
    public String firstValue() {
        return values != null && !values.isEmpty() ? values.get(0) : null;
    }

    /**
     * Get second value (for range operators).
     */
    public String secondValue() {
        return values != null && values.size() > 1 ? values.get(1) : null;
    }
}
