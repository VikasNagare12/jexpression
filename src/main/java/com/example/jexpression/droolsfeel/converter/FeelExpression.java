package com.example.jexpression.droolsfeel.converter;

/**
 * Type-safe fluent builder for FEEL expressions.
 * 
 * <p>Eliminates string concatenation errors by providing a fluent API.
 * All string comparisons are automatically case-insensitive.
 * 
 * <h3>Usage:</h3>
 * <pre>{@code
 * String expr = FeelExpression.field("transaction.amount")
 *     .greaterOrEqual(100)
 *     .build();
 * // Result: transaction.amount >= 100
 * 
 * String expr = FeelExpression.stringField("transaction.country")
 *     .equalsValue("SA")
 *     .build();
 * // Result: lower case(transaction.country) = "sa"
 * }</pre>
 */
public final class FeelExpression {

    private final StringBuilder expr = new StringBuilder();

    private FeelExpression() {}

    // ═══════════════════════════════════════════════════════════════
    // ENTRY POINTS
    // ═══════════════════════════════════════════════════════════════

    /**
     * Start with a raw field (number, boolean).
     */
    public static FieldBuilder field(String fieldPath) {
        return new FieldBuilder(fieldPath, FieldType.RAW);
    }

    /**
     * Start with a string field (case-insensitive).
     */
    public static FieldBuilder stringField(String fieldPath) {
        return new FieldBuilder(fieldPath, FieldType.STRING);
    }

    /**
     * Start with a date field.
     */
    public static FieldBuilder dateField(String fieldPath) {
        return new FieldBuilder(fieldPath, FieldType.DATE);
    }

    // ═══════════════════════════════════════════════════════════════
    // FIELD BUILDER
    // ═══════════════════════════════════════════════════════════════

    public static class FieldBuilder {
        private final String fieldPath;
        private final FieldType type;

        FieldBuilder(String fieldPath, FieldType type) {
            this.fieldPath = fieldPath;
            this.type = type;
        }

        // ─────────────────────────────────────────────────────────────
        // Comparison Operators
        // ─────────────────────────────────────────────────────────────

        public ExpressionBuilder equalsValue(Object value) {
            return compare("=", value);
        }

        public ExpressionBuilder notEquals(Object value) {
            return compare("!=", value);
        }

        public ExpressionBuilder greaterThan(Number value) {
            return compare(">", value);
        }

        public ExpressionBuilder greaterOrEqual(Number value) {
            return compare(">=", value);
        }

        public ExpressionBuilder lessThan(Number value) {
            return compare("<", value);
        }

        public ExpressionBuilder lessOrEqual(Number value) {
            return compare("<=", value);
        }

        public ExpressionBuilder between(Object from, Object to) {
            String f = formatField();
            String fromVal = formatValue(from);
            String toVal = formatValue(to);
            return new ExpressionBuilder("%s >= %s and %s <= %s".formatted(f, fromVal, f, toVal));
        }

        // ─────────────────────────────────────────────────────────────
        // List Operators
        // ─────────────────────────────────────────────────────────────

        public ExpressionBuilder in(Object... values) {
            String list = formatList(values);
            return new ExpressionBuilder("%s in [%s]".formatted(formatField(), list));
        }

        public ExpressionBuilder notIn(Object... values) {
            String list = formatList(values);
            return new ExpressionBuilder("not(%s in [%s])".formatted(formatField(), list));
        }

        // ─────────────────────────────────────────────────────────────
        // String Operators
        // ─────────────────────────────────────────────────────────────

        public ExpressionBuilder contains(String value) {
            return new ExpressionBuilder("contains(%s, %s)".formatted(
                formatField(), formatValue(value)));
        }

        public ExpressionBuilder startsWith(String value) {
            return new ExpressionBuilder("starts with(%s, %s)".formatted(
                formatField(), formatValue(value)));
        }

        public ExpressionBuilder endsWith(String value) {
            return new ExpressionBuilder("ends with(%s, %s)".formatted(
                formatField(), formatValue(value)));
        }

        public ExpressionBuilder matches(String pattern) {
            // Case-insensitive regex
            return new ExpressionBuilder("matches(%s, \"%s\", \"i\")".formatted(
                fieldPath, escape(pattern)));
        }

        // ─────────────────────────────────────────────────────────────
        // Null Operators
        // ─────────────────────────────────────────────────────────────

        public ExpressionBuilder isNull() {
            return new ExpressionBuilder("%s = null".formatted(fieldPath));
        }

        public ExpressionBuilder isNotNull() {
            return new ExpressionBuilder("%s != null".formatted(fieldPath));
        }

        public ExpressionBuilder exists() {
            return isNotNull();
        }

        // ─────────────────────────────────────────────────────────────
        // Internal Helpers
        // ─────────────────────────────────────────────────────────────

        private ExpressionBuilder compare(String operator, Object value) {
            return new ExpressionBuilder("%s %s %s".formatted(
                formatField(), operator, formatValue(value)));
        }

        private String formatField() {
            return switch (type) {
                case STRING -> "lower case(%s)".formatted(fieldPath);
                case DATE -> "date(%s)".formatted(fieldPath);
                case RAW -> fieldPath;
            };
        }

        private String formatValue(Object value) {
            if (value == null) return "null";
            
            return switch (type) {
                case STRING -> "\"%s\"".formatted(value.toString().toLowerCase());
                case DATE -> "date(\"%s\")".formatted(value);
                case RAW -> value.toString();
            };
        }

        private String formatList(Object... values) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < values.length; i++) {
                if (i > 0) sb.append(", ");
                sb.append(formatValue(values[i]));
            }
            return sb.toString();
        }

        private String escape(String s) {
            return s == null ? "" : s.replace("\\", "\\\\");
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // EXPRESSION BUILDER (for chaining with AND/OR)
    // ═══════════════════════════════════════════════════════════════

    public static class ExpressionBuilder {
        private final StringBuilder expr;

        ExpressionBuilder(String initial) {
            this.expr = new StringBuilder(initial);
        }

        public ExpressionBuilder and(ExpressionBuilder other) {
            expr.append(" and ").append(other.expr);
            return this;
        }

        public ExpressionBuilder or(ExpressionBuilder other) {
            expr.append(" or ").append(other.expr);
            return this;
        }

        public String build() {
            return expr.toString();
        }

        @Override
        public String toString() {
            return build();
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // FIELD TYPE ENUM
    // ═══════════════════════════════════════════════════════════════

    private enum FieldType {
        RAW,    // Numbers, booleans
        STRING, // Case-insensitive strings
        DATE    // Date values
    }
}
