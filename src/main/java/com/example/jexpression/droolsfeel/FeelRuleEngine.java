package com.example.jexpression.droolsfeel;

import com.example.jexpression.droolsfeel.model.FeelRule;
import org.kie.dmn.feel.FEEL;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * FEEL Rule Engine.
 * 
 * Evaluates pre-converted FEEL rules against a DTO.
 */
@Service
public class FeelRuleEngine {

    private final FEEL feel = FEEL.newInstance();

    /**
     * Validate a DTO against FEEL rules.
     * 
     * @param rules       List of rules with combined FEEL expressions
     * @param dto         Your DTO object (e.g., Transaction)
     * @param contextName Key name for FEEL context (e.g., "transaction")
     * @return List of failed rule codes (empty = all passed)
     */
    public List<String> validate(List<FeelRule> rules, Object dto, String contextName) {
        Objects.requireNonNull(dto, "dto must not be null");
        Objects.requireNonNull(contextName, "contextName must not be null");

        Map<String, Object> context = new HashMap<>();
        context.put(contextName, dto);

        return rules.stream()
                .filter(rule -> !evaluate(rule.expression(), context))
                .map(FeelRule::code)
                .toList();
    }

    private boolean evaluate(String expression, Map<String, Object> context) {
        try {
            return Boolean.TRUE.equals(feel.evaluate(expression, context));
        } catch (Exception e) {
            System.err.println("FEEL error: " + e.getMessage());
            return false;
        }
    }
}
