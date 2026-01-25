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
    EQUALS("Equals", DataType.NUMBER, ParamType.SCALAR, 2, "{0} = {1}"),
    NOT_EQUALS("NotEquals", DataType.NUMBER, ParamType.SCALAR, 2, "{0} != {1}"),
    GREATER_THAN("Greater", DataType.NUMBER, ParamType.SCALAR, 2, "{0} > {1}"),
    GREATER_OR_EQUAL("GreaterOrEqual", DataType.NUMBER, ParamType.SCALAR, 2, "{0} >= {1}"),
    LESS_THAN("Less", DataType.NUMBER, ParamType.SCALAR, 2, "{0} < {1}"),
    LESS_OR_EQUAL("LessOrEqual", DataType.NUMBER, ParamType.SCALAR, 2, "{0} <= {1}"),
    BETWEEN("Between", DataType.NUMBER, ParamType.RANGE, 3, "{0} >= {1} and {0} <= {2}"),
    IN_LIST("In", DataType.NUMBER, ParamType.LIST, 2, "{0} in [{1}]"),
    NOT_IN_LIST("NotIn", DataType.NUMBER, ParamType.LIST, 2, "not({0} in [{1}])"),

    // String Operators (case-insensitive default)
    STRING_EQUALS("Equals", DataType.STRING, ParamType.SCALAR, 2, "lower case({0}) = {1}"),
    STRING_NOT_EQUALS("NotEquals", DataType.STRING, ParamType.SCALAR, 2, "lower case({0}) != {1}"),
    STRING_IN_LIST("In", DataType.STRING, ParamType.LIST, 2, "lower case({0}) in [{1}]"),
    CONTAINS("Contains", DataType.STRING, ParamType.SCALAR, 2, "contains(lower case({0}), {1})"),
    STARTS_WITH("StartsWith", DataType.STRING, ParamType.SCALAR, 2, "starts with(lower case({0}), {1})"),
    ENDS_WITH("EndsWith", DataType.STRING, ParamType.SCALAR, 2, "ends with(lower case({0}), {1})"),
    MATCHES("Matches", DataType.STRING, ParamType.SCALAR, 2, "matches({0}, {1}, \"i\")"),

    // Date Operators
    DATE_EQUALS("Equals", DataType.DATE, ParamType.SCALAR, 2, "date({0}) = date({1})"),
            DATE_GREATER_THAN("Greater", DataType.DATE, ParamType.SCALAR, 2, "date({0}) > date({1})"),
    DATE_GREATER_OR_EQUAL("GreaterOrEqual", DataType.DATE, ParamType.SCALAR, 2, "date({0}) >= date({1})"),
    DATE_LESS_THAN("Less", DataType.DATE, ParamType.SCALAR, 2, "date({0}) < date({1})"),
    DATE_LESS_OR_EQUAL("LessOrEqual", DataType.DATE, ParamType.SCALAR, 2, "date({0}) <= date({1})"),
    DATE_BETWEEN("Between", DataType.DATE, ParamType.RANGE, 3, "date({0}) >= date({1}) and date({0}) <= date({2})"),

    // Generic Operators
    IS_NULL("IsNull", DataType.ANY, ParamType.NONE, 1, "{0} = null"),
    IS_NOT_NULL("IsNotNull", DataType.ANY, ParamType.NONE, 1, "{0} != null"),
    EXISTS("Exists", DataType.ANY, ParamType.NONE, 1, "{0} != null"),
    NOT("Not", DataType.ANY, ParamType.SCALAR, 1, "not({0})"), // Assuming scalar for generic NOT, though arguably
                                                               // usually boolean
    LIST_CONTAINS("ListContains", DataType.ANY, ParamType.SCALAR, 2, "list contains({0}, {1})");

    private final String operator;
    private final DataType dataType;
    private final ParamType paramType;
    private final int argCount;
    private final String pattern;

    private static final Map<String, FeelTemplate> LOOKUP = buildLookup();

    FeelTemplate(String operator, DataType dataType, ParamType paramType, int argCount, String pattern) {
        this.operator = operator;
        this.dataType = dataType;
        this.paramType = paramType;
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
     * Apply template using a list of raw values, applying extraction logic based on
     * ParamType.
     */
    /**
     * Apply template using a list of raw values, applying extraction logic based on
     * ParamType.
     */
    /**
     * Apply template using a list of raw values, applying extraction logic based on ParamType.
     */
    public String apply(String field, java.util.List<String> values) {
        Object[] args;
        switch (paramType) {
            case SCALAR:
                String val = (values != null && !values.isEmpty()) ? values.get(0) : null;
                args = new Object[]{parseValueIfNeeded(val)};
                break;
            case LIST:
                // Parse elements if needed (e.g. string to number)
                java.util.List<Object> finalValues = (values == null) ? java.util.Collections.emptyList() :
                        values.stream().map(this::parseValueIfNeeded).collect(Collectors.toList());
                args = new Object[]{finalValues};
                break;
            case RANGE:
                String val1 = (values != null && !values.isEmpty()) ? values.get(0) : null;
                String val2 = (values != null && values.size() > 1) ? values.get(1) : null;
                args = new Object[]{parseValueIfNeeded(val1), parseValueIfNeeded(val2)};
                break;
            case NONE:
                args = new Object[0];
                break;
            default:
                throw new IllegalStateException("Unknown ParamType: " + paramType);
        }
        return applyRaw(field, args);
    }

    private Object parseValueIfNeeded(String val) {
        if (val == null) return null;
        if (dataType == DataType.NUMBER) {
            try {
                return Double.parseDouble(val);
            } catch (NumberFormatException e) {
                // ignore
            }
        }
        return val;
    }

    /**
     * Apply template to field and values.
     * <p>
     * Example: apply("amount", 100) -> "amount = 100"
     */
    public String apply(String field, Object... values) {
        return applyRaw(field, values);
    }

    private String applyRaw(String field, Object[] values) {
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

    public ParamType getParamType() {
        return paramType;
    }
    public int getArgCount() { return argCount; }
    public String getPattern() { return pattern; }
}
