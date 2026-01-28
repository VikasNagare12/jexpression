package com.example.jexpression.ast;

/**
 * Data types with centralized literal formatting for FEEL expressions.
 */
public enum DataType {
    STRING {
        @Override
        public String formatLiteral(String value) {
            if (value == null)
                return "null";
            return "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
        }
    },
    NUMBER {
        @Override
        public String formatLiteral(String value) {
            return value; // Numbers are raw
        }
    },
    BOOLEAN {
        @Override
        public String formatLiteral(String value) {
            return value; // true/false raw
        }
    },
    DATE {
        @Override
        public String formatLiteral(String value) {
            if (value == null)
                return "null";
            return "date(\"" + value + "\")";
        }
    };

    /**
     * Format a raw value into valid FEEL syntax.
     */
    public abstract String formatLiteral(String value);

    /**
     * Format a list of values into FEEL list syntax: [v1, v2, v3]
     */
    public String formatLiteralList(java.util.List<String> values) {
        if (values == null || values.isEmpty())
            return "[]";
        return "[" + values.stream()
                .map(this::formatLiteral)
                .collect(java.util.stream.Collectors.joining(", ")) + "]";
    }

    /**
     * Parse type string to enum, defaults to STRING.
     */
    public static DataType from(String type) {
        if (type == null)
            return STRING;
        try {
            return valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            return STRING;
        }
    }
}
