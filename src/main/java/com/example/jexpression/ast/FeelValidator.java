package com.example.jexpression.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A visitor that validates the AST structure.
 * Returns a list of error messages (empty if valid).
 */
public class FeelValidator implements FeelVisitor<List<String>> {

    public static List<String> validate(FeelNode node) {
        return node.accept(new FeelValidator());
    }

    @Override
    public List<String> visit(FieldNode node) {
        if (node.name() == null || node.name().isBlank()) {
            return Collections.singletonList("Field name cannot be empty");
        }
        return Collections.emptyList();
    }

    @Override
    public List<String> visit(LiteralNode node) {
        if (node.type() == null) {
            return Collections.singletonList("Literal type cannot be null");
        }
        return Collections.emptyList();
    }

    @Override
    public List<String> visit(ListNode node) {
        List<String> errors = new ArrayList<>();
        for (LiteralNode n : node.values()) {
            errors.addAll(n.accept(this));
        }
        return errors;
    }

    @Override
    public List<String> visit(UnaryNode node) {
        if (node.operator() == null) {
            return Collections.singletonList("Unary operator cannot be null");
        }
        return node.operand().accept(this);
    }

    @Override
    public List<String> visit(BinaryNode node) {
        List<String> errors = new ArrayList<>();
        if (node.operator() == null) {
            errors.add("Binary operator cannot be null");
        }
        errors.addAll(node.left().accept(this));
        errors.addAll(node.right().accept(this));
        return errors;
    }
}
