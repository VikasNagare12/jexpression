package com.example.jexpression.ast;

import java.util.stream.Collectors;

/**
 * Renders AST to valid FEEL output string.
 * Logic is strict: no semantic decisions, just traversal and formatting.
 */
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
        // Delegate formatting to the type definition
        return node.type().format(node.rawValue());
    }

    @Override
    public String visit(ListNode node) {
        return "[" + node.elements().stream()
                .map(n -> n.accept(this))
                .collect(Collectors.joining(", ")) + "]";
    }

    @Override
    public String visit(UnaryNode node) {
        if (node.operator().isFunctionStyle()) {
            return node.operator().symbol() + "(" + node.operand().accept(this) + ")";
        }
        // Operator style (e.g. "not x" - though FEEL prefers function style usually)
        return node.operator().symbol() + " " + node.operand().accept(this);
    }

    @Override
    public String visit(BinaryNode node) {
        if (node.operator().isFunctionStyle()) {
            return node.operator().symbol() + "(" +
                    node.left().accept(this) + ", " +
                    node.right().accept(this) + ")";
        }

        return node.left().accept(this) +
                " " + node.operator().symbol() +
                " " + node.right().accept(this);
    }
}
