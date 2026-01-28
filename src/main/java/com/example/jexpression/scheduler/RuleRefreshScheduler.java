package com.example.jexpression.scheduler;

import com.example.jexpression.model.FeelRule;
import com.example.jexpression.model.RuleConfigRow;
import com.example.jexpression.model.RuleKey;
import com.example.jexpression.repository.RuleConfigJdbcRepository;
import com.example.jexpression.repository.RuleRepository;
import com.example.jexpression.service.RuleLoadingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Scheduled task to refresh rule cache from database every hour.
 * Atomic cache replacement on success, no update on failure.
 */
@Component
public class RuleRefreshScheduler {

    private static final Logger log = LoggerFactory.getLogger(RuleRefreshScheduler.class);

    private final RuleConfigJdbcRepository jdbcRepository;
    private final RuleLoadingService ruleLoadingService;
    private final RuleRepository ruleRepository;

    public RuleRefreshScheduler(
            RuleConfigJdbcRepository jdbcRepository,
            RuleLoadingService ruleLoadingService,
            RuleRepository ruleRepository) {
        this.jdbcRepository = jdbcRepository;
        this.ruleLoadingService = ruleLoadingService;
        this.ruleRepository = ruleRepository;
    }

    /**
     * Refresh rule cache every hour.
     * Runs with fixed delay of 1 hour after previous completion.
     */
    @Scheduled(fixedDelayString = "PT1H", initialDelayString = "PT10S")
    public void refreshRules() {
        log.info("Starting rule cache refresh");

        try {
            // 1. Load enabled rows from database
            List<RuleConfigRow> rows = jdbcRepository.findEnabledRules();
            log.info("Loaded {} enabled rule configurations", rows.size());

            // 2. Build new cache map
            Map<RuleKey, List<FeelRule>> newCache = new HashMap<>();

            for (RuleConfigRow row : rows) {
                try {
                    // Parse and compile rules for this row
                    List<FeelRule> compiledRules = ruleLoadingService.loadFromJson(row.rulesJson());
                    RuleKey key = RuleKey.of(row.country(), row.currency());

                    newCache.put(key, compiledRules);

                    log.debug("Loaded {} rules for {}/{}",
                            compiledRules.size(), row.country(), row.currency());

                } catch (Exception e) {
                    log.error("Failed to compile rules for row {}: {}", row.id(), e.getMessage());
                    // Continue processing other rows
                }
            }

            // 3. Atomically replace cache
            ruleRepository.replaceAll(newCache);

            log.info("Rule cache refresh complete: {} keys loaded", newCache.size());

        } catch (Exception e) {
            log.error("Rule cache refresh FAILED: {}", e.getMessage(), e);
            // DO NOT update cache on failure
        }
    }
}
