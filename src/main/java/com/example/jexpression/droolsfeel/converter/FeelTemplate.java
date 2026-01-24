package com.example.jexpression.droolsfeel.converter;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Pre-defined FEEL expression templates.
 * Just fill in values - no string building, no errors.
 * 
 * Usage:
 *   FeelTemplate.EQUALS.apply("amount", 100)
 *   FeelTemplate.STRING_EQUALS.apply("country", "SA")
 *   FeelTemplate.DATE_BETWEEN.apply("date", "2025-01-01", "2025-12-31")
 */
public enum FeelTemplate {

    // ═══════════════════════════════════════════════════════════════
    // COMPARISON
    // ═══════════════════════════════════════════════════════════════

    EQUALS("{0} = {1}"),
    NOT_EQUALS("{0} != {1}"),
    GREATER_THAN("{0} > {1}"),
    GREATER_OR_EQUAL("{0} >= {1}"),
    LESS_THAN("{0} < {1}"),
    LESS_OR_EQUAL("{0} <= {1}"),
    BETWEEN("{0} >= {1} and {0} <= {2}"),

    // ═══════════════════════════════════════════════════════════════
    // STRING (case-insensitive)
    // ═══════════════════════════════════════════════════════════════

    STRING_EQUALS("lower case({0}) = lower case(\"{1}\")"),
    STRING_NOT_EQUALS("lower case({0}) != lower case(\"{1}\")"),
    CONTAINS("contains(lower case({0}), lower case(\"{1}\"))"),
    STARTS_WITH("starts with(lower case({0}), lower case(\"{1}\"))"),
    ENDS_WITH("ends with(lower case({0}), lower case(\"{1}\"))"),
    MATCHES("matches({0}, \"{1}\", \"i\")"),

    // ═══════════════════════════════════════════════════════════════
    // LIST
    // ═══════════════════════════════════════════════════════════════

    IN_LIST("{0} in [{1}]"),
    NOT_IN_LIST("not({0} in [{1}])"),
    STRING_IN_LIST("lower case({0}) in [{1}]"),
    LIST_CONTAINS("list contains({0}, {1})"),

    // ═══════════════════════════════════════════════════════════════
    // DATE
    // ═══════════════════════════════════════════════════════════════

    DATE_EQUALS("date({0}) = date(\"{1}\")"),
    DATE_GREATER_THAN("date({0}) > date(\"{1}\")"),
    DATE_GREATER_OR_EQUAL("date({0}) >= date(\"{1}\")"),
    DATE_LESS_THAN("date({0}) < date(\"{1}\")"),
    DATE_LESS_OR_EQUAL("date({0}) <= date(\"{1}\")"),
    DATE_BETWEEN("date({0}) >= date(\"{1}\") and date({0}) <= date(\"{2}\")"),

    // ═══════════════════════════════════════════════════════════════
    // NULL
    // ═══════════════════════════════════════════════════════════════

    IS_NULL("{0} = null"),
    IS_NOT_NULL("{0} != null"),

    // ═══════════════════════════════════════════════════════════════
    // BOOLEAN
    // ═══════════════════════════════════════════════════════════════

    NOT("not({0})");

    // ═══════════════════════════════════════════════════════════════

    private final String template;

    FeelTemplate(String template) {
        this.template = template;
    }

    /**
     * Apply values to template.
     * 
     * @param args Values to fill in: {0}, {1}, {2}...
     * @return Complete FEEL expression
     */
    public String apply(Object... args) {
        String result = template;
        for (int i = 0; i < args.length; i++) {
            String value = formatValue(args[i]);
            result = result.replace("{" + i + "}", value);
        }
        return result;
    }

    /**
     * Apply with list of values (for IN operators).
     */
    public String applyWithList(String field, Object... values) {
        String list = Arrays.stream(values)
            .map(this::formatValue)
            .collect(Collectors.joining(", "));
        return apply(field, list);
    }

    /**
     * Apply with string list (for STRING_IN_LIST).
     */
    public String applyWithStringList(String field, String... values) {
        String list = Arrays.stream(values)
            .map(v -> "lower case(\"" + v + "\")")
            .collect(Collectors.joining(", "));
        return apply(field, list);
    }

    /**
     * Get raw template (for debugging).
     */
    public String getTemplate() {
        return template;
    }

    private String formatValue(Object value) {
        if (value == null) return "null";
        if (value instanceof Number) return value.toString();
        if (value instanceof Boolean) return value.toString();
        return value.toString();
    }
}
