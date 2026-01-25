package com.example.jexpression.ast;

import java.util.List;

public final class FeelAst {

    private FeelAst() {
    }

    public static FieldNode field(String name) {
        return new FieldNode(name);
    }

    public static LiteralNode value(Object v, DataType t) {
        return new LiteralNode(v, t);
    }

    public static ListNode list(DataType t, List<String> values) {
        if (values == null)
            return new ListNode(List.of());
        return new ListNode(
                values.stream()
                        .map(v -> new LiteralNode(t.parse(v), t))
                        .toList());
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
