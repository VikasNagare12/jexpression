package com.example.jexpression.ast;

import java.util.List;

/**
 * FEEL operators with self-contained expression generation.
 * Expects pre-formatted literal values from the boundary layer.
 * Operator strings must be normalized (trimmed + uppercased) before calling
 * from().
 */
public enum ExpressionOperator {

    EQUALS {
        @Override
        public String buildFeelExpression(String field, List<String> formattedValues) {
            requireValues(formattedValues, "EQUALS");
            return field + " = " + first(formattedValues);
        }
    },
    NOT_EQUALS {
        @Override
        public String buildFeelExpression(String field, List<String> formattedValues) {
            requireValues(formattedValues, "NOT_EQUALS");
            return field + " != " + first(formattedValues);
        }
    },
    GREATER {
        @Override
        public String buildFeelExpression(String field, List<String> formattedValues) {
            requireValues(formattedValues, "GREATER");
            return field + " > " + first(formattedValues);
        }
    },
    GREATER_OR_EQUAL {
        @Override
        public String buildFeelExpression(String field, List<String> formattedValues) {
            requireValues(formattedValues, "GREATER_OR_EQUAL");
            return field + " >= " + first(formattedValues);
        }
    },
    LESS {
        @Override
        public String buildFeelExpression(String field, List<String> formattedValues) {
            requireValues(formattedValues, "LESS");
            return field + " < " + first(formattedValues);
        }
    },
    LESS_OR_EQUAL {
        @Override
        public String buildFeelExpression(String field, List<String> formattedValues) {
            requireValues(formattedValues, "LESS_OR_EQUAL");
            return field + " <= " + first(formattedValues);
        }
    },
    BETWEEN {
        @Override
        public String buildFeelExpression(String field, List<String> formattedValues) {
            requireMinValues(formattedValues, 2, "BETWEEN");
            return field + " >= " + first(formattedValues) + " and " + field + " <= " + second(formattedValues);
        }
    },
    IN {
        @Override
        public String buildFeelExpression(String field, List<String> formattedValues) {
            requireValues(formattedValues, "IN");
            return field + " in [" + String.join(", ", formattedValues) + "]";
        }
    },
    NOT_IN {
        @Override
        public String buildFeelExpression(String field, List<String> formattedValues) {
            requireValues(formattedValues, "NOT_IN");
            return "not(" + field + " in [" + String.join(", ", formattedValues) + "])";
        }
    },
    CONTAINS {
        @Override
        public String buildFeelExpression(String field, List<String> formattedValues) {
            requireValues(formattedValues, "CONTAINS");
            return "contains(" + field + ", " + first(formattedValues) + ")";
        }
    },
    STARTS_WITH {
        @Override
        public String buildFeelExpression(String field, List<String> formattedValues) {
            requireValues(formattedValues, "STARTS_WITH");
            return "starts with(" + field + ", " + first(formattedValues) + ")";
        }
    },
    ENDS_WITH {
        @Override
        public String buildFeelExpression(String field, List<String> formattedValues) {
            requireValues(formattedValues, "ENDS_WITH");
            return "ends with(" + field + ", " + first(formattedValues) + ")";
        }
    },
    IS_NULL {
        @Override
        public String buildFeelExpression(String field, List<String> formattedValues) {
            return field + " = null";
        }
    },
    IS_NOT_NULL {
        @Override
        public String buildFeelExpression(String field, List<String> formattedValues) {
            return field + " != null";
        }
    };

    /**
     * Generate FEEL expression for this operator.
     * Values must be pre-formatted by the boundary layer.
     */
    public abstract String buildFeelExpression(String field, List<String> formattedValues);

    /**
     * Parse normalized operator string to enum.
     * Input must be trimmed and uppercased by the caller.
     */
    public static ExpressionOperator from(String normalizedOp) {
        if (normalizedOp == null) {
            throw new IllegalArgumentException("Operator cannot be null");
        }

        return switch (normalizedOp) {
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
            default -> throw new IllegalArgumentException("Unknown operator: " + normalizedOp);
        };
    }

    // ─────────────────────────────────────────────────────────────
    // Private Guards
    // ─────────────────────────────────────────────────────────────

    private static void requireValues(List<String> values, String operatorName) {
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException(operatorName + " requires at least one value");
        }
    }

    private static void requireMinValues(List<String> values, int min, String operatorName) {
        if (values == null || values.size() < min) {
            throw new IllegalArgumentException(operatorName + " requires at least " + min + " values");
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Private Helpers
    // ─────────────────────────────────────────────────────────────

    private static String first(List<String> values) {
        return values.get(0);
    }

    private static String second(List<String> values) {
        return values.get(1);
    }
}
