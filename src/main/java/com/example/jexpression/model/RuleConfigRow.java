package com.example.jexpression.model;

import java.time.LocalDateTime;

/**
 * Database row from rule_config table.
 * Immutable data carrier only.
 */
public record RuleConfigRow(
        Long id,
        String country,
        String currency,
        String rulesJson,
        boolean enabled,
        LocalDateTime updatedAt) {
}
