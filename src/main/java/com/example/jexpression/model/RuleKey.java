package com.example.jexpression.model;

/**
 * Cache key for rule lookup.
 * Normalizes country and currency to uppercase, trimmed.
 */
public record RuleKey(String country, String currency) {

    /**
     * Canonical constructor with normalization.
     */
    public RuleKey {
        country = normalize(country);
        currency = normalize(currency);
    }

    /**
     * Factory method for clarity.
     */
    public static RuleKey of(String country, String currency) {
        return new RuleKey(country, currency);
    }

    private static String normalize(String value) {
        if (value == null) return "";
        return value.trim().toUpperCase();
    }
}
