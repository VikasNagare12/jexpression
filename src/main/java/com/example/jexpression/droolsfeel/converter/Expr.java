package com.example.jexpression.droolsfeel.converter;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Simple utility class for building FEEL expressions.
 * All string comparisons are case-insensitive.
 */
public final class Expr {

    private Expr() {}

    // ═══════════════════════════════════════════════════════════════
    // COMPARISON
    // ═══════════════════════════════════════════════════════════════

    public static String eq(String field, Object value) {
        return "%s = %s".formatted(field, format(value));
    }

    public static String neq(String field, Object value) {
        return "%s != %s".formatted(field, format(value));
    }

    public static String gt(String field, Number value) {
        return "%s > %s".formatted(field, value);
    }

    public static String gte(String field, Number value) {
        return "%s >= %s".formatted(field, value);
    }

    public static String lt(String field, Number value) {
        return "%s < %s".formatted(field, value);
    }

    public static String lte(String field, Number value) {
        return "%s <= %s".formatted(field, value);
    }

    public static String between(String field, Object from, Object to) {
        return "%s >= %s and %s <= %s".formatted(field, format(from), field, format(to));
    }

    // ═══════════════════════════════════════════════════════════════
    // STRING (case-insensitive)
    // ═══════════════════════════════════════════════════════════════

    public static String strEq(String field, String value) {
        return "lower case(%s) = \"%s\"".formatted(field, value.toLowerCase());
    }

    public static String strNeq(String field, String value) {
        return "lower case(%s) != \"%s\"".formatted(field, value.toLowerCase());
    }

    public static String contains(String field, String value) {
        return "contains(lower case(%s), \"%s\")".formatted(field, value.toLowerCase());
    }

    public static String startsWith(String field, String value) {
        return "starts with(lower case(%s), \"%s\")".formatted(field, value.toLowerCase());
    }

    public static String endsWith(String field, String value) {
        return "ends with(lower case(%s), \"%s\")".formatted(field, value.toLowerCase());
    }

    public static String matches(String field, String pattern) {
        return "matches(%s, \"%s\", \"i\")".formatted(field, pattern);
    }

    // ═══════════════════════════════════════════════════════════════
    // LIST
    // ═══════════════════════════════════════════════════════════════

    public static String in(String field, Object... values) {
        return "%s in [%s]".formatted(field, formatList(values));
    }

    public static String notIn(String field, Object... values) {
        return "not(%s in [%s])".formatted(field, formatList(values));
    }

    public static String strIn(String field, String... values) {
        String list = Arrays.stream(values)
            .map(v -> "\"%s\"".formatted(v.toLowerCase()))
            .collect(Collectors.joining(", "));
        return "lower case(%s) in [%s]".formatted(field, list);
    }

    // ═══════════════════════════════════════════════════════════════
    // DATE
    // ═══════════════════════════════════════════════════════════════

    public static String dateEq(String field, String date) {
        return "date(%s) = date(\"%s\")".formatted(field, date);
    }

    public static String dateBetween(String field, String from, String to) {
        return "date(%s) >= date(\"%s\") and date(%s) <= date(\"%s\")".formatted(field, from, field, to);
    }

    public static String dateGte(String field, String date) {
        return "date(%s) >= date(\"%s\")".formatted(field, date);
    }

    public static String dateLte(String field, String date) {
        return "date(%s) <= date(\"%s\")".formatted(field, date);
    }

    // ═══════════════════════════════════════════════════════════════
    // NULL
    // ═══════════════════════════════════════════════════════════════

    public static String isNull(String field) {
        return "%s = null".formatted(field);
    }

    public static String isNotNull(String field) {
        return "%s != null".formatted(field);
    }

    // ═══════════════════════════════════════════════════════════════
    // COMBINE
    // ═══════════════════════════════════════════════════════════════

    public static String and(String... exprs) {
        return String.join(" and ", exprs);
    }

    public static String or(String... exprs) {
        return String.join(" or ", exprs);
    }

    // ═══════════════════════════════════════════════════════════════
    // INTERNAL
    // ═══════════════════════════════════════════════════════════════

    private static String format(Object value) {
        if (value == null) return "null";
        if (value instanceof Number) return value.toString();
        return "\"%s\"".formatted(value);
    }

    private static String formatList(Object... values) {
        return Arrays.stream(values)
            .map(Expr::format)
            .collect(Collectors.joining(", "));
    }
}
