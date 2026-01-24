package com.example.jexpression.droolsfeel.converter;

import com.example.jexpression.droolsfeel.model.FeelOperator;
import com.example.jexpression.droolsfeel.model.Validation;

import java.util.stream.Collectors;

/**
 * Converts Validation to FEEL expression.
 * 
 * <p>
 * All string comparisons are CASE-INSENSITIVE (handled centrally).
 */
public final class FeelExpressionBuilder {

    private FeelExpressionBuilder() {}

    /**
     * Convert validation to FEEL expression.
     */
    public static String toFeel(Validation v) {
        FeelOperator op = v.operator();

        return switch (op) {
            // Comparison Operators
            case EQUALS, NOT_EQUALS, GREATER, GREATER_OR_EQUAL, LESS, LESS_OR_EQUAL -> simpleComparison(v, op);

            // Range Operators
            case BETWEEN -> between(v);
            case IN -> inList(v);
            case NOT_IN -> notInList(v);

            // String Operators
            case CONTAINS -> contains(v);
            case STARTS_WITH -> startsWith(v);
            case ENDS_WITH -> endsWith(v);
            case MATCHES -> matches(v);

            // Null Operators
            case EXISTS, IS_NOT_NULL -> isNotNull(v);
            case IS_NULL -> isNull(v);

            // List Operators
            case LIST_CONTAINS -> listContains(v);
        };
    }

    // ─────────────────────────────────────────────────────────────
    // Comparison Operators
    // ─────────────────────────────────────────────────────────────

    private static String simpleComparison(Validation v, FeelOperator op) {
        return "%s %s %s".formatted(field(v), op.getFeelSymbol(), value(v, 0));
    }

    private static String between(Validation v) {
        String f = field(v);
        String from = value(v, 0);
        String to = value(v, 1);
        return "%s >= %s and %s <= %s".formatted(f, from, f, to);
    }

    // ─────────────────────────────────────────────────────────────
    // List/In Operators
    // ─────────────────────────────────────────────────────────────

    private static String inList(Validation v) {
        String list = v.values().stream()
                .map(val -> formatValue(val, v.type()))
                .collect(Collectors.joining(", "));
        return "%s in [%s]".formatted(field(v), list);
    }

    private static String notInList(Validation v) {
        return "not(" + inList(v) + ")";
    }

    private static String listContains(Validation v) {
        return "list contains(%s, %s)".formatted(field(v), value(v, 0));
    }

    // ─────────────────────────────────────────────────────────────
    // String Operators
    // ─────────────────────────────────────────────────────────────

    private static String contains(Validation v) {
        return "contains(%s, %s)".formatted(field(v), value(v, 0));
    }

    private static String startsWith(Validation v) {
        return "starts with(%s, %s)".formatted(field(v), value(v, 0));
    }

    private static String endsWith(Validation v) {
        return "ends with(%s, %s)".formatted(field(v), value(v, 0));
    }

    private static String matches(Validation v) {
        // Regex with case-insensitive flag "i"
        return "matches(%s, %s, \"i\")".formatted(field(v), value(v, 0));
    }

    // ─────────────────────────────────────────────────────────────
    // Null Operators
    // ─────────────────────────────────────────────────────────────

    private static String isNull(Validation v) {
        return "%s = null".formatted(v.field());
    }

    private static String isNotNull(Validation v) {
        return "%s != null".formatted(v.field());
    }

    // ─────────────────────────────────────────────────────────────
    // CENTRAL: Field and Value Formatting
    // ─────────────────────────────────────────────────────────────

    /**
     * Format field - wraps with lower case() for strings, date() for dates.
     */
    private static String field(Validation v) {
        if (v.isString()) {
            return "lower case(%s)".formatted(v.field());
        }
        if (v.isDate()) {
            return "date(%s)".formatted(v.field());
        }
        return v.field();
    }

    /**
     * Format value at index based on type.
     */
    private static String value(Validation v, int index) {
        String val = index == 0 ? v.firstValue() : v.secondValue();
        return formatValue(val, v.type());
    }

    /**
     * Format value - strings are lowercased for case-insensitive comparison.
     */
    private static String formatValue(String val, String type) {
        if (val == null)
            return "null";

        return switch (type != null ? type.toLowerCase() : "string") {
            case "number" -> val;
            case "date" -> "date(\"%s\")".formatted(val);
            case "datetime" -> "date and time(\"%s\")".formatted(val);
            case "time" -> "time(\"%s\")".formatted(val);
            case "boolean" -> val.toLowerCase();
            default -> "\"%s\"".formatted(val.toLowerCase()); // string: lowercase value
        };
    }

    /**
     * Escape regex special characters.
     */
    private static String escape(String s) {
        if (s == null)
            return "";
        return s.replace("\\", "\\\\");
    }
}
