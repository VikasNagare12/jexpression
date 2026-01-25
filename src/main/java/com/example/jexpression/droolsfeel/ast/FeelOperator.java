package com.example.jexpression.droolsfeel.ast;

public enum FeelOperator {
    EQUALS("="),
    NOT_EQUALS("!="),
    GREATER(">"),
    GREATER_OR_EQUAL(">="),
    LESS("<"),
    LESS_OR_EQUAL("<="),
    AND("and"),
    OR("or"),
    NOT("not"),
    IN("in"),
    CONTAINS("contains"),
    STARTS_WITH("starts with"),
    ENDS_WITH("ends with"),
    LOWER_CASE("lower case");

    private final String feel;

    FeelOperator(String feel) {
        this.feel = feel;
    }

    public String feel() {
        return feel;
    }
}
