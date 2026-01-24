package com.example.jexpression.droolsfeel.converter;

import com.example.jexpression.droolsfeel.model.RuleCondition;

/**
 * Converts RuleCondition to FEEL expression string.
 */
public final class FeelExpressionBuilder {

    private FeelExpressionBuilder() {}

    public static String toFeel(RuleCondition c) {
        String field = c.field();

        return switch (c.op()) {
            // Comparison
            case "Equals" -> isString(c) ? Expr.stringEquals(field, c.firstValue()) : Expr.equals(field, typed(c));
            case "NotEquals" ->
                isString(c) ? Expr.stringNotEquals(field, c.firstValue()) : Expr.notEquals(field, typed(c));
            case "Greater" -> Expr.greaterThan(field, num(c));
            case "GreaterOrEqual" -> Expr.greaterOrEqual(field, num(c));
            case "Less" -> Expr.lessThan(field, num(c));
            case "LessOrEqual" -> Expr.lessOrEqual(field, num(c));

            // Range
            case "Between" -> isDate(c)
                    ? Expr.dateBetween(field, c.firstValue(), c.secondValue())
                    : Expr.between(field, typed(c, 0), typed(c, 1));
            case "In" -> isString(c)
                    ? Expr.stringInList(field, c.values().toArray(String[]::new))
                    : Expr.inList(field, c.values().toArray());
            case "NotIn" -> Expr.notInList(field, c.values().toArray());

            // String
            case "Contains" -> Expr.contains(field, c.firstValue());
            case "StartsWith" -> Expr.startsWith(field, c.firstValue());
            case "EndsWith" -> Expr.endsWith(field, c.firstValue());
            case "Matches" -> Expr.matches(field, c.firstValue());

            // Null
            case "Exists", "IsNotNull" -> Expr.isNotNull(field);
            case "IsNull" -> Expr.isNull(field);

            // List
            case "ListContains" -> Expr.inList(field, c.firstValue());

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
