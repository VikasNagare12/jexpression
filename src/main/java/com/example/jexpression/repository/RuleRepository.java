package com.example.jexpression.repository;

import com.example.jexpression.model.FeelRule;
import com.example.jexpression.model.RuleKey;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Thread-safe in-memory rule cache.
 * Uses AtomicReference for lock-free reads with atomic updates.
 */
@Repository
public class RuleRepository {

    private final AtomicReference<Map<RuleKey, List<FeelRule>>> cache =
            new AtomicReference<>(Collections.emptyMap());

    /**
     * Get rules for country and currency.
     * Lock-free read. Returns empty list if not found.
     */
    public List<FeelRule> getRules(String country, String currency) {
        RuleKey key = RuleKey.of(country, currency);
        Map<RuleKey, List<FeelRule>> current = cache.get();
        return current.getOrDefault(key, Collections.emptyList());
    }

    /**
     * Atomically replace entire cache.
     * Copy-on-write pattern for thread safety.
     */
    public void replaceAll(Map<RuleKey, List<FeelRule>> newRules) {
        if (newRules == null) {
            cache.set(Collections.emptyMap());
        } else {
            // Create defensive immutable copy
            Map<RuleKey, List<FeelRule>> immutableCopy = Map.copyOf(newRules);
            cache.set(immutableCopy);
        }
    }

    /**
     * Get current cache snapshot for debugging/monitoring.
     */
    public Map<RuleKey, List<FeelRule>> getSnapshot() {
        return cache.get();
    }
}
