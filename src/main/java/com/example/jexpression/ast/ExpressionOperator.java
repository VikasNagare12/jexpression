package com.example.jexpression.ast;

import java.util.function.Predicate;

/**
 * FEEL Operators with rich metadata for validation and rendering.
 * Refactored to use behavioral type validation instead of collection-based
 * checks.
 */
public enum ExpressionOperator {
    // Comparison
    EQUALS("=", TypeRules::isComparable, false),
    NOT_EQUALS("!=", TypeRules::isComparable, false),
    GREATER(">", TypeRules::isOrdered, false),
    GREATER_OR_EQUAL(">=", TypeRules::isOrdered, false),
    LESS("<", TypeRules::isOrdered, false),
    LESS_OR_EQUAL("<=", TypeRules::isOrdered, false),

    // Logic
    AND("and", TypeRules::isBoolean, false),
    OR("or", TypeRules::isBoolean, false),
    NOT("not", TypeRules::isBoolean, true), // Render as function: not(x)

    // Collection / String
    IN("in", TypeRules::allowAll, false),
    CONTAINS("contains", TypeRules::isString, true),
    STARTS_WITH("starts with", TypeRules::isString, true),
    ENDS_WITH("ends with", TypeRules::isString, true),
    LOWER_CASE("lower case", TypeRules::isString, true);

    private final String symbol;
    private final Predicate<DataType> typeValidator;
    private final boolean isFunctionStyle;

    ExpressionOperator(String symbol, Predicate<DataType> typeValidator, boolean isFunctionStyle) {
        this.symbol = symbol;
        this.typeValidator = typeValidator;
        this.isFunctionStyle = isFunctionStyle;
    }

    public String symbol() {
        return symbol;
    }

    public boolean isSupportedFor(DataType type) {
        return typeValidator.test(type);
    }

    public boolean isFunctionStyle() {
        return isFunctionStyle;
    }

    /**
     * Render the operator expression.
     */
    public String toFeelExpression(String left, String right) {
        if (isFunctionStyle) {
            // Function call style: op(left, right) or op(left)
            if (right == null) {
                return symbol + "(" + left + ")"; // Unary function
            }
            return symbol + "(" + left + ", " + right + ")"; // Binary function
        } else {
            // Infix style: left op right
            if (right == null) {
                return symbol + " " + left; // Unary prefix (Rare in FEEL, mostly 'not' which is function style usually
                                            // but here defined as function style)
            }
            return left + " " + symbol + " " + right;
        }
    }

    /**
     * Centralized type compatibility rules.
     * Replaces Sets and Wildcards with explicit, readable behavior.
     */
    private static class TypeRules {
        // Any non-void/non-unknown type is generally comparable for equality
        static boolean isComparable(DataType t) {
            return t != DataType.ANY;
        }

        static boolean isOrdered(DataType t) {
            return t == DataType.NUMBER || t == DataType.DATE;
        }

        static boolean isBoolean(DataType t) {
            return t == DataType.BOOLEAN;
        }

        static boolean isString(DataType t) {
            return t == DataType.STRING;
        }

        static boolean allowAll(DataType t) {
            return true;
        }
    }
}
