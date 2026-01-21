package com.example.jexpression.droolsfeel.model;

import java.util.List;

/**
 * Single validation condition.
 */
public record Validation(
    String field,
    String type,
    String op,
    String source,
    List<String> values
) {}
