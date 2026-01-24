package com.example.jexpression.droolsfeel.converter;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Production-ready FEEL expression templates with type-based lookup.
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

    // Comparators
    EQUALS("Equals", DataType.NUMBER, 2, "{0} = {1}"),
    NOT_EQUALS("NotEquals", DataType.NUMBER, 2, "{0} != {1}"),
    GREATER_THAN("Greater", DataType.NUMBER, 2, "{0} > {1}"),
    GREATER_OR_EQUAL("GreaterOrEqual", DataType.NUMBER, 2, "{0} >= {1}"),
    LESS_THAN("Less", DataType.NUMBER, 2, "{0} < {1}"),
    LESS_OR_EQUAL("LessOrEqual", DataType.NUMBER, 2, "{0} <= {1}"),
    BETWEEN("Between", DataType.NUMBER, 3, "{0} >= {1} and {0} <= {2}"),
    IN_LIST("In", DataType.NUMBER, 2, "{0} in [{1}]"),
    NOT_IN_LIST("NotIn", DataType.NUMBER, 2, "not({0} in [{1}])"),

    // String Operators (case-insensitive)
    STRING_EQUALS("Equals", DataType.STRING, 2, "lower case({0}) = lower case(\"{1}\")"),
    STRING_NOT_EQUALS("NotEquals", DataType.STRING, 2, "lower case({0}) != lower case(\"{1}\")"),
    STRING_IN_LIST("In", DataType.STRING, 2, "lower case({0}) in [{1}]"),
    CONTAINS("Contains", DataType.STRING, 2, "contains(lower case({0}), lower case(\"{1}\"))"),
    STARTS_WITH("StartsWith", DataType.STRING, 2, "starts with(lower case({0}), lower case(\"{1}\"))"),
    ENDS_WITH("EndsWith", DataType.STRING, 2, "ends with(lower case({0}), lower case(\"{1}\"))"),
    MATCHES("Matches", DataType.STRING, 2, "matches({0}, \"{1}\", \"i\")"),

    // Date Operators
    DATE_EQUALS("Equals", DataType.DATE, 2, "date({0}) = date(\"{1}\")"),
    DATE_GREATER_THAN("Greater", DataType.DATE, 2, "date({0}) > date(\"{1}\")"),
    DATE_GREATER_OR_EQUAL("GreaterOrEqual", DataType.DATE, 2, "date({0}) >= date(\"{1}\")"),
    DATE_LESS_THAN("Less", DataType.DATE, 2, "date({0}) < date(\"{1}\")"),
            DATE_LESS_OR_EQUAL("LessOrEqual", DataType.DATE, 2, "date({0}) <= date(\"{1}\")"),
    DATE_BETWEEN("Between", DataType.DATE, 3, "date({0}) >= date(\"{1}\") and date({0}) <= date(\"{2}\")"),

    // Generic Operators
    IS_NULL("IsNull", DataType.ANY, 1, "{0} = null"),
    IS_NOT_NULL("IsNotNull", DataType.ANY, 1, "{0} != null"),
    EXISTS("Exists", DataType.ANY, 1, "{0} != null"),
    NOT("Not", DataType.ANY, 1, "not({0})"),
            LIST_CONTAINS("ListContains", DataType.ANY, 2, "list contains({0}, {1})");

    public enum DataType {
        STRING, NUMBER, DATE, BOOLEAN, ANY;

        public static DataType fromString(String type) {
            if (type == null)
                return STRING;
            return switch (type.toLowerCase()) {
                case "string" -> STRING;
                case "number" -> NUMBER;
                case "date", "datetime" -> DATE;
                case "boolean" -> BOOLEAN;
                default -> STRING;
            };
        }
    }

    private final String operator;
    private final DataType dataType;
    private final int argCount;
    private final String pattern;

    private static final Map<String, FeelTemplate> LOOKUP = buildLookup();

    FeelTemplate(String operator, DataType dataType, int argCount, String pattern) {
        this.operator = operator;
        this.dataType = dataType;
        this.argCount = argCount;
        this.pattern = pattern;
    }

    private static Map<String, FeelTemplate> buildLookup() {
        return Arrays.stream(values())
                .collect(Collectors.toMap(
                        t -> t.operator + ":" + t.dataType.name(),
                        t -> t,
                        (a, b) -> a // Keep first if duplicate
                ));
    }

    /**
     * Find template by operator and data type.
     */
    public static FeelTemplate forOperator(String operator, String type) {
        return forOperator(operator, DataType.fromString(type));
    }

    /**
     * Find template by operator and data type.
     */
    public static FeelTemplate forOperator(String operator, DataType type) {
        String key = operator + ":" + type.name();
        FeelTemplate template = LOOKUP.get(key);

        if (template == null) {
            template = LOOKUP.get(operator + ":" + DataType.ANY.name());
        }

        if (template == null) {
            throw new IllegalArgumentException(
                    "No template for operator '%s' with type '%s'".formatted(operator, type));
        }
        return template;
    }

    /**
     * Apply values to template with validation.
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
     * <p>
     * Automatically handles String case-insensitivity if DataType is STRING.
     */
    public String applyWithList(String field, Object... values) {
        Objects.requireNonNull(field, "field must not be null");
        validateNotEmpty(values, "values");
        
        if (this.dataType == DataType.STRING) {
            String[] stringValues = Arrays.stream(values)
                .map(Object::toString)
                .toArray(String[]::new);
            return applyWithStringList(field, stringValues);
        }
        
        String list = Arrays.stream(values)
            .map(this::formatListValue)
            .collect(Collectors.joining(", "));
        return MessageFormat.format(pattern, formatValue(field), list);
    }

    /**
     * Apply with case-insensitive string list.
     */
    public String applyWithStringList(String field, String... values) {
        Objects.requireNonNull(field, "field must not be null");
        validateNotEmpty(values, "values");
        
        String list = Arrays.stream(values)
            .map(v -> "lower case(\"" + escape(v) + "\")")
            .collect(Collectors.joining(", "));
        return MessageFormat.format(pattern, formatValue(field), list);
    }

    // Unused methods removed: and(), or(), getters...

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
        return escape(value.toString());
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
