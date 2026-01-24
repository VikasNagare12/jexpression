package com.example.jexpression.droolsfeel.converter;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Production-ready FEEL expression templates with type-based lookup.
 * Covers comprehensive FEEL specification including Date, Time, Duration, and
 * List operations.
 * 
 * <p>
 * Usage:
 * 
 * <pre>
 * // Direct usage
 * FeelTemplate.STRING_EQUALS.apply("country", "SA")
 * 
 * // Auto-lookup by operator and type
 * FeelTemplate.forOperator("Equals", DataType.STRING).apply("country", "SA")
 * </pre>
 */
public enum FeelTemplate {

    // ═══════════════════════════════════════════════════════════════════════════
    // COMPARISON (Number)
    // ═══════════════════════════════════════════════════════════════════════════

    EQUALS("Equals", DataType.NUMBER, 2, "{0} = {1}"),
            NOT_EQUALS("NotEquals", DataType.NUMBER, 2, "{0} != {1}"),
    GREATER_THAN("Greater", DataType.NUMBER, 2, "{0} > {1}"),
    GREATER_OR_EQUAL("GreaterOrEqual", DataType.NUMBER, 2, "{0} >= {1}"),
    LESS_THAN("Less", DataType.NUMBER, 2, "{0} < {1}"),
    LESS_OR_EQUAL("LessOrEqual", DataType.NUMBER, 2, "{0} <= {1}"),
    BETWEEN("Between", DataType.NUMBER, 3, "{0} >= {1} and {0} <= {2}"),
    IN_RANGE("InRange", DataType.NUMBER, 2, "{0} in {1}"),
    IN_LIST("In", DataType.NUMBER, 2, "{0} in [{1}]"),
    NOT_IN_LIST("NotIn", DataType.NUMBER, 2, "not({0} in [{1}])"),

    // ═══════════════════════════════════════════════════════════════════════════
    // STRING (case-insensitive)
    // ═══════════════════════════════════════════════════════════════════════════

    STRING_EQUALS("Equals", DataType.STRING, 2, "lower case({0}) = lower case(\"{1}\")"),
            STRING_NOT_EQUALS("NotEquals", DataType.STRING, 2, "lower case({0}) != lower case(\"{1}\")"),
    STRING_IN_LIST("In", DataType.STRING, 2, "lower case({0}) in [{1}]"),
    STRING_NOT_IN_LIST("NotIn", DataType.STRING, 2, "not(lower case({0}) in [{1}])"),
    CONTAINS("Contains", DataType.STRING, 2, "contains(lower case({0}), lower case(\"{1}\"))"),
    STARTS_WITH("StartsWith", DataType.STRING, 2, "starts with(lower case({0}), lower case(\"{1}\"))"),
    ENDS_WITH("EndsWith", DataType.STRING, 2, "ends with(lower case({0}), lower case(\"{1}\"))"),
    MATCHES("Matches", DataType.STRING, 2, "matches({0}, \"{1}\", \"i\")"),

    // ═══════════════════════════════════════════════════════════════════════════
    // DATE
    // ═══════════════════════════════════════════════════════════════════════════

    DATE_EQUALS("Equals", DataType.DATE, 2, "date({0}) = date(\"{1}\")"),
            DATE_NOT_EQUALS("NotEquals", DataType.DATE, 2, "date({0}) != date(\"{1}\")"),
    DATE_GREATER_THAN("Greater", DataType.DATE, 2, "date({0}) > date(\"{1}\")"),
    DATE_GREATER_OR_EQUAL("GreaterOrEqual", DataType.DATE, 2, "date({0}) >= date(\"{1}\")"),
    DATE_LESS_THAN("Less", DataType.DATE, 2, "date({0}) < date(\"{1}\")"),
    DATE_LESS_OR_EQUAL("LessOrEqual", DataType.DATE, 2, "date({0}) <= date(\"{1}\")"),
    DATE_BETWEEN("Between", DataType.DATE, 3, "date({0}) >= date(\"{1}\") and date({0}) <= date(\"{2}\")"),

    // ═══════════════════════════════════════════════════════════════════════════
    // TIME
    // ═══════════════════════════════════════════════════════════════════════════

    TIME_EQUALS("Equals", DataType.TIME, 2, "time({0}) = time(\"{1}\")"),
            TIME_GREATER_THAN("Greater", DataType.TIME, 2, "time({0}) > time(\"{1}\")"),
    TIME_GREATER_OR_EQUAL("GreaterOrEqual", DataType.TIME, 2, "time({0}) >= time(\"{1}\")"),
    TIME_LESS_THAN("Less", DataType.TIME, 2, "time({0}) < time(\"{1}\")"),
    TIME_LESS_OR_EQUAL("LessOrEqual", DataType.TIME, 2, "time({0}) <= time(\"{1}\")"),
    TIME_BETWEEN("Between", DataType.TIME, 3, "time({0}) >= time(\"{1}\") and time({0}) <= time(\"{2}\")"),

    // ═══════════════════════════════════════════════════════════════════════════
    // DURATION (Year-Month or Day-Time)
    // ═══════════════════════════════════════════════════════════════════════════

    DURATION_EQUALS("Equals", DataType.DURATION, 2, "duration({0}) = duration(\"{1}\")"),
            DURATION_GREATER_THAN("Greater", DataType.DURATION, 2, "duration({0}) > duration(\"{1}\")"),
    DURATION_GREATER_OR_EQUAL("GreaterOrEqual", DataType.DURATION, 2, "duration({0}) >= duration(\"{1}\")"),
    DURATION_LESS_THAN("Less", DataType.DURATION, 2, "duration({0}) < duration(\"{1}\")"),
    DURATION_LESS_OR_EQUAL("LessOrEqual", DataType.DURATION, 2, "duration({0}) <= duration(\"{1}\")"),

    // ═══════════════════════════════════════════════════════════════════════════
    // LIST Functions & Operators
    // ═══════════════════════════════════════════════════════════════════════════

    LIST_CONTAINS("ListContains", DataType.LIST, 2, "list contains({0}, {1})"),
            COUNT("Count", DataType.LIST, 2, "count({0}) = {1}"),
    COUNT_GT("CountGreater", DataType.LIST, 2, "count({0}) > {1}"),
    SUM("Sum", DataType.LIST, 2, "sum({0}) = {1}"),
    MIN("Min", DataType.LIST, 2, "min({0}) = {1}"),
    MAX("Max", DataType.LIST, 2, "max({0}) = {1}"),
    ALL("All", DataType.LIST, 2, "all({0}) = {1}"),
    ANY("Any", DataType.LIST, 2, "any({0}) = {1}"),

    // ═══════════════════════════════════════════════════════════════════════════
    // NULL / BOOLEAN / ANY
    // ═══════════════════════════════════════════════════════════════════════════

    IS_NULL("IsNull", DataType.ANY, 1, "{0} = null"),
    IS_NOT_NULL("IsNotNull", DataType.ANY, 1, "{0} != null"),
    EXISTS("Exists", DataType.ANY, 1, "{0} != null"),
    NOT("Not", DataType.BOOLEAN, 1, "not({0})");

    // ═══════════════════════════════════════════════════════════════════════════
    // DATA TYPE ENUM
    // ═══════════════════════════════════════════════════════════════════════════

    public enum DataType {
        STRING, NUMBER, DATE, TIME, DURATION, BOOLEAN, LIST, ANY;

        public static DataType fromString(String type) {
            if (type == null)
                return STRING;
            return switch (type.toLowerCase()) {
                case "number", "numeric", "decimal", "integer" -> NUMBER;
                case "date", "datetime" -> DATE;
                case "time" -> TIME;
                case "duration", "daytimeduration", "yearmonthduration" -> DURATION;
                case "boolean", "bool" -> BOOLEAN;
                case "list", "array" -> LIST;
                default -> STRING;
            };
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // FIELDS & CONSTRUCTOR
    // ═══════════════════════════════════════════════════════════════════════════

    private final String operator;
    private final DataType dataType;
    private final int argCount;
    private final String pattern;

    FeelTemplate(String operator, DataType dataType, int argCount, String pattern) {
        this.operator = operator;
        this.dataType = dataType;
        this.argCount = argCount;
        this.pattern = pattern;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // STATIC LOOKUP
    // ═══════════════════════════════════════════════════════════════════════════

    // Optimized cached lookup table using EnumMap for types
    private static final Map<DataType, Map<String, FeelTemplate>> LOOKUP = new EnumMap<>(DataType.class);

    static {
        for (FeelTemplate t : values()) {
            LOOKUP.computeIfAbsent(t.dataType, k -> new HashMap<>())
                    .putIfAbsent(t.operator.toLowerCase(), t);
        }
    }

    /**
     * Find template by operator and data type.
     * 
     * @param operator Operator name (case-insensitive)
     * @param type     Data type string
     * @return Matching template
     * @throws IllegalArgumentException if no template found
     */
    public static FeelTemplate forOperator(String operator, String type) {
        return forOperator(operator, DataType.fromString(type));
    }

    /**
     * Find template by operator and data type object.
     */
    public static FeelTemplate forOperator(String operator, DataType type) {
        if (operator == null)
            throw new IllegalArgumentException("Operator cannot be null");

        String opKey = operator.toLowerCase();

        // 1. Try exact match for specific type
        Map<String, FeelTemplate> typeMap = LOOKUP.get(type);
        if (typeMap != null) {
            FeelTemplate t = typeMap.get(opKey);
            if (t != null)
                return t;
        }

        // 2. Try ANY type (generic operators like IsNull)
        Map<String, FeelTemplate> anyMap = LOOKUP.get(DataType.ANY);
        if (anyMap != null) {
            FeelTemplate t = anyMap.get(opKey);
            if (t != null)
                return t;
        }

        throw new IllegalArgumentException(
                "No FEEL template found for operator '%s' and type '%s'".formatted(operator, type));
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // PUBLIC API
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Apply values to template with validation and formatting.
     */
    public String apply(Object... args) {
        validateArgCount(args.length);

        Object[] formatted = Arrays.stream(args)
                .map(this::formatValue)
                .toArray();

        return MessageFormat.format(pattern, formatted);
    }

    /**
     * Apply with list of values (correctly quoted/formatted).
     */
    public String applyWithList(String field, Object... values) {
        Objects.requireNonNull(field, "field must not be null");
        validateNotEmpty(values, "values");
        
        String list = Arrays.stream(values)
            .map(this::formatListValue)
            .collect(Collectors.joining(", "));

        // Use MessageFormat directly to avoid double-escaping the list
        return MessageFormat.format(pattern, formatValue(field), list);
    }

    /**
     * Apply with list of strings (force quotes and case-insensitivity).
     */
    public String applyWithStringList(String field, String... values) {
        Objects.requireNonNull(field, "field must not be null");
        validateNotEmpty(values, "values");
        
        String list = Arrays.stream(values)
            .map(v -> "lower case(\"" + escape(v) + "\")")
            .collect(Collectors.joining(", "));

        // Use MessageFormat directly to avoid double-escaping the list
        return MessageFormat.format(pattern, formatValue(field), list);
    }

    public static String and(String... expressions) {
        validateNotEmpty(expressions, "expressions");
        return String.join(" and ", expressions);
    }

    public static String or(String... expressions) {
        validateNotEmpty(expressions, "expressions");
        return String.join(" or ", expressions);
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

        String s = value.toString();
        // Don't escape if it looks like a function call.
        // Regex: starts with lowercase letters + optional space + '(', matches function
        // calls like 'date(...)', 'lower case(...)'
        if (s.matches("^[a-z ]+\\(.*\\)$"))
            return s;

        return escape(s);
    }

    private String formatListValue(Object value) {
        if (value == null) return "null";
        if (value instanceof Number) return value.toString();
        if (value instanceof Boolean) return value.toString();
        return "\"" + escape(value.toString()) + "\"";
    }

    private static String escape(String value) {
        if (value == null)
            return "null";
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
