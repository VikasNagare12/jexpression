package com.example.jexpression.droolsfeel.converter;

import java.util.Optional;

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
