package com.example.jexpression.droolsfeel.ast;

public enum FeelOperator {
    EQUALS("=", false),
    NOT_EQUALS("!=", false),
    GREATER(">", false),
    GREATER_OR_EQUAL(">=", false),
    LESS("<", false),
    LESS_OR_EQUAL("<=", false),
    AND("and", false),
    OR("or", false),
    NOT("not", true), // Unary function style? Or operator? NOT is usually operator "not(x)" or "not
                      // x". FEEL uses function style often.
    IN("in", false),
    CONTAINS("contains", true),
    STARTS_WITH("starts with", true),
    ENDS_WITH("ends with", true),
    LOWER_CASE("lower case", true);

    private final String feel;
    private final boolean isFunction;

    FeelOperator(String feel, boolean isFunction) {
        this.feel = feel;
        this.isFunction = isFunction;
    }

    public String feel() {
        return feel;
    }

    public boolean isFunction() {
        return isFunction;
    }
}
