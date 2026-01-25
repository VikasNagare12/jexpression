package com.example.jexpression.droolsfeel.ast;

public interface FeelVisitor<R> {
    R visit(FieldNode node);
    R visit(LiteralNode node);
    R visit(ListNode node);
    R visit(BinaryNode node);
    R visit(UnaryNode node);
}
