package com.example.jexpression.ast;

import java.util.Set;

/**
 * FEEL Operators with rich metadata for validation and rendering.
 */
public enum FeelOperator {
    // Comparison
    EQUALS("=", Arity.BINARY, Set.of(DataType.ANY), false),
    NOT_EQUALS("!=", Arity.BINARY, Set.of(DataType.ANY), false),
    GREATER(">", Arity.BINARY, Set.of(DataType.NUMBER, DataType.DATE), false),
    GREATER_OR_EQUAL(">=", Arity.BINARY, Set.of(DataType.NUMBER, DataType.DATE), false),
    LESS("<", Arity.BINARY, Set.of(DataType.NUMBER, DataType.DATE), false),
    LESS_OR_EQUAL("<=", Arity.BINARY, Set.of(DataType.NUMBER, DataType.DATE), false),

    // Logic
    AND("and", Arity.BINARY, Set.of(DataType.BOOLEAN), false),
    OR("or", Arity.BINARY, Set.of(DataType.BOOLEAN), false),
    NOT("not", Arity.UNARY, Set.of(DataType.BOOLEAN), true), // Render as function: not(x)

    // Collection / String
    IN("in", Arity.BINARY, Set.of(DataType.ANY), false),
    CONTAINS("contains", Arity.BINARY, Set.of(DataType.STRING), true),
    STARTS_WITH("starts with", Arity.BINARY, Set.of(DataType.STRING), true),
    ENDS_WITH("ends with", Arity.BINARY, Set.of(DataType.STRING), true),
    LOWER_CASE("lower case", Arity.UNARY, Set.of(DataType.STRING), true);

    public enum Arity {
        UNARY, BINARY
    }

    private final String symbol;
    private final Arity arity;
    private final Set<DataType> supportedTypes;
    private final boolean isFunctionStyle;

    FeelOperator(String symbol, Arity arity, Set<DataType> supportedTypes, boolean isFunctionStyle) {
        this.symbol = symbol;
        this.arity = arity;
        this.supportedTypes = supportedTypes;
        this.isFunctionStyle = isFunctionStyle;
    }

    public String symbol() {
        return symbol;
    }

    public Arity arity() {
        return arity;
    }

    public boolean supports(DataType type) {
        return supportedTypes.contains(DataType.ANY) || supportedTypes.contains(type);
    }

    public boolean isFunctionStyle() {
        return isFunctionStyle;
    }
}
