package com.example.jexpression.ast;

/**
 * Generic Visitor for FEEL AST nodes.
 *
 * @param <R> Return type of the visit operation.
 */
public interface FeelVisitor<R> {
    R visit(FieldNode node);

    R visit(LiteralNode node);
    R visit(ListNode node);

    R visit(UnaryNode node);

    R visit(BinaryNode node);
}
