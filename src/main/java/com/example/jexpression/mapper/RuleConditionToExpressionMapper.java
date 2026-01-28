package com.example.jexpression.mapper;

import com.example.jexpression.ast.DataType;
import com.example.jexpression.ast.ExpressionBuilder;
import com.example.jexpression.ast.Expression;
import com.example.jexpression.ast.ExpressionToFeelRenderer;
import com.example.jexpression.model.RuleCondition;

import java.util.List;

public final class RuleConditionToExpressionMapper {

    private RuleConditionToExpressionMapper() {
    }

    /**
     * Convert RuleCondition to FEEL expression string using AST.
     */
    public static String toFeel(RuleCondition c) {
        Expression node = mapToExpression(c);
        return ExpressionToFeelRenderer.toFeel(node);
    }

    public static Expression mapToExpression(RuleCondition c) {
        String field = c.field();
        String op = c.op();
        DataType type;
        try {
            type = DataType.valueOf(c.type() == null ? "STRING" : c.type().toUpperCase());
        } catch (IllegalArgumentException e) {
            type = DataType.STRING;
        }
        List<String> values = c.values();

        // Handle Lower Case logic for Strings
        if (type == DataType.STRING) {
            // Apply lower case function to field
            Expression fieldNode = ExpressionBuilder.lowerCase(ExpressionBuilder.field(field, type));

            // Lower case values
            values = values == null ? null
                    : values.stream()
                            .map(v -> v == null ? null : v.toLowerCase())
                            .toList();

            return buildExpressionInternal(op, type, fieldNode, values);
        }

        return buildExpressionInternal(op, type, ExpressionBuilder.field(field, type), values);
    }

    private static Expression buildExpressionInternal(String op, DataType type, Expression fieldNode,
            List<String> values) {
        // Validate or normalize operator if needed
        String normalizedOp = op.trim().toUpperCase();

        return switch (normalizedOp) {
            case "EQUALS" -> ExpressionBuilder.equalTo(fieldNode, ExpressionBuilder.literal(first(values), type));
            case "NOTEQUALS" -> ExpressionBuilder
                    .not(ExpressionBuilder.equalTo(fieldNode, ExpressionBuilder.literal(first(values), type)));
            case "GREATER" -> ExpressionBuilder.greaterThan(fieldNode, ExpressionBuilder.literal(first(values), type));
            case "GREATEROREQUAL" ->
                ExpressionBuilder.greaterThanOrEqualTo(fieldNode, ExpressionBuilder.literal(first(values), type));
            case "LESS" -> ExpressionBuilder.lessThan(fieldNode, ExpressionBuilder.literal(first(values), type));
            case "LESSOREQUAL" ->
                ExpressionBuilder.lessThanOrEqualTo(fieldNode, ExpressionBuilder.literal(first(values), type));

            case "BETWEEN" -> {
                Expression val1 = ExpressionBuilder.literal(first(values), type);
                Expression val2 = ExpressionBuilder.literal(second(values), type);
                yield ExpressionBuilder.and(ExpressionBuilder.greaterThanOrEqualTo(fieldNode, val1),
                        ExpressionBuilder.lessThanOrEqualTo(fieldNode, val2));
            }

            case "IN" -> ExpressionBuilder.inList(fieldNode, ExpressionBuilder.list(type, values));
            case "NOTIN" ->
                ExpressionBuilder.not(ExpressionBuilder.inList(fieldNode, ExpressionBuilder.list(type, values)));

            case "CONTAINS" -> ExpressionBuilder.contains(fieldNode, ExpressionBuilder.literal(first(values), type));
            case "STARTSWITH" ->
                ExpressionBuilder.startsWith(fieldNode, ExpressionBuilder.literal(first(values), type));
            case "ENDSWITH" -> ExpressionBuilder.endsWith(fieldNode, ExpressionBuilder.literal(first(values), type));

            case "ISNULL" -> ExpressionBuilder.equalTo(fieldNode, ExpressionBuilder.literal(null, type));
            case "ISNOTNULL", "EXISTS" ->
                ExpressionBuilder.not(ExpressionBuilder.equalTo(fieldNode, ExpressionBuilder.literal(null, type)));

            default -> throw new IllegalArgumentException("Unsupported operator: " + op);
        };
    }

    private static String first(List<String> values) {
        return (values != null && !values.isEmpty()) ? values.get(0) : null;
    }

    private static String second(List<String> values) {
        return (values != null && values.size() > 1) ? values.get(1) : null;
    }
}
