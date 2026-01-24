package com.example.jexpression.droolsfeel.converter;

import com.example.jexpression.droolsfeel.model.RuleCondition;

/**
 * Converts RuleCondition to FEEL expression using pre-defined templates.
 * No string building - just select template and apply values.
 */
public final class FeelExpressionBuilder {

    private FeelExpressionBuilder() {}

    public static String toFeel(RuleCondition c) {
        String field = c.field();

        return switch (c.op()) {
            // Comparison
            case "Equals" -> isString(c)
                    ? FeelTemplate.STRING_EQUALS.apply(field, c.firstValue())
                    : FeelTemplate.EQUALS.apply(field, typed(c));
            case "NotEquals" -> isString(c)
                    ? FeelTemplate.STRING_NOT_EQUALS.apply(field, c.firstValue())
                    : FeelTemplate.NOT_EQUALS.apply(field, typed(c));
            case "Greater" -> FeelTemplate.GREATER_THAN.apply(field, num(c));
            case "GreaterOrEqual" -> FeelTemplate.GREATER_OR_EQUAL.apply(field, num(c));
            case "Less" -> FeelTemplate.LESS_THAN.apply(field, num(c));
            case "LessOrEqual" -> FeelTemplate.LESS_OR_EQUAL.apply(field, num(c));

            // Range
            case "Between" -> isDate(c)
                    ? FeelTemplate.DATE_BETWEEN.apply(field, c.firstValue(), c.secondValue())
                    : FeelTemplate.BETWEEN.apply(field, typed(c, 0), typed(c, 1));
            case "In" -> isString(c)
                    ? FeelTemplate.STRING_IN_LIST.applyWithStringList(field, c.values().toArray(String[]::new))
                    : FeelTemplate.IN_LIST.applyWithList(field, c.values().toArray());
            case "NotIn" -> FeelTemplate.NOT_IN_LIST.applyWithList(field, c.values().toArray());

            // String
            case "Contains" -> FeelTemplate.CONTAINS.apply(field, c.firstValue());
            case "StartsWith" -> FeelTemplate.STARTS_WITH.apply(field, c.firstValue());
            case "EndsWith" -> FeelTemplate.ENDS_WITH.apply(field, c.firstValue());
            case "Matches" -> FeelTemplate.MATCHES.apply(field, c.firstValue());

            // Null
            case "Exists", "IsNotNull" -> FeelTemplate.IS_NOT_NULL.apply(field);
            case "IsNull" -> FeelTemplate.IS_NULL.apply(field);

            // List
            case "ListContains" -> FeelTemplate.LIST_CONTAINS.apply(field, c.firstValue());

            default -> throw new IllegalArgumentException("Unknown operator: " + c.op());
        };
    }

    private static boolean isString(RuleCondition c) {
        return c.type() == null || "string".equalsIgnoreCase(c.type());
    }

    private static boolean isDate(RuleCondition c) {
        return "date".equalsIgnoreCase(c.type());
    }

    private static Object typed(RuleCondition c) {
        return typed(c, 0);
    }

    private static Object typed(RuleCondition c, int index) {
        String val = index == 0 ? c.firstValue() : c.secondValue();
        if (val == null)
            return null;
        return c.isNumber() ? Double.parseDouble(val) : val;
    }

    private static Number num(RuleCondition c) {
        String val = c.firstValue();
        return val != null ? Double.parseDouble(val) : 0;
    }
}
