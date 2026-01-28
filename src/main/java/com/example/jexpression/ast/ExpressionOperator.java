package com.example.jexpression.ast;

import java.util.List;

/**
 * FEEL operators with self-contained expression generation.
 * Each operator knows how to render itself to FEEL syntax.
 */
public enum ExpressionOperator {

    EQUALS {
        @Override
        public String toFeel(String field, List<String> values, DataType type) {
            return field + " = " + type.format(first(values));
        }
    },
    NOT_EQUALS {
        @Override
        public String toFeel(String field, List<String> values, DataType type) {
            return field + " != " + type.format(first(values));
        }
    },
    GREATER {
        @Override
        public String toFeel(String field, List<String> values, DataType type) {
            return field + " > " + type.format(first(values));
        }
    },
    GREATER_OR_EQUAL {
        @Override
        public String toFeel(String field, List<String> values, DataType type) {
            return field + " >= " + type.format(first(values));
        }
    },
    LESS {
        @Override
        public String toFeel(String field, List<String> values, DataType type) {
            return field + " < " + type.format(first(values));
        }
    },
    LESS_OR_EQUAL {
        @Override
        public String toFeel(String field, List<String> values, DataType type) {
            return field + " <= " + type.format(first(values));
        }
    },
    BETWEEN {
        @Override
        public String toFeel(String field, List<String> values, DataType type) {
            String v1 = type.format(first(values));
            String v2 = type.format(second(values));
            return field + " >= " + v1 + " and " + field + " <= " + v2;
        }
    },
    IN {
        @Override
        public String toFeel(String field, List<String> values, DataType type) {
            return field + " in " + type.formatList(values);
        }
    },
    NOT_IN {
        @Override
        public String toFeel(String field, List<String> values, DataType type) {
            return "not(" + field + " in " + type.formatList(values) + ")";
        }
    },
    CONTAINS {
        @Override
        public String toFeel(String field, List<String> values, DataType type) {
            return "contains(" + field + ", " + type.format(first(values)) + ")";
        }
    },
    STARTS_WITH {
        @Override
        public String toFeel(String field, List<String> values, DataType type) {
            return "starts with(" + field + ", " + type.format(first(values)) + ")";
        }
    },
    ENDS_WITH {
        @Override
        public String toFeel(String field, List<String> values, DataType type) {
            return "ends with(" + field + ", " + type.format(first(values)) + ")";
        }
    },
    IS_NULL {
        @Override
        public String toFeel(String field, List<String> values, DataType type) {
            return field + " = null";
        }
    },
    IS_NOT_NULL {
        @Override
        public String toFeel(String field, List<String> values, DataType type) {
            return field + " != null";
        }
    };

    /**
     * Generate FEEL expression for this operator.
     */
    public abstract String toFeel(String field, List<String> values, DataType type);

    /**
     * Parse operator string to enum.
     */
    public static ExpressionOperator from(String op) {
        if (op == null)
            throw new IllegalArgumentException("Operator cannot be null");

        return switch (op.trim().toUpperCase()) {
            case "EQUALS" -> EQUALS;
            case "NOTEQUALS" -> NOT_EQUALS;
            case "GREATER" -> GREATER;
            case "GREATEROREQUAL" -> GREATER_OR_EQUAL;
            case "LESS" -> LESS;
            case "LESSOREQUAL" -> LESS_OR_EQUAL;
            case "BETWEEN" -> BETWEEN;
            case "IN" -> IN;
            case "NOTIN" -> NOT_IN;
            case "CONTAINS" -> CONTAINS;
            case "STARTSWITH" -> STARTS_WITH;
            case "ENDSWITH" -> ENDS_WITH;
            case "ISNULL" -> IS_NULL;
            case "ISNOTNULL", "EXISTS" -> IS_NOT_NULL;
            default -> throw new IllegalArgumentException("Unknown operator: " + op);
        };
    }

    // ─────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────

    protected static String first(List<String> values) {
        return (values != null && !values.isEmpty()) ? values.get(0) : null;
    }

    protected static String second(List<String> values) {
        return (values != null && values.size() > 1) ? values.get(1) : null;
    }
}
