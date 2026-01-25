package com.example.jexpression.mapper;

import com.example.jexpression.ast.DataType;
import com.example.jexpression.ast.FeelAst;
import com.example.jexpression.ast.FeelNode;
import com.example.jexpression.ast.FeelRenderer;
import com.example.jexpression.model.RuleCondition;

import java.util.List;

public final class RuleConditionMapper {

    private RuleConditionMapper() {
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
        DataType type;
        try {
            type = DataType.valueOf(c.type() == null ? "STRING" : c.type().toUpperCase());
        } catch (IllegalArgumentException e) {
            // "Error porn" avoidance: graceful degradation
            type = DataType.STRING;
        }
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
        // Validate or normalize operator if needed
        String normalizedOp = op.trim().toUpperCase();

        return switch (normalizedOp) {
            case "EQUALS" -> FeelAst.eq(fieldNode, FeelAst.value(parse(first(values), type), type));
            case "NOTEQUALS" -> FeelAst.not(FeelAst.eq(fieldNode, FeelAst.value(parse(first(values), type), type)));
            case "GREATER" -> FeelAst.gt(fieldNode, FeelAst.value(parse(first(values), type), type));
            case "GREATEROREQUAL" -> FeelAst.gte(fieldNode, FeelAst.value(parse(first(values), type), type));
            case "LESS" -> FeelAst.lt(fieldNode, FeelAst.value(parse(first(values), type), type));
            case "LESSOREQUAL" -> FeelAst.lte(fieldNode, FeelAst.value(parse(first(values), type), type));

            case "BETWEEN" -> {
                FeelNode val1 = FeelAst.value(parse(first(values), type), type);
                FeelNode val2 = FeelAst.value(parse(second(values), type), type);
                yield FeelAst.and(FeelAst.gte(fieldNode, val1), FeelAst.lte(fieldNode, val2));
            }

            case "IN" -> FeelAst.in(fieldNode, FeelAst.list(type, values));
            case "NOTIN" -> FeelAst.not(FeelAst.in(fieldNode, FeelAst.list(type, values)));

            case "CONTAINS" -> FeelAst.contains(fieldNode, FeelAst.value(parse(first(values), type), type));
            case "STARTSWITH" -> FeelAst.startsWith(fieldNode, FeelAst.value(parse(first(values), type), type));
            case "ENDSWITH" -> FeelAst.endsWith(fieldNode, FeelAst.value(parse(first(values), type), type));

            case "ISNULL" -> FeelAst.eq(fieldNode, FeelAst.value(null, type));
            case "ISNOTNULL", "EXISTS" -> FeelAst.not(FeelAst.eq(fieldNode, FeelAst.value(null, type)));

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
