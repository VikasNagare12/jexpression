package com.example.jexpression.ast;

import java.util.stream.Collectors;

/**
 * Renders AST to FEEL expression strings using the Visitor pattern.
 */
public class ExpressionToFeelRenderer implements ExpressionVisitor<String> {

    public static String toFeel(Expression expression) {
        return expression.processWith(new ExpressionToFeelRenderer());
    }

    @Override
    public String handleField(Expression.Field expression) {
        return expression.name();
    }

    @Override
    public String handleLiteral(Expression.Literal expression) {
        return expression.type().formatLiteral(expression.rawValue());
    }

    @Override
    public String handleList(Expression.List expression) {
        String elements = expression.elements().stream()
                .map(e -> e.processWith(this))
                .collect(Collectors.joining(", "));
        return "[" + elements + "]";
    }

    @Override
    public String handleUnary(Expression.Unary expression) {
        String operand = expression.operand().processWith(this);
        return expression.operator().toFeelExpression(operand, null);
    }

    @Override
    public String handleBinary(Expression.Binary expression) {
        String left = expression.left().processWith(this);
        String right = expression.right().processWith(this);
        return expression.operator().toFeelExpression(left, right);
    }
}
