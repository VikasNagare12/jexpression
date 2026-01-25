package com.example.jexpression.droolsfeel.converter;

import com.example.jexpression.droolsfeel.ast.DataType;
import com.example.jexpression.droolsfeel.ast.FeelAst;
import com.example.jexpression.droolsfeel.ast.FeelNode;
import com.example.jexpression.droolsfeel.ast.FeelRenderer;
import com.example.jexpression.droolsfeel.model.RuleCondition;

import java.util.List;

public final class FeelAstExpressionBuilder {

    private FeelAstExpressionBuilder() {
    }

    /**
     * Convert RuleCondition to FEEL expression string using AST.
     */
    public static String toFeel(RuleCondition c) {
        FeelNode node = buildAst(c);
        return FeelRenderer.render(node);
    }

    public static FeelNode buildAst(RuleCondition c) {
        String field = c.field();
        String op = c.op();
        DataType type = DataType.valueOf(c.type().toUpperCase());
        List<String> values = c.values();

        // Handle Lower Case logic for Strings
        if (type == DataType.STRING) {
            // Apply lower case function to field
            FeelNode fieldNode = FeelAst.lowerCase(FeelAst.field(field));

            // Lower case values
            values = values == null ? null
                    : values.stream()
                            .map(v -> v == null ? null : v.toLowerCase())
                            .toList();

            return buildAstInternal(op, type, fieldNode, values);
        }

        return buildAstInternal(op, type, FeelAst.field(field), values);
    }

    private static FeelNode buildAstInternal(String op, DataType type, FeelNode fieldNode, List<String> values) {
        return switch (op) {
            case "Equals" -> FeelAst.eq(fieldNode, FeelAst.value(parse(first(values), type), type));
            case "NotEquals" -> FeelAst.not(FeelAst.eq(fieldNode, FeelAst.value(parse(first(values), type), type)));
            case "Greater" -> FeelAst.gt(fieldNode, FeelAst.value(parse(first(values), type), type));
            case "GreaterOrEqual" -> FeelAst.gte(fieldNode, FeelAst.value(parse(first(values), type), type));
            case "Less" -> FeelAst.lt(fieldNode, FeelAst.value(parse(first(values), type), type));
            case "LessOrEqual" -> FeelAst.lte(fieldNode, FeelAst.value(parse(first(values), type), type));

            case "Between" -> {
                FeelNode val1 = FeelAst.value(parse(first(values), type), type);
                FeelNode val2 = FeelAst.value(parse(second(values), type), type);
                yield FeelAst.and(FeelAst.gte(fieldNode, val1), FeelAst.lte(fieldNode, val2));
            }

            case "In" -> FeelAst.in(fieldNode, FeelAst.list(type, values));
            case "NotIn" -> FeelAst.not(FeelAst.in(fieldNode, FeelAst.list(type, values)));

            case "Contains" -> FeelAst.contains(fieldNode, FeelAst.value(parse(first(values), type), type));
            case "StartsWith" -> FeelAst.startsWith(fieldNode, FeelAst.value(parse(first(values), type), type));
            case "EndsWith" -> FeelAst.endsWith(fieldNode, FeelAst.value(parse(first(values), type), type));

            case "IsNull" -> FeelAst.eq(fieldNode, FeelAst.value(null, type));
            case "IsNotNull", "Exists" -> FeelAst.not(FeelAst.eq(fieldNode, FeelAst.value(null, type)));

            default -> throw new IllegalArgumentException("Unsupported operator: " + op);
        };
    }

    private static Object parse(String v, DataType type) {
        return type.parse(v);
    }

    private static String first(List<String> values) {
        return (values != null && !values.isEmpty()) ? values.get(0) : null;
    }

    private static String second(List<String> values) {
        return (values != null && values.size() > 1) ? values.get(1) : null;
    }
}
