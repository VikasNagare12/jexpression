package com.example.jexpression.service;

import com.example.jexpression.model.Transaction;
import java.util.Map;

public interface DataEnricher {
    /**
     * Enrich the rule evaluation context with extra data based on the transaction.
     * 
     * @param transaction The current transaction.
     * @return A map of extra variables (e.g., "customerProfile" -> { ... }).
     */
    Map<String, Object> enrich(Transaction transaction);
}
