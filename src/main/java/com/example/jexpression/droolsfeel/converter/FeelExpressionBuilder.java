package com.example.jexpression.droolsfeel.converter;

import com.example.jexpression.droolsfeel.model.RuleCondition;
import com.example.jexpression.droolsfeel.model.FeelOperator;
import org.apache.commons.lang3.StringUtils;

/**
 * Converts RuleCondition to FEEL expression using type-safe builder.
 */
public final class FeelExpressionBuilder {

    private FeelExpressionBuilder() {}

    public static String toFeel(RuleCondition c) {
        var fb = fieldBuilder(c);
        var op = c.operator();

        return switch (op) {
            case EQUALS -> fb.equalsValue(typedValue(c, 0)).build();
            case NOT_EQUALS -> fb.notEquals(typedValue(c, 0)).build();
            case GREATER -> fb.greaterThan(numValue(c)).build();
            case GREATER_OR_EQUAL -> fb.greaterOrEqual(numValue(c)).build();
            case LESS -> fb.lessThan(numValue(c)).build();
            case LESS_OR_EQUAL -> fb.lessOrEqual(numValue(c)).build();
            case BETWEEN -> fb.between(typedValue(c, 0), typedValue(c, 1)).build();
            case IN -> fb.in(c.values().toArray()).build();
            case NOT_IN -> fb.notIn(c.values().toArray()).build();
            case CONTAINS -> fb.contains(c.firstValue()).build();
            case STARTS_WITH -> fb.startsWith(c.firstValue()).build();
            case ENDS_WITH -> fb.endsWith(c.firstValue()).build();
            case MATCHES -> fb.matches(c.firstValue()).build();
            case EXISTS, IS_NOT_NULL -> fb.isNotNull().build();
            case IS_NULL -> fb.isNull().build();
            case LIST_CONTAINS -> fb.in(c.firstValue()).build();
        };
    }

    private static FeelExpression.FieldBuilder fieldBuilder(RuleCondition c) {
        var type = StringUtils.defaultIfBlank(c.type(), "string").toLowerCase();
        return switch (type) {
            case "number", "boolean" -> FeelExpression.field(c.field());
            case "date", "datetime" -> FeelExpression.dateField(c.field());
            default -> FeelExpression.stringField(c.field());
        };
    }

    private static Object typedValue(RuleCondition c, int index) {
        var val = index == 0 ? c.firstValue() : c.secondValue();
        if (StringUtils.isBlank(val))
            return null;
        return c.isNumber() ? Double.parseDouble(val) : val;
    }

    private static Number numValue(RuleCondition c) {
        var val = c.firstValue();
        return StringUtils.isNotBlank(val) ? Double.parseDouble(val) : 0;
    }
}
