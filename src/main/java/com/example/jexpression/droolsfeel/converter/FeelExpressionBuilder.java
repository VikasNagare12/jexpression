package com.example.jexpression.droolsfeel.converter;

import com.example.jexpression.droolsfeel.model.RuleCondition;

/**
 * Converts RuleCondition to FEEL expression using type-based template lookup.
 * 
 * <p>
 * Uses {@link FeelTemplate#forOperator(String, String)} for automatic template
 * selection.
 */
public final class FeelExpressionBuilder {

    private FeelExpressionBuilder() {}

    /**
     * Convert RuleCondition to FEEL expression string.
     */
    public static String toFeel(RuleCondition c) {
        String field = c.field();
        String type = c.type();
        String op = c.op();

        FeelTemplate template = FeelTemplate.forOperator(op, type);

        return switch (op) {
            // List operators need special handling
            case "In" -> isString(type)
                    ? template.applyWithStringList(field, c.values().toArray(String[]::new))
                    : template.applyWithList(field, c.values().toArray());
            case "NotIn" -> template.applyWithList(field, c.values().toArray());

            // Range operator needs 3 args
            case "Between" -> template.apply(field, c.firstValue(), c.secondValue());

            // Null checks need only field
            case "Exists", "IsNotNull", "IsNull" -> template.apply(field);

            // Standard 2-arg operators
            default -> template.apply(field, getValue(c));
        };
    }

    private static boolean isString(String type) {
        return type == null || "string".equalsIgnoreCase(type);
    }

    private static Object getValue(RuleCondition c) {
        String val = c.firstValue();
        if (val == null)
            return null;

        // Convert to number if type is number
        if ("number".equalsIgnoreCase(c.type())) {
            return Double.parseDouble(val);
        }
        return val;
    }
}
