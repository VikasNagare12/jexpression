package com.example.jexpression.droolsfeel.converter;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Type-safe fluent builder for FEEL expressions.
 * 
 * <p>
 * Uses Java 21 sealed classes, records, pattern matching.
 * Uses Apache Commons for null-safe operations.
 */
public final class FeelExpression {

    private FeelExpression() {}

    // ═══════════════════════════════════════════════════════════════
    // ENTRY POINTS
    // ═══════════════════════════════════════════════════════════════

    /** Raw field (number, boolean). */
    public static FieldBuilder field(String fieldPath) {
        return new FieldBuilder(fieldPath, ValueFormatter.RAW);
    }

    /** String field (case-insensitive). */
    public static FieldBuilder stringField(String fieldPath) {
        return new FieldBuilder(fieldPath, ValueFormatter.STRING);
    }

    /** Date field. */
    public static FieldBuilder dateField(String fieldPath) {
        return new FieldBuilder(fieldPath, ValueFormatter.DATE);
    }

    // ═══════════════════════════════════════════════════════════════
    // VALUE FORMATTER (enum-based for simplicity)
    // ═══════════════════════════════════════════════════════════════

    public enum ValueFormatter {
        RAW {
            @Override
            public String wrapField(String field) {
                return field;
            }

            @Override
            public String formatValue(Object value) {
                return value != null ? value.toString() : "null";
            }
        },
        STRING {
            @Override
            public String wrapField(String field) {
                return "lower case(%s)".formatted(field);
            }

            @Override
            public String formatValue(Object value) {
                return value != null
                        ? "\"%s\"".formatted(StringUtils.lowerCase(value.toString()))
                        : "null";
            }
        },
        DATE {
            @Override
            public String wrapField(String field) {
                return "date(%s)".formatted(field);
            }

            @Override
            public String formatValue(Object value) {
                return value != null ? "date(\"%s\")".formatted(value) : "null";
            }
        };

        public abstract String wrapField(String field);

        public abstract String formatValue(Object value);
    }

    // ═══════════════════════════════════════════════════════════════
    // FIELD BUILDER
    // ═══════════════════════════════════════════════════════════════

    public static class FieldBuilder {
        private final String fieldPath;
        private final ValueFormatter formatter;

        FieldBuilder(String fieldPath, ValueFormatter formatter) {
            this.fieldPath = Validate.notBlank(fieldPath, "fieldPath must not be blank");
            this.formatter = formatter;
        }

        // ─────────────────────────────────────────────────────────────
        // Comparison Operators
        // ─────────────────────────────────────────────────────────────

        public Expr equalsValue(Object value) {
            return compare("=", value);
        }

        public Expr notEquals(Object value) {
            return compare("!=", value);
        }

        public Expr greaterThan(Number value) {
            return compare(">", value);
        }

        public Expr greaterOrEqual(Number value) {
            return compare(">=", value);
        }

        public Expr lessThan(Number value) {
            return compare("<", value);
        }

        public Expr lessOrEqual(Number value) {
            return compare("<=", value);
        }

        public Expr between(Object from, Object to) {
            var f = formattedField();
            return new Expr("%s >= %s and %s <= %s".formatted(
                    f, formatter.formatValue(from), f, formatter.formatValue(to)));
        }

        // ─────────────────────────────────────────────────────────────
        // List Operators
        // ─────────────────────────────────────────────────────────────

        public Expr in(Object... values) {
            Validate.notEmpty(values, "values must not be empty");
            return new Expr("%s in [%s]".formatted(formattedField(), formatList(values)));
        }

        public Expr notIn(Object... values) {
            Validate.notEmpty(values, "values must not be empty");
            return new Expr("not(%s in [%s])".formatted(formattedField(), formatList(values)));
        }

        // ─────────────────────────────────────────────────────────────
        // String Operators
        // ─────────────────────────────────────────────────────────────

        public Expr contains(String value) {
            return new Expr("contains(%s, %s)".formatted(
                    formattedField(), formatter.formatValue(value)));
        }

        public Expr startsWith(String value) {
            return new Expr("starts with(%s, %s)".formatted(
                    formattedField(), formatter.formatValue(value)));
        }

        public Expr endsWith(String value) {
            return new Expr("ends with(%s, %s)".formatted(
                    formattedField(), formatter.formatValue(value)));
        }

        public Expr matches(String pattern) {
            Validate.notBlank(pattern, "pattern must not be blank");
            return new Expr("matches(%s, \"%s\", \"i\")".formatted(
                    fieldPath, StringUtils.replace(pattern, "\\", "\\\\")));
        }

        // ─────────────────────────────────────────────────────────────
        // Null Operators
        // ─────────────────────────────────────────────────────────────

        public Expr isNull() {
            return new Expr("%s = null".formatted(fieldPath));
        }

        public Expr isNotNull() {
            return new Expr("%s != null".formatted(fieldPath));
        }

        public Expr exists() {
            return isNotNull();
        }

        // ─────────────────────────────────────────────────────────────
        // Helpers
        // ─────────────────────────────────────────────────────────────

        private Expr compare(String op, Object value) {
            return new Expr("%s %s %s".formatted(
                    formattedField(), op, formatter.formatValue(value)));
        }

        private String formattedField() {
            return formatter.wrapField(fieldPath);
        }

        private String formatList(Object... values) {
            return Arrays.stream(values)
                    .map(formatter::formatValue)
                    .collect(Collectors.joining(", "));
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // EXPRESSION RECORD (immutable)
    // ═══════════════════════════════════════════════════════════════

    public record Expr(String expression) {

        public Expr and(Expr other) {
            Validate.notNull(other, "other expression must not be null");
            return new Expr(expression + " and " + other.expression);
        }

        public Expr or(Expr other) {
            Validate.notNull(other, "other expression must not be null");
            return new Expr(expression + " or " + other.expression);
        }

        public String build() {
            return expression;
        }

        @Override
        public String toString() {
            return expression;
        }
    }
}
