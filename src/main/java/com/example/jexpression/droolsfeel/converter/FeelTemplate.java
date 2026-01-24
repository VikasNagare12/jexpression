package com.example.jexpression.droolsfeel.converter;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Production-ready FEEL expression templates with validation.
 * 
 * <p>
 * Features:
 * <ul>
 * <li>Argument count validation</li>
 * <li>Null safety</li>
 * <li>String escaping</li>
 * <li>Uses Java MessageFormat internally</li>
 * </ul>
 * 
 * <p>
 * Usage:
 * 
 * <pre>
 * FeelTemplate.EQUALS.apply("amount", 100)            → "amount = 100"
 * FeelTemplate.STRING_EQUALS.apply("country", "SA")   → "lower case(country) = lower case(\"SA\")"
 * FeelTemplate.DATE_BETWEEN.apply("dt", "2025-01-01", "2025-12-31")
 * </pre>
 */
public enum FeelTemplate {

    // ═══════════════════════════════════════════════════════════════════════════
    // COMPARISON
    // ═══════════════════════════════════════════════════════════════════════════

    /** {0} = {1} */
            EQUALS(2, "{0} = {1}"),

    /** {0} != {1} */
    NOT_EQUALS(2, "{0} != {1}"),

    /** {0} > {1} */
    GREATER_THAN(2, "{0} > {1}"),

    /** {0} >= {1} */
    GREATER_OR_EQUAL(2, "{0} >= {1}"),

    /** {0} < {1} */
    LESS_THAN(2, "{0} < {1}"),

    /** {0} <= {1} */
    LESS_OR_EQUAL(2, "{0} <= {1}"),

    /** {0} >= {1} and {0} <= {2} */
    BETWEEN(3, "{0} >= {1} and {0} <= {2}"),

    // ═══════════════════════════════════════════════════════════════════════════
    // STRING (case-insensitive)
    // ═══════════════════════════════════════════════════════════════════════════

    /** lower case({0}) = lower case("{1}") */
            STRING_EQUALS(2, "lower case({0}) = lower case(\"{1}\")"),

    /** lower case({0}) != lower case("{1}") */
    STRING_NOT_EQUALS(2, "lower case({0}) != lower case(\"{1}\")"),

    /** contains(lower case({0}), lower case("{1}")) */
    CONTAINS(2, "contains(lower case({0}), lower case(\"{1}\"))"),

    /** starts with(lower case({0}), lower case("{1}")) */
    STARTS_WITH(2, "starts with(lower case({0}), lower case(\"{1}\"))"),

    /** ends with(lower case({0}), lower case("{1}")) */
    ENDS_WITH(2, "ends with(lower case({0}), lower case(\"{1}\"))"),

    /** matches({0}, "{1}", "i") */
    MATCHES(2, "matches({0}, \"{1}\", \"i\")"),

    // ═══════════════════════════════════════════════════════════════════════════
    // LIST
    // ═══════════════════════════════════════════════════════════════════════════

    /** {0} in [{1}] - use applyWithList() */
            IN_LIST(2, "{0} in [{1}]"),

    /** not({0} in [{1}]) - use applyWithList() */
    NOT_IN_LIST(2, "not({0} in [{1}])"),

    /** lower case({0}) in [{1}] - use applyWithStringList() */
    STRING_IN_LIST(2, "lower case({0}) in [{1}]"),

    /** list contains({0}, {1}) */
    LIST_CONTAINS(2, "list contains({0}, {1})"),

    // ═══════════════════════════════════════════════════════════════════════════
    // DATE
    // ═══════════════════════════════════════════════════════════════════════════

    /** date({0}) = date("{1}") */
            DATE_EQUALS(2, "date({0}) = date(\"{1}\")"),

    /** date({0}) > date("{1}") */
    DATE_GREATER_THAN(2, "date({0}) > date(\"{1}\")"),

    /** date({0}) >= date("{1}") */
    DATE_GREATER_OR_EQUAL(2, "date({0}) >= date(\"{1}\")"),

    /** date({0}) < date("{1}") */
    DATE_LESS_THAN(2, "date({0}) < date(\"{1}\")"),

    /** date({0}) <= date("{1}") */
    DATE_LESS_OR_EQUAL(2, "date({0}) <= date(\"{1}\")"),

    /** date({0}) >= date("{1}") and date({0}) <= date("{2}") */
    DATE_BETWEEN(3, "date({0}) >= date(\"{1}\") and date({0}) <= date(\"{2}\")"),

    // ═══════════════════════════════════════════════════════════════════════════
    // NULL
    // ═══════════════════════════════════════════════════════════════════════════
        
    /** {0} = null */
    IS_NULL(1, "{0} = null"),

    /** {0} != null */
            IS_NOT_NULL(1, "{0} != null"),

    // ═══════════════════════════════════════════════════════════════════════════
    // BOOLEAN
    // ═══════════════════════════════════════════════════════════════════════════

    /** not({0}) */
    NOT(1, "not({0})");

    // ═══════════════════════════════════════════════════════════════════════════
    // FIELDS
    // ═══════════════════════════════════════════════════════════════════════════

    private final int argCount;
    private final String pattern;

    FeelTemplate(int argCount, String pattern) {
        this.argCount = argCount;
        this.pattern = pattern;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // PUBLIC API
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Apply values to template with validation.
     * Uses MessageFormat for efficient substitution.
     * 
     * @param args Values to fill in: {0}, {1}, {2}...
     * @return Complete FEEL expression
     * @throws IllegalArgumentException if wrong number of arguments
     */
    public String apply(Object... args) {
        validateArgCount(args.length);

        Object[] formatted = Arrays.stream(args)
                .map(this::formatValue)
                .toArray();

        return MessageFormat.format(pattern, formatted);
    }

    /**
     * Apply with list of values (for IN_LIST, NOT_IN_LIST).
     * 
     * @param field  Field name
     * @param values Values to include in list
     * @return Complete FEEL expression
     */
    public String applyWithList(String field, Object... values) {
        Objects.requireNonNull(field, "field must not be null");
        validateNotEmpty(values, "values");
        
        String list = Arrays.stream(values)
            .map(this::formatListValue)
            .collect(Collectors.joining(", "));
        return apply(field, list);
    }

    /**
     * Apply with case-insensitive string list (for STRING_IN_LIST).
     * 
     * @param field  Field name
     * @param values String values (will be wrapped with lower case())
     * @return Complete FEEL expression
     */
    public String applyWithStringList(String field, String... values) {
        Objects.requireNonNull(field, "field must not be null");
        validateNotEmpty(values, "values");
        
        String list = Arrays.stream(values)
            .map(v -> "lower case(\"" + escape(v) + "\")")
            .collect(Collectors.joining(", "));
        return apply(field, list);
    }

    /**
     * Combine multiple expressions with AND.
     */
    public static String and(String... expressions) {
        validateNotEmpty(expressions, "expressions");
        return String.join(" and ", expressions);
    }

    /**
     * Combine multiple expressions with OR.
     */
    public static String or(String... expressions) {
        validateNotEmpty(expressions, "expressions");
        return String.join(" or ", expressions);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // GETTERS
    // ═══════════════════════════════════════════════════════════════════════════

    /** Expected number of arguments */
    public int getArgCount() {
        return argCount;
    }

    /** Raw pattern template */
    public String getPattern() {
        return pattern;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // INTERNAL HELPERS
    // ═══════════════════════════════════════════════════════════════════════════

    private void validateArgCount(int actual) {
        if (actual != argCount) {
            throw new IllegalArgumentException(
                    "%s requires %d argument(s), but got %d".formatted(name(), argCount, actual));
        }
    }

    private static void validateNotEmpty(Object[] arr, String name) {
        if (arr == null || arr.length == 0) {
            throw new IllegalArgumentException(name + " must not be empty");
        }
    }

    private String formatValue(Object value) {
        if (value == null) return "null";
        if (value instanceof Number) return value.toString();
        if (value instanceof Boolean) return value.toString();
        return value.toString();
    }

    /**
     * Format value for list inclusion (quotes strings).
     */
    private String formatListValue(Object value) {
        if (value == null) return "null";
        if (value instanceof Number) return value.toString();
        if (value instanceof Boolean) return value.toString();
        return "\"" + escape(value.toString()) + "\"";
    }

    /**
     * Escape special characters in FEEL strings.
     */
    private static String escape(String value) {
        if (value == null)
            return "null";
        return value
                .replace("\\", "\\\\") // Escape backslashes first
                .replace("\"", "\\\""); // Escape quotes
    }
}
