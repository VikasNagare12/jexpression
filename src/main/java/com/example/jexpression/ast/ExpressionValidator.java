package com.example.jexpression.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * Validates AST semantic correctness (type compatibility, operator usage).
 */
public class ExpressionValidator implements ExpressionVisitor<List<String>> {

    public static List<String> validateExpression(Expression expression) {
        return expression.processWith(new ExpressionValidator());
    }

    @Override
    public List<String> handleField(Expression.Field expression) {
        // Field validation typically requires a context/schema, skipping for now.
        return List.of();
    }

    @Override
    public List<String> handleLiteral(Expression.Literal expression) {
        // Validate literal structure if needed (e.g. date format)
        return List.of();
    }

    @Override
    public List<String> handleList(Expression.List expression) {
        List<String> errors = new ArrayList<>();
        for (Expression element : expression.elements()) {
            errors.addAll(element.processWith(this));
        }
        return errors;
    }

    @Override
    public List<String> handleUnary(Expression.Unary expression) {
        List<String> errors = new ArrayList<>(expression.operand().processWith(this));

        if (!expression.operator().isSupportedFor(expression.operand().type())) {
            errors.add("Generic Error: Operator '" + expression.operator().symbol() +
                    "' does not support type " + expression.operand().type());
        }
        return errors;
    }

    @Override
    public List<String> handleBinary(Expression.Binary expression) {
        List<String> errors = new ArrayList<>();
        errors.addAll(expression.left().processWith(this));
        errors.addAll(expression.right().processWith(this));

        // Check compatibility
        DataType leftType = expression.left().type();

        if (!expression.operator().isSupportedFor(leftType)) {
            errors.add("Operator '" + expression.operator().symbol() +
                    "' does not support left operand type " + leftType);
        }

        // Special case: IN operator (Left=Any, Right=List)
        if (expression.operator() == ExpressionOperator.IN) {
            if (!(expression.right() instanceof Expression.List)) {
                errors.add("IN operator requires a List on the right side");
            } else if (expression.left().type() != expression.right().type()) {
                errors.add("Type mismatch: Cannot check if " + expression.left().type() + " is in List<"
                        + expression.right().type() + ">");
            }
        } else {
            // Strict type equality for all other binary operators
            DataType rightType = expression.right().type();
            if (leftType != rightType) {
                errors.add("Type mismatch: Operator '" + expression.operator().symbol() +
                        "' cannot compare " + leftType + " with " + rightType);
            }
        }

        return errors;
    }
}
