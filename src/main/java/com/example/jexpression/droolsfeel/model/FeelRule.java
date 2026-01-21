package com.example.jexpression.droolsfeel.model;

import java.util.List;

/**
 * Rule with pre-converted, combined FEEL expression.
 */
public record FeelRule(
        String code,
        String name,
        String expression // Combined FEEL expression (e.g., "a >= 30 and b != null")
) {
}
