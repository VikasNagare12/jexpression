package com.example.jexpression.service;

import com.example.jexpression.model.EvaluationResult;
import com.example.jexpression.model.FeelRule;
import com.example.jexpression.model.Transaction;
import com.example.jexpression.repository.RuleRepository;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * Transaction evaluation service using cached rules.
 * DB-free, lock-free, fast evaluation.
 */
@Service
public class TransactionRuleService {

    private static final Logger log = LoggerFactory.getLogger(TransactionRuleService.class);
    private static final String CONTEXT_NAME = "transaction";

    private final RuleRepository ruleRepository;
    private final FeelRuleService feelRuleService;

    public TransactionRuleService(RuleRepository ruleRepository, FeelRuleService feelRuleService) {
        this.ruleRepository = ruleRepository;
        this.feelRuleService = feelRuleService;
    }

    /**
     * Evaluate transaction against cached rules for its country/currency.
     * Returns empty list if no rules found.
     */
    public List<EvaluationResult> evaluate(Transaction transaction) {
        Validate.notNull(transaction, "transaction must not be null");

        String country = transaction.getCountry();
        String currency = transaction.getCurrency();

        if (country == null || currency == null) {
            log.warn("Transaction has null country or currency, skipping evaluation");
            return Collections.emptyList();
        }

        // Lock-free read from cache
        List<FeelRule> rules = ruleRepository.getRules(country, currency);

        if (rules.isEmpty()) {
            log.debug("No rules found for {}/{}", country, currency);
            return Collections.emptyList();
        }

        log.debug("Evaluating {} rules for {}/{}", rules.size(), country, currency);

        return feelRuleService.evaluate(rules, transaction, CONTEXT_NAME);
    }

    /**
     * Get failed rule codes for transaction.
     */
    public List<String> getFailedCodes(Transaction transaction) {
        return evaluate(transaction).stream()
                .filter(EvaluationResult::failed)
                .map(EvaluationResult::ruleCode)
                .toList();
    }

    /**
     * Check if transaction passes all rules.
     */
    public boolean validateTransaction(Transaction transaction) {
        List<EvaluationResult> results = evaluate(transaction);
        return results.stream().allMatch(EvaluationResult::passed);
    }
}
