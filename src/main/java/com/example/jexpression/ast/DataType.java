package com.example.jexpression.ast;

/**
 * Supported data types in the FEEL engine with centralized formatting logic.
 */
public enum DataType {
    STRING {
        @Override
        public String formatLiteral(Object val) {
            return quote(String.valueOf(val));
        }
    },
    NUMBER {
        @Override
        public String formatLiteral(Object val) {
            // Numbers are raw (no quotes)
            return String.valueOf(val);
        }
    },
    BOOLEAN {
        @Override
        public String formatLiteral(Object val) {
            // Booleans are raw
            return String.valueOf(val);
        }
    },
    DATE {
        @Override
        public String formatLiteral(Object val) {
            // Dates are wrapped: date("2023-01-01")
            return "date(" + quote(String.valueOf(val)) + ")";
        }
    },
    ANY {
        @Override
        public String formatLiteral(Object val) {
            return quote(String.valueOf(val));
        }
    };

    /**
     * Format a raw value into a valid FEEL syntax string literal.
     * e.g. "foo" -> "\"foo\""
     * 100 -> "100"
     */
    public abstract String formatLiteral(Object val);

    private static String quote(String s) {
        if (s == null || "null".equals(s))
            return "null";
        // Escape quotes and wrap
        return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }
}
