package com.example.jexpression.droolsfeel.ast;

import java.util.stream.Collectors;

public final class FeelRenderer implements FeelVisitor<String> {

    private FeelRenderer() {
    }

    public static String render(FeelNode node) {
        return node.accept(new FeelRenderer());
    }

    @Override
    public String visit(FieldNode node) {
        return node.name();
    }

    @Override
    public String visit(LiteralNode node) {
        Object v = node.value();

        if (v == null)
            return "null";
        if (v instanceof Number || v instanceof Boolean)
            return v.toString();

        if (node.type() == DataType.DATE) {
            return "date(\"" + v + "\")";
        }

        return "\"" + escape(v.toString()) + "\"";
    }

    @Override
    public String visit(ListNode node) {
        return "[" + node.values().stream()
                .map(n -> n.accept(this))
                .collect(Collectors.joining(", ")) + "]";
    }

    @Override
    public String visit(UnaryNode node) {
        return node.operator().feel() + "(" + node.operand().accept(this) + ")";
    }

    @Override
    public String visit(BinaryNode node) {
        if (node.operator() == FeelOperator.CONTAINS
                || node.operator() == FeelOperator.STARTS_WITH
                || node.operator() == FeelOperator.ENDS_WITH) {

            return node.operator().feel() + "(" +
                    node.left().accept(this) + ", " +
                    node.right().accept(this) + ")";
        }

        return node.left().accept(this) +
                " " + node.operator().feel() +
                " " + node.right().accept(this);
    }

    private String escape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
