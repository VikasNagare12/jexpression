package com.example.jexpression.droolsfeel.converter;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Complete FEEL expression builder with all operators from DMN specification.
 * All method names are descriptive and match FEEL operator names.
 */
public final class Expr {

    private Expr() {}

    // ═══════════════════════════════════════════════════════════════
    // COMPARISON OPERATORS
    // ═══════════════════════════════════════════════════════════════

    public static String equals(String field, Object value) {
        return "%s = %s".formatted(field, format(value));
    }

    public static String notEquals(String field, Object value) {
        return "%s != %s".formatted(field, format(value));
    }

    public static String greaterThan(String field, Number value) {
        return "%s > %s".formatted(field, value);
    }

    public static String greaterOrEqual(String field, Number value) {
        return "%s >= %s".formatted(field, value);
    }

    public static String lessThan(String field, Number value) {
        return "%s < %s".formatted(field, value);
    }

    public static String lessOrEqual(String field, Number value) {
        return "%s <= %s".formatted(field, value);
    }

    public static String between(String field, Object from, Object to) {
        return "%s >= %s and %s <= %s".formatted(field, format(from), field, format(to));
    }

    public static String inRange(String field, String range) {
        return "%s in %s".formatted(field, range);
    }

    // ═══════════════════════════════════════════════════════════════
    // STRING OPERATORS (case-insensitive)
    // ═══════════════════════════════════════════════════════════════

    public static String stringEquals(String field, String value) {
        return "lower case(%s) = lower case(\"%s\")".formatted(field, value);
    }

    public static String stringNotEquals(String field, String value) {
        return "lower case(%s) != lower case(\"%s\")".formatted(field, value);
    }

    public static String contains(String field, String value) {
        return "contains(lower case(%s), lower case(\"%s\"))".formatted(field, value);
    }

    public static String startsWith(String field, String value) {
        return "starts with(lower case(%s), lower case(\"%s\"))".formatted(field, value);
    }

    public static String endsWith(String field, String value) {
        return "ends with(lower case(%s), lower case(\"%s\"))".formatted(field, value);
    }

    public static String matches(String field, String pattern) {
        return "matches(%s, \"%s\", \"i\")".formatted(field, pattern);
    }

    public static String stringLength(String field) {
        return "string length(%s)".formatted(field);
    }

    public static String upperCase(String field) {
        return "upper case(%s)".formatted(field);
    }

    public static String lowerCase(String field) {
        return "lower case(%s)".formatted(field);
    }

    public static String substring(String field, int start) {
        return "substring(%s, %d)".formatted(field, start);
    }

    public static String substring(String field, int start, int length) {
        return "substring(%s, %d, %d)".formatted(field, start, length);
    }

    public static String substringBefore(String field, String match) {
        return "substring before(%s, \"%s\")".formatted(field, match);
    }

    public static String substringAfter(String field, String match) {
        return "substring after(%s, \"%s\")".formatted(field, match);
    }

    public static String replace(String field, String pattern, String replacement) {
        return "replace(%s, \"%s\", \"%s\")".formatted(field, pattern, replacement);
    }

    public static String split(String field, String delimiter) {
        return "split(%s, \"%s\")".formatted(field, delimiter);
    }

    // ═══════════════════════════════════════════════════════════════
    // LIST OPERATORS
    // ═══════════════════════════════════════════════════════════════

    public static String inList(String field, Object... values) {
        return "%s in [%s]".formatted(field, formatList(values));
    }

    public static String notInList(String field, Object... values) {
        return "not(%s in [%s])".formatted(field, formatList(values));
    }

    public static String stringInList(String field, String... values) {
        String list = Arrays.stream(values)
                .map(v -> "lower case(\"%s\")".formatted(v))
            .collect(Collectors.joining(", "));
        return "lower case(%s) in [%s]".formatted(field, list);
    }

    public static String listContains(String listField, Object element) {
        return "list contains(%s, %s)".formatted(listField, format(element));
    }

    public static String count(String listField) {
        return "count(%s)".formatted(listField);
    }

    public static String sum(String listField) {
        return "sum(%s)".formatted(listField);
    }

    public static String minimum(String listField) {
        return "min(%s)".formatted(listField);
    }

    public static String maximum(String listField) {
        return "max(%s)".formatted(listField);
    }

    public static String mean(String listField) {
        return "mean(%s)".formatted(listField);
    }

    public static String median(String listField) {
        return "median(%s)".formatted(listField);
    }

    public static String mode(String listField) {
        return "mode(%s)".formatted(listField);
    }

    public static String standardDeviation(String listField) {
        return "stddev(%s)".formatted(listField);
    }

    public static String allTrue(String listField) {
        return "all(%s)".formatted(listField);
    }

    public static String anyTrue(String listField) {
        return "any(%s)".formatted(listField);
    }

    public static String flatten(String listField) {
        return "flatten(%s)".formatted(listField);
    }

    public static String distinctValues(String listField) {
        return "distinct values(%s)".formatted(listField);
    }

    public static String reverse(String listField) {
        return "reverse(%s)".formatted(listField);
    }

    // ═══════════════════════════════════════════════════════════════
    // DATE/TIME OPERATORS
    // ═══════════════════════════════════════════════════════════════

    public static String dateEquals(String field, String date) {
        return "date(%s) = date(\"%s\")".formatted(field, date);
    }

    public static String dateBetween(String field, String from, String to) {
        return "date(%s) >= date(\"%s\") and date(%s) <= date(\"%s\")".formatted(field, from, field, to);
    }

    public static String dateGreaterThan(String field, String date) {
        return "date(%s) > date(\"%s\")".formatted(field, date);
    }

    public static String dateGreaterOrEqual(String field, String date) {
        return "date(%s) >= date(\"%s\")".formatted(field, date);
    }

    public static String dateLessThan(String field, String date) {
        return "date(%s) < date(\"%s\")".formatted(field, date);
    }

    public static String dateLessOrEqual(String field, String date) {
        return "date(%s) <= date(\"%s\")".formatted(field, date);
    }

    public static String dateConstruct(int year, int month, int day) {
        return "date(%d, %d, %d)".formatted(year, month, day);
    }

    public static String today() {
        return "today()";
    }

    public static String now() {
        return "now()";
    }

    public static String dayOfWeek(String field) {
        return "day of week(date(%s))".formatted(field);
    }

    public static String monthOfYear(String field) {
        return "month of year(date(%s))".formatted(field);
    }

    public static String extractYear(String field) {
        return "date(%s).year".formatted(field);
    }

    public static String extractMonth(String field) {
        return "date(%s).month".formatted(field);
    }

    public static String extractDay(String field) {
        return "date(%s).day".formatted(field);
    }

    // ═══════════════════════════════════════════════════════════════
    // NULL OPERATORS
    // ═══════════════════════════════════════════════════════════════

    public static String isNull(String field) {
        return "%s = null".formatted(field);
    }

    public static String isNotNull(String field) {
        return "%s != null".formatted(field);
    }

    // ═══════════════════════════════════════════════════════════════
    // BOOLEAN / LOGICAL OPERATORS
    // ═══════════════════════════════════════════════════════════════

    public static String and(String... expressions) {
        return String.join(" and ", expressions);
    }

    public static String or(String... expressions) {
        return String.join(" or ", expressions);
    }

    public static String not(String expression) {
        return "not(%s)".formatted(expression);
    }

    public static String ifThenElse(String condition, String thenExpr, String elseExpr) {
        return "if %s then %s else %s".formatted(condition, thenExpr, elseExpr);
    }

    // ═══════════════════════════════════════════════════════════════
    // ARITHMETIC OPERATORS
    // ═══════════════════════════════════════════════════════════════

    public static String add(String left, String right) {
        return "%s + %s".formatted(left, right);
    }

    public static String subtract(String left, String right) {
        return "%s - %s".formatted(left, right);
    }

    public static String multiply(String left, String right) {
        return "%s * %s".formatted(left, right);
    }

    public static String divide(String left, String right) {
        return "%s / %s".formatted(left, right);
    }

    public static String modulo(String left, String right) {
        return "%s mod %s".formatted(left, right);
    }

    public static String power(String base, String exponent) {
        return "%s ** %s".formatted(base, exponent);
    }

    public static String absoluteValue(String field) {
        return "abs(%s)".formatted(field);
    }

    public static String ceiling(String field) {
        return "ceiling(%s)".formatted(field);
    }

    public static String floor(String field) {
        return "floor(%s)".formatted(field);
    }

    public static String round(String field, int scale, String mode) {
        return "round %s(%s, %d)".formatted(mode, field, scale);
    }

    public static String squareRoot(String field) {
        return "sqrt(%s)".formatted(field);
    }

    public static String logarithm(String field) {
        return "log(%s)".formatted(field);
    }

    public static String exponential(String field) {
        return "exp(%s)".formatted(field);
    }

    public static String decimal(String field, int scale) {
        return "decimal(%s, %d)".formatted(field, scale);
    }

    // ═══════════════════════════════════════════════════════════════
    // TYPE CONVERSION
    // ═══════════════════════════════════════════════════════════════

    public static String toNumber(String field) {
        return "number(%s)".formatted(field);
    }

    public static String convertToString(String field) {
        return "string(%s)".formatted(field);
    }

    // ═══════════════════════════════════════════════════════════════
    // INTERNAL HELPERS
    // ═══════════════════════════════════════════════════════════════

    private static String format(Object value) {
        if (value == null) return "null";
        if (value instanceof Number) return value.toString();
        if (value instanceof Boolean)
            return value.toString();
        return "\"%s\"".formatted(value);
    }

    private static String formatList(Object... values) {
        return Arrays.stream(values)
            .map(Expr::format)
            .collect(Collectors.joining(", "));
    }
}
