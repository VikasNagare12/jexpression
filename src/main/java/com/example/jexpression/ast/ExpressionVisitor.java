package com.example.jexpression.ast;

public interface ExpressionVisitor<R> {
    R handleField(Expression.Field expression);

    R handleLiteral(Expression.Literal expression);

    R handleList(Expression.List expression);

    R handleUnary(Expression.Unary expression);

    R handleBinary(Expression.Binary expression);
}
