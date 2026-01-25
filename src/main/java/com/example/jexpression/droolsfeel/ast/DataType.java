package com.example.jexpression.droolsfeel.ast;

public enum DataType {
    STRING, NUMBER, BOOLEAN, DATE, ANY;

    public Object parse(String v) {
        if (v == null) return null;
        return switch (this) {
            case NUMBER -> {
                try {
                    yield Double.parseDouble(v);
                } catch (NumberFormatException e) {
                    // Fallback or rethrow a better error?
                    // For a robust system, we might want to return the string as-is
                    // or null, but let's assume we want to avoid crashing.
                    // Returning the raw string might be safer than crashing.
                    yield v;
                }
            }
            case BOOLEAN -> Boolean.parseBoolean(v);
            default -> v;
        };
    }
}
