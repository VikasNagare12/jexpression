package com.example.jexpression.ast;

import java.util.List;

/**
 * Factory for creating valid FEEL AST nodes.
 */
public final class FeelAst {

    private FeelAst() {
    }

    public static FieldNode field(String name) {
        return new FieldNode(name);
    }

    public static LiteralNode value(String rawValue, DataType t) {
        return new LiteralNode(rawValue, t);
    }

    public static ListNode list(DataType t, List<String> rawValues) {
        if (rawValues == null) {
            return new ListNode(List.of(), t);
        }
        List<FeelNode> elements = rawValues.stream()
                .map(v -> (FeelNode) new LiteralNode(v, t))
                .toList();
        return new ListNode(elements, t);
    }

    public static BinaryNode eq(FeelNode l, FeelNode r) {
        return new BinaryNode(FeelOperator.EQUALS, l, r);
    }

    public static BinaryNode gt(FeelNode l, FeelNode r) {
        return new BinaryNode(FeelOperator.GREATER, l, r);
    }

    public static BinaryNode gte(FeelNode l, FeelNode r) {
        return new BinaryNode(FeelOperator.GREATER_OR_EQUAL, l, r);
    }

    public static BinaryNode lt(FeelNode l, FeelNode r) {
        return new BinaryNode(FeelOperator.LESS, l, r);
    }

    public static BinaryNode lte(FeelNode l, FeelNode r) {
        return new BinaryNode(FeelOperator.LESS_OR_EQUAL, l, r);
    }

    public static BinaryNode and(FeelNode l, FeelNode r) {
        return new BinaryNode(FeelOperator.AND, l, r);
    }

    public static BinaryNode or(FeelNode l, FeelNode r) {
        return new BinaryNode(FeelOperator.OR, l, r);
    }

    public static UnaryNode not(FeelNode n) {
        return new UnaryNode(FeelOperator.NOT, n);
    }

    public static UnaryNode lowerCase(FeelNode n) {
        return new UnaryNode(FeelOperator.LOWER_CASE, n);
    }

    public static BinaryNode in(FeelNode l, ListNode r) {
        return new BinaryNode(FeelOperator.IN, l, r);
    }

    public static BinaryNode contains(FeelNode l, FeelNode r) {
        return new BinaryNode(FeelOperator.CONTAINS, l, r);
    }

    public static BinaryNode startsWith(FeelNode l, FeelNode r) {
        return new BinaryNode(FeelOperator.STARTS_WITH, l, r);
    }

    public static BinaryNode endsWith(FeelNode l, FeelNode r) {
        return new BinaryNode(FeelOperator.ENDS_WITH, l, r);
    }
}
