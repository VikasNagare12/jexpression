package com.example.jexpression.droolsfeel.converter;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Complete FEEL expression builder with all operators from DMN specification.
 * 
 * Categories:
 * - Comparison: eq, neq, gt, gte, lt, lte, between
 * - String: contains, startsWith, endsWith, matches, length, upper, lower,
 * substring, replace
 * - List: in, notIn, listContains, count, sum, min, max, mean, all, any
 * - Date/Time: date, time, dateTime, duration, dayOfWeek, monthOfYear
 * - Null: isNull, isNotNull
 * - Boolean: and, or, not
 * - Arithmetic: add, sub, mul, div, mod
 */
public final class Expr {

    private Expr() {}

    // ═══════════════════════════════════════════════════════════════
    // COMPARISON OPERATORS
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

    /** FEEL range syntax: [1..10], (1..10), [1..10) */
    public static String inRange(String field, String range) {
        return "%s in %s".formatted(field, range);
    }

    // ═══════════════════════════════════════════════════════════════
    // STRING OPERATORS (case-insensitive by default)
    // ═══════════════════════════════════════════════════════════════

    public static String strEq(String field, String value) {
        return "lower case(%s) = lower case(\"%s\")".formatted(field, value);
    }

    public static String strNeq(String field, String value) {
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

    /** string length(string) */
    public static String strLength(String field) {
        return "string length(%s)".formatted(field);
    }

    /** upper case(string) */
    public static String upper(String field) {
        return "upper case(%s)".formatted(field);
    }

    /** lower case(string) */
    public static String lower(String field) {
        return "lower case(%s)".formatted(field);
    }

    /** substring(string, start, length?) */
    public static String substring(String field, int start) {
        return "substring(%s, %d)".formatted(field, start);
    }

    public static String substring(String field, int start, int length) {
        return "substring(%s, %d, %d)".formatted(field, start, length);
    }

    /** substring before(string, match) */
    public static String substringBefore(String field, String match) {
        return "substring before(%s, \"%s\")".formatted(field, match);
    }

    /** substring after(string, match) */
    public static String substringAfter(String field, String match) {
        return "substring after(%s, \"%s\")".formatted(field, match);
    }

    /** replace(input, pattern, replacement) */
    public static String replace(String field, String pattern, String replacement) {
        return "replace(%s, \"%s\", \"%s\")".formatted(field, pattern, replacement);
    }

    /** split(string, delimiter) - returns list */
    public static String split(String field, String delimiter) {
        return "split(%s, \"%s\")".formatted(field, delimiter);
    }

    // ═══════════════════════════════════════════════════════════════
    // LIST OPERATORS
    // ═══════════════════════════════════════════════════════════════

    public static String in(String field, Object... values) {
        return "%s in [%s]".formatted(field, formatList(values));
    }

    public static String notIn(String field, Object... values) {
        return "not(%s in [%s])".formatted(field, formatList(values));
    }

    public static String strIn(String field, String... values) {
        String list = Arrays.stream(values)
                .map(v -> "lower case(\"%s\")".formatted(v))
            .collect(Collectors.joining(", "));
        return "lower case(%s) in [%s]".formatted(field, list);
    }

    /** list contains(list, element) */
    public static String listContains(String listField, Object element) {
        return "list contains(%s, %s)".formatted(listField, format(element));
    }

    /** count(list) */
    public static String count(String listField) {
        return "count(%s)".formatted(listField);
    }

    /** sum(list) */
    public static String sum(String listField) {
        return "sum(%s)".formatted(listField);
    }

    /** min(list) */
    public static String min(String listField) {
        return "min(%s)".formatted(listField);
    }

    /** max(list) */
    public static String max(String listField) {
        return "max(%s)".formatted(listField);
    }

    /** mean(list) - average */
    public static String mean(String listField) {
        return "mean(%s)".formatted(listField);
    }

    /** median(list) */
    public static String median(String listField) {
        return "median(%s)".formatted(listField);
    }

    /** mode(list) */
    public static String mode(String listField) {
        return "mode(%s)".formatted(listField);
    }

    /** stddev(list) - standard deviation */
    public static String stddev(String listField) {
        return "stddev(%s)".formatted(listField);
    }

    /** all(list) - all elements true */
    public static String all(String listField) {
        return "all(%s)".formatted(listField);
    }

    /** any(list) - any element true */
    public static String any(String listField) {
        return "any(%s)".formatted(listField);
    }

    /** flatten(list) - removes nesting */
    public static String flatten(String listField) {
        return "flatten(%s)".formatted(listField);
    }

    /** distinct values(list) */
    public static String distinct(String listField) {
        return "distinct values(%s)".formatted(listField);
    }

    /** reverse(list) */
    public static String reverse(String listField) {
        return "reverse(%s)".formatted(listField);
    }

    // ═══════════════════════════════════════════════════════════════
    // DATE/TIME OPERATORS
    // ═══════════════════════════════════════════════════════════════

    public static String dateEq(String field, String date) {
        return "date(%s) = date(\"%s\")".formatted(field, date);
    }

    public static String dateBetween(String field, String from, String to) {
        return "date(%s) >= date(\"%s\") and date(%s) <= date(\"%s\")".formatted(field, from, field, to);
    }

    public static String dateGt(String field, String date) {
        return "date(%s) > date(\"%s\")".formatted(field, date);
    }

    public static String dateGte(String field, String date) {
        return "date(%s) >= date(\"%s\")".formatted(field, date);
    }

    public static String dateLt(String field, String date) {
        return "date(%s) < date(\"%s\")".formatted(field, date);
    }

    public static String dateLte(String field, String date) {
        return "date(%s) <= date(\"%s\")".formatted(field, date);
    }

    /** date(year, month, day) - construct date */
    public static String dateConstruct(int year, int month, int day) {
        return "date(%d, %d, %d)".formatted(year, month, day);
    }

    /** today() */
    public static String today() {
        return "today()";
    }

    /** now() */
    public static String now() {
        return "now()";
    }

    /** day of week(date) */
    public static String dayOfWeek(String field) {
        return "day of week(date(%s))".formatted(field);
    }

    /** month of year(date) */
    public static String monthOfYear(String field) {
        return "month of year(date(%s))".formatted(field);
    }

    /** Extract year: date.year */
    public static String year(String field) {
        return "date(%s).year".formatted(field);
    }

    /** Extract month: date.month */
    public static String month(String field) {
        return "date(%s).month".formatted(field);
    }

    /** Extract day: date.day */
    public static String day(String field) {
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

    public static String and(String... exprs) {
        return String.join(" and ", exprs);
    }

    public static String or(String... exprs) {
        return String.join(" or ", exprs);
    }

    public static String not(String expr) {
        return "not(%s)".formatted(expr);
    }

    /** if condition then result1 else result2 */
    public static String ifThenElse(String condition, String thenExpr, String elseExpr) {
        return "if %s then %s else %s".formatted(condition, thenExpr, elseExpr);
    }

    // ═══════════════════════════════════════════════════════════════
    // ARITHMETIC OPERATORS
    // ═══════════════════════════════════════════════════════════════

    public static String add(String a, String b) {
        return "%s + %s".formatted(a, b);
    }

    public static String sub(String a, String b) {
        return "%s - %s".formatted(a, b);
    }

    public static String mul(String a, String b) {
        return "%s * %s".formatted(a, b);
    }

    public static String div(String a, String b) {
        return "%s / %s".formatted(a, b);
    }

    public static String mod(String a, String b) {
        return "%s mod %s".formatted(a, b);
    }

    public static String exp(String base, String exponent) {
        return "%s ** %s".formatted(base, exponent);
    }

    /** abs(number) */
    public static String abs(String field) {
        return "abs(%s)".formatted(field);
    }

    /** ceiling(number) */
    public static String ceiling(String field) {
        return "ceiling(%s)".formatted(field);
    }

    /** floor(number) */
    public static String floor(String field) {
        return "floor(%s)".formatted(field);
    }

    /** round up/down/half up/half down/half even */
    public static String round(String field, int scale, String mode) {
        return "round %s(%s, %d)".formatted(mode, field, scale);
    }

    /** sqrt(number) */
    public static String sqrt(String field) {
        return "sqrt(%s)".formatted(field);
    }

    /** log(number) */
    public static String log(String field) {
        return "log(%s)".formatted(field);
    }

    /** exp(number) */
    public static String expNum(String field) {
        return "exp(%s)".formatted(field);
    }

    /** decimal(number, scale) */
    public static String decimal(String field, int scale) {
        return "decimal(%s, %d)".formatted(field, scale);
    }

    // ═══════════════════════════════════════════════════════════════
    // TYPE TESTING & CONVERSION
    // ═══════════════════════════════════════════════════════════════

    /** number(string) - convert to number */
    public static String toNumber(String field) {
        return "number(%s)".formatted(field);
    }

    /** string(value) - convert to string */
    public static String toString(String field) {
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
