package com.example.jexpression;

import com.example.jexpression.model.Action;
import com.example.jexpression.model.Rule;
import com.example.jexpression.model.Transaction;
import com.example.jexpression.model.Payment;
import com.example.jexpression.model.Amount;
import com.example.jexpression.service.RuleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class RuleServiceTest {

  private ObjectMapper objectMapper;
  private RuleService ruleService;

  @BeforeEach
  public void setup() {
    objectMapper = new ObjectMapper();
    ruleService = new RuleService(objectMapper, java.util.Collections.emptyList());
  }

  @Test
  void testEvaluateRule_Match() throws Exception {
    // Construct the Rule object from the user's JSON example
    String ruleJson = """
            {
              "ruleId": "R_SA_AMOUNT_CHECK",
              "version": 1,
              "priority": 10,
              "status": "ACTIVE",
              "effectiveFrom": "2026-01-01T00:00:00Z",
              "effectiveTo": "9999-12-31T23:59:59Z",
              "index": {
                "country": ["SA"],
                "channel": ["SWIFT"],
                "currency": ["SAR"]
              },
              "logic": {
                "and": [
                  { "<":  [ { "var": "payment.amount.value" }, 30 ] },
                  { "==": [ { "var": "payment.amount.currency" }, "SAR" ] }
                ]
              },
              "action": {
                "status": "REJECT",
                "reasonCode": "SA01"
              }
            }
        """;

    Rule rule = objectMapper.readValue(ruleJson, Rule.class);

    // precise match data
    Amount amount = new Amount();
    amount.setValue(29.0);
    amount.setCurrency("SAR");

    Payment payment = new Payment();
    payment.setAmount(amount);

    Transaction data = new Transaction();
    data.setPayment(payment);

    boolean matched = ruleService.evaluate(rule, data);

    assertTrue(matched, "Rule should match");
  }

  @Test
  void testEvaluateRule_NoMatch() throws Exception {
    // Same rule
    String ruleJson = """
            {
              "logic": {
                "and": [
                  { "<":  [ { "var": "payment.amount.value" }, 30 ] },
                  { "==": [ { "var": "payment.amount.currency" }, "SAR" ] }
                ]
              },
              "action": { "status": "REJECT", "reasonCode": "SA01" }
            }
        """;
    Rule rule = objectMapper.readValue(ruleJson, Rule.class);

    // Non-matching data (amount > 30)
    Amount amount = new Amount();
    amount.setValue(50.0);
    amount.setCurrency("SAR");

    Payment payment = new Payment();
    payment.setAmount(amount);

    Transaction data = new Transaction();
    data.setPayment(payment);

    boolean matched = ruleService.evaluate(rule, data);

    assertFalse(matched, "Rule should not match");
  }
}
