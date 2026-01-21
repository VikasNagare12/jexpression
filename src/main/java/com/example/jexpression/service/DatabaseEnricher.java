package com.example.jexpression.service;

import com.example.jexpression.model.Transaction;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class DatabaseEnricher implements DataEnricher {

    @Override
    public Map<String, Object> enrich(Transaction transaction) {
        // Simulate a Database Lookup based on Transaction content
        // e.g., SELECT * FROM Customer WHERE id = transaction.customerId

        Map<String, Object> dbData = new HashMap<>();

        // Mocking a database result: Daily Limit for the user
        // In a real app, you would inject a Repository and call it here.
        Map<String, Object> limits = new HashMap<>();
        limits.put("dailyLimit", 1000.0); // Fetched from DB

        dbData.put("db", limits);

        return dbData;
    }
}
