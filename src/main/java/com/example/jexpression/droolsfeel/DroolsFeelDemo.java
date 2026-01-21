package com.example.jexpression.droolsfeel;

import com.example.jexpression.droolsfeel.converter.RuleConverter;
import com.example.jexpression.droolsfeel.model.FeelRule;
import com.example.jexpression.droolsfeel.model.ValidationRule;
import com.example.jexpression.model.Transaction;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Demo: Load JSON → Convert to FEEL → Pass DTO → Get Results
 */
@Component
public class DroolsFeelDemo {

    private final FeelRuleEngine engine;
    private final RuleConverter converter;
    private final ObjectMapper mapper;

    public DroolsFeelDemo(FeelRuleEngine engine, RuleConverter converter, ObjectMapper mapper) {
        this.engine = engine;
        this.converter = converter;
        this.mapper = mapper;
    }

    public void run() throws Exception {
        System.out.println("\n=== FEEL Rule Engine (Final) ===\n");

        // ========================================
        // STEP 1: Load JSON rules
        // ========================================
        System.out.println("Step 1: Loading JSON rules...");
        var input = new ClassPathResource("feel-raw-input.json").getInputStream();
        var wrapper = mapper.readValue(input, new TypeReference<Map<String, List<ValidationRule>>>() {
        });
        List<ValidationRule> rawRules = wrapper.get("rules");
        System.out.println("  Loaded: " + rawRules.size() + " rules\n");

        // ========================================
        // STEP 2: Convert to FEEL (combined expressions)
        // ========================================
        System.out.println("Step 2: Converting to FEEL expressions...");
        List<FeelRule> feelRules = converter.convert(rawRules);

        feelRules.forEach(rule -> System.out.println("  " + rule.code() + ": " + rule.expression()));
        System.out.println();

        // ========================================
        // STEP 3: Create Transaction DTO
        // ========================================
        System.out.println("Step 3: Creating Transaction DTO...");
        Transaction tx = new Transaction();
        tx.setAmount(50.0);
        tx.setMessageType("pain.001");
        tx.setBeneficiaryIban("SA1234567890");
        tx.setPurposeCode("PRDM_POP_AE");
        tx.setRequestedExecutionDate("2025-11-15");
        tx.setCountry("SA");
        tx.setChannel("SWIFT");

        System.out.println("  Transaction: amount=" + tx.getAmount() +
                ", messageType=" + tx.getMessageType() +
                ", country=" + tx.getCountry() + "\n");

        // ========================================
        // STEP 4: Evaluate (pass FEEL rules + Transaction DTO)
        // ========================================
        System.out.println("Step 4: Evaluating rules against Transaction DTO...");
        List<String> failures = engine.validate(feelRules, tx, "transaction");

        if (failures.isEmpty()) {
            System.out.println("  ✓ All validations PASSED");
        } else {
            System.out.println("  Failed rules:");
            failures.forEach(code -> System.out.println("    ✗ " + code));
        }

        System.out.println("\n=== Demo Complete ===\n");
    }
}
