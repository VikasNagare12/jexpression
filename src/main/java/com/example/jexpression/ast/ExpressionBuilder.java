package com.example.jexpression.ast;

import java.util.List;

/**
 * Factory for creating valid FEEL AST nodes.
 */
public final class ExpressionBuilder {

    private ExpressionBuilder() {
    }

    public static Expression.Field field(String name, DataType type) {
        return new Expression.Field(name, type);
    }

    public static Expression.Literal literal(String rawValue, DataType t) {
        return new Expression.Literal(rawValue, t);
    }

    public static Expression.List list(DataType t, List<String> rawValues) {
        if (rawValues == null) {
            return new Expression.List(List.of(), t);
        }
        List<Expression> elements = rawValues.stream()
                .map(v -> (Expression) new Expression.Literal(v, t))
                .toList();
        return new Expression.List(elements, t);
    }

    public static Expression.Binary equalTo(Expression l, Expression r) {
        return new Expression.Binary(ExpressionOperator.EQUALS, l, r);
    }

    public static Expression.Binary greaterThan(Expression l, Expression r) {
        return new Expression.Binary(ExpressionOperator.GREATER, l, r);
    }

    public static Expression.Binary greaterThanOrEqualTo(Expression l, Expression r) {
        return new Expression.Binary(ExpressionOperator.GREATER_OR_EQUAL, l, r);
    }

    public static Expression.Binary lessThan(Expression l, Expression r) {
        return new Expression.Binary(ExpressionOperator.LESS, l, r);
    }

    public static Expression.Binary lessThanOrEqualTo(Expression l, Expression r) {
        return new Expression.Binary(ExpressionOperator.LESS_OR_EQUAL, l, r);
    }

    public static Expression.Binary and(Expression l, Expression r) {
        return new Expression.Binary(ExpressionOperator.AND, l, r);
    }

    public static Expression.Binary or(Expression l, Expression r) {
        return new Expression.Binary(ExpressionOperator.OR, l, r);
    }

    public static Expression.Unary not(Expression n) {
        return new Expression.Unary(ExpressionOperator.NOT, n);
    }

    public static Expression.Unary lowerCase(Expression n) {
        return new Expression.Unary(ExpressionOperator.LOWER_CASE, n);
    }

    public static Expression.Binary inList(Expression l, Expression.List r) {
        return new Expression.Binary(ExpressionOperator.IN, l, r);
    }

    public static Expression.Binary contains(Expression l, Expression r) {
        return new Expression.Binary(ExpressionOperator.CONTAINS, l, r);
    }

    public static Expression.Binary startsWith(Expression l, Expression r) {
        return new Expression.Binary(ExpressionOperator.STARTS_WITH, l, r);
    }

    public static Expression.Binary endsWith(Expression l, Expression r) {
        return new Expression.Binary(ExpressionOperator.ENDS_WITH, l, r);
    }
}
