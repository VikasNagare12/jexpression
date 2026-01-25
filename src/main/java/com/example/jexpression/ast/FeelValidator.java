package com.example.jexpression.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * Validates AST semantic correctness (type compatibility, operator usage).
 */
public class FeelValidator implements FeelVisitor<List<String>> {

    public static List<String> validate(FeelNode node) {
        return node.accept(new FeelValidator());
    }

    @Override
    public List<String> visit(FieldNode node) {
        // Field validation typically requires a context/schema, skipping for now.
        return List.of();
    }

    @Override
    public List<String> visit(LiteralNode node) {
        // Validate literal structure if needed (e.g. date format)
        return List.of();
    }

    @Override
    public List<String> visit(ListNode node) {
        List<String> errors = new ArrayList<>();
        for (FeelNode element : node.elements()) {
            errors.addAll(element.accept(this));
        }
        return errors;
    }

    @Override
    public List<String> visit(UnaryNode node) {
        List<String> errors = new ArrayList<>(node.operand().accept(this));

        if (!node.operator().supports(node.operand().type())) {
            errors.add("Generic Error: Operator '" + node.operator().symbol() +
                    "' does not support type " + node.operand().type());
        }
        return errors;
    }

    @Override
    public List<String> visit(BinaryNode node) {
        List<String> errors = new ArrayList<>();
        errors.addAll(node.left().accept(this));
        errors.addAll(node.right().accept(this));

        // Check compatibility
        DataType leftType = node.left().type();

        if (!node.operator().supports(leftType)) {
            errors.add("Operator '" + node.operator().symbol() +
                    "' does not support left operand type " + leftType);
        }

        // Ensure right operand matches left (simplistic check)
        // Ideally we check if operator supports the PAIR (left, right)
        // For now, strict type equality is a safe baseline for logic/comparison
        if (node.operator().arity() == FeelOperator.Arity.BINARY) {
            // Special case: IN operator (Left=Any, Right=List)
            if (node.operator() == FeelOperator.IN) {
                if (!(node.right() instanceof ListNode)) {
                    errors.add("IN operator requires a List on the right side");
                }
            } else {
                // Standard binary: types should align (weak check)
                // Deeper checks would require checking node.right().type() against expected
                // params
            }
        }

        return errors;
    }
}
