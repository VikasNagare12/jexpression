package com.example.jexpression.droolsfeel.ast;

public enum DataType {
    STRING, NUMBER, BOOLEAN, DATE, ANY;

    public Object parse(String v) {
        if (v == null) return null;
        return switch (this) {
            case NUMBER -> Double.parseDouble(v);
            case BOOLEAN -> Boolean.parseBoolean(v);
            default -> v;
        };
    }
}
