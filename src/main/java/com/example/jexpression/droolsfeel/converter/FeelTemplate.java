package com.example.jexpression.droolsfeel.converter;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Production-ready FEEL expression templates with type-based lookup.
 * 
 * <p>Usage:
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

    // String Operators (case-insensitive default)
    STRING_EQUALS("Equals", DataType.STRING, 2, "lower case({0}) = {1}"),
    STRING_NOT_EQUALS("NotEquals", DataType.STRING, 2, "lower case({0}) != {1}"),
    STRING_IN_LIST("In", DataType.STRING, 2, "lower case({0}) in [{1}]"),
    CONTAINS("Contains", DataType.STRING, 2, "contains(lower case({0}), {1})"),
    STARTS_WITH("StartsWith", DataType.STRING, 2, "starts with(lower case({0}), {1})"),
    ENDS_WITH("EndsWith", DataType.STRING, 2, "ends with(lower case({0}), {1})"),
    MATCHES("Matches", DataType.STRING, 2, "matches({0}, {1}, \"i\")"),

    // Date Operators
    DATE_EQUALS("Equals", DataType.DATE, 2, "date({0}) = date({1})"),
    DATE_GREATER_THAN("Greater", DataType.DATE, 2, "date({0}) > date({1})"),
    DATE_GREATER_OR_EQUAL("GreaterOrEqual", DataType.DATE, 2, "date({0}) >= date({1})"),
    DATE_LESS_THAN("Less", DataType.DATE, 2, "date({0}) < date({1})"),
    DATE_LESS_OR_EQUAL("LessOrEqual", DataType.DATE, 2, "date({0}) <= date({1})"),
    DATE_BETWEEN("Between", DataType.DATE, 3, "date({0}) >= date({1}) and date({0}) <= date({2})"),

    // Generic Operators
    IS_NULL("IsNull", DataType.ANY, 1, "{0} = null"),
    IS_NOT_NULL("IsNotNull", DataType.ANY, 1, "{0} != null"),
    EXISTS("Exists", DataType.ANY, 1, "{0} != null"),
    NOT("Not", DataType.ANY, 1, "not({0})"),
    LIST_CONTAINS("ListContains", DataType.ANY, 2, "list contains({0}, {1})");

    public enum DataType {
        STRING, NUMBER, DATE, BOOLEAN, ANY;

        public static DataType fromString(String type) {
            return Optional.ofNullable(type)
                .map(String::toLowerCase)
                .map(t -> switch (t) {
                    case "string" -> STRING;
                    case "number" -> NUMBER;
                    case "date", "datetime" -> DATE;
                    case "boolean" -> BOOLEAN;
                    default -> STRING;
                })
                .orElse(STRING);
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
                (a, b) -> a // Stable selection
            ));
    }

    public static FeelTemplate forOperator(String operator, String type) {
        return forOperator(operator, DataType.fromString(type));
    }

    public static FeelTemplate forOperator(String operator, DataType type) {
        return Optional.ofNullable(LOOKUP.get(operator + ":" + type.name()))
            .or(() -> Optional.ofNullable(LOOKUP.get(operator + ":" + DataType.ANY.name())))
            .orElseThrow(() -> new IllegalArgumentException(
                "No template for operator '%s' with type '%s'".formatted(operator, type)
            ));
    }

    /**
     * Apply template to field and values.
     * <p>
     * Example: apply("amount", 100) -> "amount = 100"
     */
    public String apply(String field, Object... values) {
        // Validate total args: field (1) + values (N) == expected argCount
        if (values.length + 1 != argCount) {
             throw new IllegalArgumentException(
                "%s requires %d argument(s), but got %d".formatted(name(), argCount, values.length + 1)
            );
        }

        // Stream field + formatted values
        Object[] args = Stream.concat(
                Stream.of(field), 
                Arrays.stream(values).map(this::formatValue)
            ).toArray();

        return MessageFormat.format(pattern, args);
    }

    private String formatValue(Object value) {
        if (value == null) return "null";

        if (value instanceof java.util.Collection<?> col) {
            return col.stream().map(this::formatValue).collect(Collectors.joining(", "));
        }
        if (value.getClass().isArray()) {
            return Arrays.stream((Object[]) value).map(this::formatValue).collect(Collectors.joining(", "));
        }
        
        // Use pattern matching style logic (Java 8+ compatible)
        if (value instanceof Number || value instanceof Boolean) {
            return value.toString();
        }

        String s = value.toString();
        if (this.dataType == DataType.STRING) {
            s = s.toLowerCase();
        }
        return "\"" + escape(s) + "\"";
    }

    private static String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
    
    // Getters
    public String getOperator() { return operator; }
    public DataType getDataType() { return dataType; }
    public int getArgCount() { return argCount; }
    public String getPattern() { return pattern; }
}
