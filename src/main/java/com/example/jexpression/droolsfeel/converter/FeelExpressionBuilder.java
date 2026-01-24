package com.example.jexpression.droolsfeel.converter;

import com.example.jexpression.droolsfeel.model.RuleCondition;

/**
 * Converts RuleCondition to FEEL expression string.
 */
public final class FeelExpressionBuilder {

    private FeelExpressionBuilder() {}

    public static String toFeel(RuleCondition c) {
        String f = c.field();

        return switch (c.op()) {
            // Comparison
            case "Equals" -> isString(c) ? Expr.strEq(f, c.firstValue()) : Expr.eq(f, typed(c));
            case "NotEquals" -> isString(c) ? Expr.strNeq(f, c.firstValue()) : Expr.neq(f, typed(c));
            case "Greater" -> Expr.gt(f, num(c));
            case "GreaterOrEqual" -> Expr.gte(f, num(c));
            case "Less" -> Expr.lt(f, num(c));
            case "LessOrEqual" -> Expr.lte(f, num(c));

            // Range
            case "Between" -> isDate(c)
                    ? Expr.dateBetween(f, c.firstValue(), c.secondValue())
                    : Expr.between(f, typed(c, 0), typed(c, 1));
            case "In" -> isString(c)
                    ? Expr.strIn(f, c.values().toArray(String[]::new))
                    : Expr.in(f, c.values().toArray());
            case "NotIn" -> Expr.notIn(f, c.values().toArray());

            // String
            case "Contains" -> Expr.contains(f, c.firstValue());
            case "StartsWith" -> Expr.startsWith(f, c.firstValue());
            case "EndsWith" -> Expr.endsWith(f, c.firstValue());
            case "Matches" -> Expr.matches(f, c.firstValue());

            // Null
            case "Exists", "IsNotNull" -> Expr.isNotNull(f);
            case "IsNull" -> Expr.isNull(f);

            // List
            case "ListContains" -> Expr.in(f, c.firstValue());

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

    private static Object typed(RuleCondition c, int idx) {
        String val = idx == 0 ? c.firstValue() : c.secondValue();
        if (val == null)
            return null;
        return c.isNumber() ? Double.parseDouble(val) : val;
    }

    private static Number num(RuleCondition c) {
        String val = c.firstValue();
        return val != null ? Double.parseDouble(val) : 0;
    }
}
