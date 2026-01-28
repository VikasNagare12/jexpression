package com.example.jexpression.mapper;

import com.example.jexpression.ast.DataType;
import com.example.jexpression.ast.ExpressionOperator;
import com.example.jexpression.model.RuleCondition;

import java.util.List;

/**
 * Thin orchestrator that converts RuleCondition to FEEL expression.
 * Delegates all logic to DataType and ExpressionOperator.
 */
public final class RuleConditionMapper {

    private RuleConditionMapper() {}

    /**
     * Convert a RuleCondition to its FEEL expression string.
     */
    public static String toFeel(RuleCondition condition) {
        String field = condition.field();
        DataType type = DataType.from(condition.type());
        ExpressionOperator operator = ExpressionOperator.from(condition.op());
        List<String> values = prepareValues(condition.values(), type);

        // For string comparisons, wrap field in lower case
        if (type == DataType.STRING) {
            field = "lower case(" + field + ")";
        }

        return operator.toFeel(field, values, type);
    }

    /**
     * Prepare values (lowercase for strings).
     */
    private static List<String> prepareValues(List<String> values, DataType type) {
        if (values == null || type != DataType.STRING) return values;
        return values.stream()
                .map(v -> v == null ? null : v.toLowerCase())
                .toList();
    }
}
