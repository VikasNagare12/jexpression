package com.example.jexpression.droolsfeel.model;

/**
 * Supported FEEL operators for validation expressions.
 * 
 * <p>Maps JSON operator names to FEEL expression syntax.
 * Based on Drools FEEL specification with extensions.
 */
public enum FeelOperator {
    
    // Comparison Operators
    EQUALS("Equals", "="),
    NOT_EQUALS("NotEquals", "!="),
    GREATER("Greater", ">"),
    GREATER_OR_EQUAL("GreaterOrEqual", ">="),
    LESS("Less", "<"),
    LESS_OR_EQUAL("LessOrEqual", "<="),
    
    // Range Operators
    BETWEEN("Between", null),  // Requires custom handling: field >= a and field <= b
    IN("In", "in"),
    NOT_IN("NotIn", null),     // Requires custom handling: not(field in [...])
    
    // String Operators
    CONTAINS("Contains", null),       // contains(field, value)
    STARTS_WITH("StartsWith", null),  // starts with(field, value)
    ENDS_WITH("EndsWith", null),      // ends with(field, value)
    MATCHES("Matches", null),         // matches(field, pattern)
    
    // Null Operators
    EXISTS("Exists", "!="),           // field != null
    IS_NULL("IsNull", "="),           // field = null
    IS_NOT_NULL("IsNotNull", "!="),   // field != null
    
    // List Operators
    LIST_CONTAINS("ListContains", null); // list contains(field, value)
    
    private final String jsonName;
    private final String feelSymbol;
    
    FeelOperator(String jsonName, String feelSymbol) {
        this.jsonName = jsonName;
        this.feelSymbol = feelSymbol;
    }
    
    public String getJsonName() {
        return jsonName;
    }
    
    public String getFeelSymbol() {
        return feelSymbol;
    }
    
    /**
     * Lookup operator by JSON name.
     */
    public static FeelOperator fromJsonName(String name) {
        for (FeelOperator op : values()) {
            if (op.jsonName.equalsIgnoreCase(name)) {
                return op;
            }
        }
        throw new IllegalArgumentException("Unknown operator: " + name);
    }
    
    /**
     * Check if operator requires custom expression building.
     */
    public boolean requiresCustomHandling() {
        return feelSymbol == null;
    }
    
    /**
     * Check if this is a simple binary comparison operator.
     */
    public boolean isSimpleComparison() {
        return switch (this) {
            case EQUALS, NOT_EQUALS, GREATER, GREATER_OR_EQUAL, LESS, LESS_OR_EQUAL -> true;
            default -> false;
        };
    }
}
