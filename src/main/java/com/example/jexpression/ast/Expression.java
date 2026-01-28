package com.example.jexpression.ast;

/**
 * Sealed AST Node hierarchy for FEEL expressions.
 * Enforces exhaustiveness in Visitors.
 */
public sealed interface Expression
                permits Expression.Field, Expression.Literal, Expression.List, Expression.Unary, Expression.Binary {

        /**
         * Accept a visitor.
         * Kept for visitor pattern mechanics.
         */
        <R> R accept(ExpressionVisitor<R> visitor);

        /**
         * Process this expression with a visitor.
         * Alias for accept() to improve readability.
         */
        default <R> R processWith(ExpressionVisitor<R> visitor) {
                return accept(visitor);
        }

        /**
         * Infer the data type of this node.
         */
        DataType type();

        /**
         * Represents a field reference (e.g., "transaction.amount").
         */
        record Field(String name, DataType type) implements Expression {
                @Override
                public <R> R accept(ExpressionVisitor<R> visitor) {
                        return visitor.handleField(this);
                }
        }

        /**
         * Represents a literal value (e.g., 100, "Active").
         */
        record Literal(String rawValue, DataType type) implements Expression {
                @Override
                public <R> R accept(ExpressionVisitor<R> visitor) {
                        return visitor.handleLiteral(this);
                }
        }

        /**
         * Represents a list of values (e.g., [1, 2, 3]).
         */
        record List(java.util.List<Expression> elements, DataType type) implements Expression {
                @Override
                public <R> R accept(ExpressionVisitor<R> visitor) {
                        return visitor.handleList(this);
                }
        }

        /**
         * Represents a unary operation (e.g., not(x), lower case(x)).
         */
        record Unary(ExpressionOperator operator, Expression operand) implements Expression {
                @Override
                public <R> R accept(ExpressionVisitor<R> visitor) {
                        return visitor.handleUnary(this);
                }

                @Override
                public DataType type() {
                        // Logic operators return BOOLEAN, others depend on context
                        if (operator == ExpressionOperator.NOT)
                                return DataType.BOOLEAN;
                        return operand.type();
                }
        }

        /**
         * Represents a binary operation (e.g., x = y, x > y).
         */
        record Binary(ExpressionOperator operator, Expression left, Expression right) implements Expression {
                @Override
                public <R> R accept(ExpressionVisitor<R> visitor) {
                        return visitor.handleBinary(this);
                }

                @Override
                public DataType type() {
                        // Comparison/Logic returns BOOLEAN.
                        // Can be enhanced if we support arithmetic.
                        return DataType.BOOLEAN;
                }
        }
}
