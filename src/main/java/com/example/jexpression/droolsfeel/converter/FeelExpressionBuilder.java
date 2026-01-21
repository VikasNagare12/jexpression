package com.example.jexpression.droolsfeel.converter;

import com.example.jexpression.droolsfeel.model.Validation;

import java.util.stream.Collectors;

/**
 * Converts Validation to FEEL expression.
 */
public final class FeelExpressionBuilder {

    private FeelExpressionBuilder() {}

    public static String toFeel(Validation v) {
        return switch (v.op()) {
            case "Between" -> between(v);
            case "Exists" -> "%s != null".formatted(v.field());
            case "Matches" -> "matches(%s, \"%s\")".formatted(v.field(), escape(v.values().get(0)));
            case "GreaterOrEqual" -> "%s >= %s".formatted(field(v), value(v, 0));
            case "LessOrEqual" -> "%s <= %s".formatted(field(v), value(v, 0));
            case "Greater" -> "%s > %s".formatted(field(v), value(v, 0));
            case "Less" -> "%s < %s".formatted(field(v), value(v, 0));
            case "Equals" -> "%s = %s".formatted(field(v), value(v, 0));
            case "NotEquals" -> "%s != %s".formatted(field(v), value(v, 0));
            case "In" -> "%s in [%s]".formatted(v.field(), inList(v));
            default -> throw new IllegalArgumentException("Unknown operator: " + v.op());
        };
    }

    private static String between(Validation v) {
        var f = field(v);
        var from = value(v, 0);
        var to = value(v, 1);
        return "%s >= %s and %s <= %s".formatted(f, from, f, to);
    }

    /**
     * Wrap field with date() if type is date.
     */
    private static String field(Validation v) {
        if ("date".equals(v.type())) {
            return "date(%s)".formatted(v.field());
        }
        return v.field();
    }

    private static String inList(Validation v) {
        return v.values().stream()
                .map(val -> "\"" + val + "\"")
                .collect(Collectors.joining(", "));
    }

    private static String value(Validation v, int index) {
        var val = v.values().get(index);
        return switch (v.type()) {
            case "number" -> val;
            case "date" -> "date(\"%s\")".formatted(val);
            default -> "\"%s\"".formatted(val);
        };
    }

    private static String escape(String s) {
        return s.replace("\\", "\\\\");
    }
}
