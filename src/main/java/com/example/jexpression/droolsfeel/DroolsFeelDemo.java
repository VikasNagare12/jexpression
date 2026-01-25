package com.example.jexpression.droolsfeel;

import com.example.jexpression.droolsfeel.model.EvaluationResult;
import com.example.jexpression.droolsfeel.model.FeelRule;
import com.example.jexpression.droolsfeel.service.FeelRuleEngine;
import com.example.jexpression.droolsfeel.service.RuleLoadingService;
import com.example.jexpression.model.Transaction;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Production FEEL Rule Engine Demo.
 * 
 * <p>
 * Demonstrates: Load → Compile → Evaluate → Results
 */
@Component
public class DroolsFeelDemo {

    private static final String RULES_PATH = "feel-raw-input.json";

    private final RuleLoadingService ruleLoadingService;
    private final FeelRuleEngine engine;

    public DroolsFeelDemo(RuleLoadingService ruleLoadingService, FeelRuleEngine engine) {
        this.ruleLoadingService = ruleLoadingService;
        this.engine = engine;
    }

    public void run() {
        printHeader();

        // ══════════════════════════════════════════════════════════════
        // STEP 1: Load and compile rules
        // ══════════════════════════════════════════════════════════════
        printStep(1, "Loading rules from JSON");
        List<FeelRule> rules = ruleLoadingService.loadFromClasspath(RULES_PATH);
        printRules(rules);

        // ══════════════════════════════════════════════════════════════
        // STEP 2: Create test transaction
        // ══════════════════════════════════════════════════════════════
        printStep(2, "Creating Transaction DTO");
        Transaction tx = createTestTransaction();
        printTransaction(tx);

        // ══════════════════════════════════════════════════════════════
        // STEP 3: Evaluate rules
        // ══════════════════════════════════════════════════════════════
        printStep(3, "Evaluating rules against transaction");
        List<EvaluationResult> results = engine.evaluate(rules, tx, "transaction");
        printResults(results);

        // ══════════════════════════════════════════════════════════════
        // STEP 4: Quick API demo
        // ══════════════════════════════════════════════════════════════
        printStep(4, "Quick API - Get failed codes");
        List<String> failedCodes = engine.getFailedCodes(rules, tx, "transaction");
        printFailedCodes(failedCodes);

        printFooter();
    }

    // ─────────────────────────────────────────────────────────────
    // Test Data
    // ─────────────────────────────────────────────────────────────

    private Transaction createTestTransaction() {
        Transaction tx = new Transaction();
        tx.setAmount(50.0);
        tx.setMessageType("pain.001");
        tx.setBeneficiaryIban("SA1234567890");
        tx.setPurposeCode("PRDM_POP_AE");
        tx.setRequestedExecutionDate("2025-11-15");
        tx.setCountry("SA");
        tx.setChannel("SWIFT");
        return tx;
    }

    // ─────────────────────────────────────────────────────────────
    // Pretty Printing
    // ─────────────────────────────────────────────────────────────

    private void printHeader() {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║           DROOLS FEEL RULE ENGINE - PRODUCTION DEMO          ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();
    }

    private void printFooter() {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                       DEMO COMPLETE                          ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();
    }

    private void printStep(int num, String title) {
        System.out.println();
        System.out.printf("▶ STEP %d: %s%n", num, title);
        System.out.println("─".repeat(60));
    }

    private void printRules(List<FeelRule> rules) {
        long valid = rules.stream().filter(FeelRule::isValid).count();
        System.out.printf("  Loaded: %d rules (%d valid)%n%n", rules.size(), valid);

        for (FeelRule rule : rules) {
            String status = rule.isValid() ? "✓" : "✗";
            System.out.printf("  [%s] %-20s │ %s%n",
                    status, rule.code(), truncate(rule.expression(), 40));
        }
    }

    private void printTransaction(Transaction tx) {
        System.out.println("  {");
        System.out.printf("    amount         : %.2f%n", tx.getAmount());
        System.out.printf("    messageType    : %s%n", tx.getMessageType());
        System.out.printf("    country        : %s%n", tx.getCountry());
        System.out.printf("    purposeCode    : %s%n", tx.getPurposeCode());
        System.out.printf("    executionDate  : %s%n", tx.getRequestedExecutionDate());
        System.out.printf("    beneficiaryIban: %s%n", tx.getBeneficiaryIban());
        System.out.println("  }");
    }

    private void printResults(List<EvaluationResult> results) {
        for (EvaluationResult r : results) {
            String status = switch (r.status()) {
                case PASSED -> "✓ PASS";
                case FAILED -> "✗ FAIL";
                case ERROR -> "⚠ ERR ";
                case SKIPPED -> "○ SKIP";
            };
            System.out.printf("  [%s] %-20s (%.2fms)%n",
                    status, r.ruleCode(), r.evaluationTimeMs());
        }

        System.out.println();
        long passed = results.stream().filter(EvaluationResult::passed).count();
        long failed = results.stream().filter(EvaluationResult::failed).count();
        System.out.printf("  Summary: %d passed, %d failed%n", passed, failed);
    }

    private void printFailedCodes(List<String> codes) {
        if (codes.isEmpty()) {
            System.out.println("  ✓ All validations PASSED");
        } else {
            System.out.println("  ✗ Failed rules: " + codes);
        }
    }

    private String truncate(String str, int max) {
        if (str == null)
            return "null";
        return str.length() <= max ? str : str.substring(0, max) + "…";
    }
}
