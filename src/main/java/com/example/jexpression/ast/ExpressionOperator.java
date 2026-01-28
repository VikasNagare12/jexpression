package com.example.jexpression.ast;

import java.util.List;

/**
 * FEEL operators with self-contained expression generation.
 * Each operator knows how to render itself to FEEL syntax.
 */
public enum ExpressionOperator {

    EQUALS {
        @Override
        public String buildFeelExpression(String field, List<String> values, DataType type) {
            requireValues(values, "EQUALS");
            return field + " = " + type.formatLiteral(first(values));
        }
    },
    NOT_EQUALS {
        @Override
        public String buildFeelExpression(String field, List<String> values, DataType type) {
            requireValues(values, "NOT_EQUALS");
            return field + " != " + type.formatLiteral(first(values));
        }
    },
    GREATER {
        @Override
        public String buildFeelExpression(String field, List<String> values, DataType type) {
            requireValues(values, "GREATER");
            return field + " > " + type.formatLiteral(first(values));
        }
    },
    GREATER_OR_EQUAL {
        @Override
        public String buildFeelExpression(String field, List<String> values, DataType type) {
            requireValues(values, "GREATER_OR_EQUAL");
            return field + " >= " + type.formatLiteral(first(values));
        }
    },
    LESS {
        @Override
        public String buildFeelExpression(String field, List<String> values, DataType type) {
            requireValues(values, "LESS");
            return field + " < " + type.formatLiteral(first(values));
        }
    },
    LESS_OR_EQUAL {
        @Override
        public String buildFeelExpression(String field, List<String> values, DataType type) {
            requireValues(values, "LESS_OR_EQUAL");
            return field + " <= " + type.formatLiteral(first(values));
        }
    },
    BETWEEN {
        @Override
        public String buildFeelExpression(String field, List<String> values, DataType type) {
            requireMinValues(values, 2, "BETWEEN");
            String v1 = type.formatLiteral(first(values));
            String v2 = type.formatLiteral(second(values));
            return field + " >= " + v1 + " and " + field + " <= " + v2;
        }
    },
    IN {
        @Override
        public String buildFeelExpression(String field, List<String> values, DataType type) {
            requireValues(values, "IN");
            return field + " in " + type.formatLiteralList(values);
        }
    },
    NOT_IN {
        @Override
        public String buildFeelExpression(String field, List<String> values, DataType type) {
            requireValues(values, "NOT_IN");
            return "not(" + field + " in " + type.formatLiteralList(values) + ")";
        }
    },
    CONTAINS {
        @Override
        public String buildFeelExpression(String field, List<String> values, DataType type) {
            requireValues(values, "CONTAINS");
            return "contains(" + field + ", " + type.formatLiteral(first(values)) + ")";
        }
    },
    STARTS_WITH {
        @Override
        public String buildFeelExpression(String field, List<String> values, DataType type) {
            requireValues(values, "STARTS_WITH");
            return "starts with(" + field + ", " + type.formatLiteral(first(values)) + ")";
        }
    },
    ENDS_WITH {
        @Override
        public String buildFeelExpression(String field, List<String> values, DataType type) {
            requireValues(values, "ENDS_WITH");
            return "ends with(" + field + ", " + type.formatLiteral(first(values)) + ")";
        }
    },
    IS_NULL {
        @Override
        public String buildFeelExpression(String field, List<String> values, DataType type) {
            return field + " = null";
        }
    },
    IS_NOT_NULL {
        @Override
        public String buildFeelExpression(String field, List<String> values, DataType type) {
            return field + " != null";
        }
    };

    /**
     * Generate FEEL expression for this operator.
     */
    public abstract String buildFeelExpression(String field, List<String> values, DataType type);

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
    // Guards
    // ─────────────────────────────────────────────────────────────

    protected static void requireValues(List<String> values, String operatorName) {
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException(operatorName + " requires at least one value");
        }
    }

    protected static void requireMinValues(List<String> values, int min, String operatorName) {
        if (values == null || values.size() < min) {
            throw new IllegalArgumentException(operatorName + " requires at least " + min + " values");
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────

    protected static String first(List<String> values) {
        return values.get(0);
    }

    protected static String second(List<String> values) {
        return values.get(1);
    }
}
