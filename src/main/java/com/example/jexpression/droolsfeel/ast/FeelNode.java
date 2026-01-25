package com.example.jexpression.droolsfeel.ast;

import java.util.List;

public sealed interface FeelNode
                permits FieldNode, LiteralNode, ListNode, BinaryNode, UnaryNode {
        <R> R accept(FeelVisitor<R> visitor);
}

record FieldNode(String name) implements FeelNode {
        @Override
        public <R> R accept(FeelVisitor<R> visitor) {
                return visitor.visit(this);
        }
}

record LiteralNode(Object value, DataType type) implements FeelNode {
        @Override
        public <R> R accept(FeelVisitor<R> visitor) {
                return visitor.visit(this);
        }
}

record ListNode(List<LiteralNode> values) implements FeelNode {
        @Override
        public <R> R accept(FeelVisitor<R> visitor) {
                return visitor.visit(this);
        }
}

record BinaryNode(
                FeelOperator operator,
                FeelNode left,
                FeelNode right) implements FeelNode {
        @Override
        public <R> R accept(FeelVisitor<R> visitor) {
                return visitor.visit(this);
        }
}

record UnaryNode(
                FeelOperator operator,
                FeelNode operand) implements FeelNode {
        @Override
        public <R> R accept(FeelVisitor<R> visitor) {
                return visitor.visit(this);
        }
}
