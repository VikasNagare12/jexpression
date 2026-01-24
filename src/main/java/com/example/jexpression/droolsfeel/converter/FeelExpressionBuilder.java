package com.example.jexpression.droolsfeel.converter;

import com.example.jexpression.droolsfeel.model.FeelOperator;
import com.example.jexpression.droolsfeel.model.Validation;

/**
 * Converts Validation to FEEL expression using type-safe builder.
 */
public final class FeelExpressionBuilder {

    private FeelExpressionBuilder() {}

    /**
     * Convert validation to FEEL expression.
     */
    public static String toFeel(Validation v) {
        FeelOperator op = v.operator();
        FeelExpression.FieldBuilder fb = fieldBuilder(v);

        return switch (op) {
            // Comparison
            case EQUALS -> fb.equalsValue(typedValue(v, 0)).build();
            case NOT_EQUALS -> fb.notEquals(typedValue(v, 0)).build();
            case GREATER -> fb.greaterThan(numValue(v)).build();
            case GREATER_OR_EQUAL -> fb.greaterOrEqual(numValue(v)).build();
            case LESS -> fb.lessThan(numValue(v)).build();
            case LESS_OR_EQUAL -> fb.lessOrEqual(numValue(v)).build();

            // Range
            case BETWEEN -> fb.between(typedValue(v, 0), typedValue(v, 1)).build();
            case IN -> fb.in(v.values().toArray()).build();
            case NOT_IN -> fb.notIn(v.values().toArray()).build();

            // String
            case CONTAINS -> fb.contains(v.firstValue()).build();
            case STARTS_WITH -> fb.startsWith(v.firstValue()).build();
            case ENDS_WITH -> fb.endsWith(v.firstValue()).build();
            case MATCHES -> fb.matches(v.firstValue()).build();

            // Null
            case EXISTS, IS_NOT_NULL -> fb.isNotNull().build();
            case IS_NULL -> fb.isNull().build();

            // List
            case LIST_CONTAINS -> fb.in(v.firstValue()).build();
        };
    }

    /**
     * Create appropriate field builder based on type.
     */
    private static FeelExpression.FieldBuilder fieldBuilder(Validation v) {
        return switch (v.type() != null ? v.type().toLowerCase() : "string") {
            case "number", "boolean" -> FeelExpression.field(v.field());
            case "date", "datetime" -> FeelExpression.dateField(v.field());
            default -> FeelExpression.stringField(v.field());
        };
    }

    /**
     * Get typed value at index.
     */
    private static Object typedValue(Validation v, int index) {
        String val = index == 0 ? v.firstValue() : v.secondValue();
        if (val == null)
            return null;

        if (v.isNumber()) {
            return Double.parseDouble(val);
        }
        return val;
    }

    /**
     * Get numeric value.
     */
    private static Number numValue(Validation v) {
        String val = v.firstValue();
        return val != null ? Double.parseDouble(val) : 0;
    }
}
