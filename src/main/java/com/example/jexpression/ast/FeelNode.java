package com.example.jexpression.ast;

import java.util.List;

/**
 * Sealed AST Node hierarchy for FEEL expressions.
 * Enforces exhaustiveness in Visitors.
 */
public sealed interface FeelNode permits FieldNode, LiteralNode, ListNode, UnaryNode, BinaryNode {

        /**
         * Accept a visitor.
         */
        <R> R accept(FeelVisitor<R> visitor);

    /**
     * Infer the data type of this node.
     */
    DataType type();
}

/**
 * Represents a field reference (e.g., "transaction.amount").
 */
record FieldNode(String name) implements FeelNode {
        @Override
        public <R> R accept(FeelVisitor<R> visitor) {
                return visitor.visit(this);
        }

    @Override
    public DataType type() {
            return DataType.ANY; // Field types are resolved at runtime or metadata
    }
}

/**
 * Represents a literal value (e.g., 100, "Active").
 */
record LiteralNode(String rawValue, DataType type) implements FeelNode {
        @Override
        public <R> R accept(FeelVisitor<R> visitor) {
                return visitor.visit(this);
        }
}

/**
 * Represents a list of values (e.g., [1, 2, 3]).
 */
record ListNode(List<FeelNode> elements, DataType type) implements FeelNode {
        @Override
        public <R> R accept(FeelVisitor<R> visitor) {
                return visitor.visit(this);
        }
}

/**
 * Represents a unary operation (e.g., not(x), lower case(x)).
 */
record UnaryNode(FeelOperator operator, FeelNode operand) implements FeelNode {
        @Override
        public <R> R accept(FeelVisitor<R> visitor) {
                return visitor.visit(this);
        }

    @Override
    public DataType type() {
            // Logic operators return BOOLEAN, others depend on context
            if (operator == FeelOperator.NOT)
                    return DataType.BOOLEAN;
            return operand.type();
    }
}

/**
 * Represents a binary operation (e.g., x = y, x > y).
 */
record BinaryNode(FeelOperator operator, FeelNode left, FeelNode right) implements FeelNode {
        @Override
        public <R> R accept(FeelVisitor<R> visitor) {
                return visitor.visit(this);
        }

    @Override
    public DataType type() {
            // Comparison/Logic returns BOOLEAN.
            // Can be enhanced if we support arithmetic.
            return DataType.BOOLEAN;
    }
}
