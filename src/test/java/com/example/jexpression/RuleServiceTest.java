package com.example.jexpression;

import com.example.jexpression.model.Action;
import com.example.jexpression.model.Amount;
import com.example.jexpression.model.Payment;
import com.example.jexpression.model.Rule;
import com.example.jexpression.model.Transaction;
import com.example.jexpression.service.RuleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class RuleServiceTest {

  private ObjectMapper objectMapper;
  private RuleService ruleService;

    @BeforeEach
    void setup() {
      objectMapper = new ObjectMapper();
        ruleService = new RuleService(objectMapper);
      }

    @Test
    void testEvaluate_FilterMatchLogicMatch_ReturnsAction() throws Exception {
      Rule rule = createRule();
      Transaction tx = createTransaction("SA", "SWIFT", 50.0, "SAR");

      Optional<Action> result = ruleService.evaluate(rule, tx);

      assertTrue(result.isPresent());
      assertEquals("REJECT", result.get().getStatus());
    }

    @Test
    void testEvaluate_FilterMismatch_ReturnsEmpty() throws Exception {
      Rule rule = createRule();
      Transaction tx = createTransaction("AE", "SWIFT", 50.0, "SAR");

        Optional<Action> result = ruleService.evaluate(rule, tx);

        assertTrue(result.isEmpty());
      }

    @Test
    void testEvaluate_FilterMatchLogicMismatch_ReturnsEmpty() throws Exception {
      Rule rule = createRule();
      Transaction tx = createTransaction("SA", "SWIFT", 150.0, "SAR");

        Optional<Action> result = ruleService.evaluate(rule, tx);

        assertTrue(result.isEmpty());
      }

    private Rule createRule() {
      Rule rule = new Rule();
      rule.setRuleId("TEST_RULE");
      rule.setIndex(Map.of("country", List.of("SA"), "channel", List.of("SWIFT")));
      rule.setLogic(Map.of("<", List.of(Map.of("var", "payment.amount.value"), 100)));

      Action action = new Action();
      action.setStatus("REJECT");
      action.setReasonCode("TEST01");
      rule.setAction(action);

      return rule;
    }

    private Transaction createTransaction(String country, String channel, double amount, String currency) {
      Transaction tx = new Transaction();
      tx.setCountry(country);
      tx.setChannel(channel);

        Payment payment = new Payment();
        Amount amt = new Amount();
        amt.setValue(amount);
        amt.setCurrency(currency);
        payment.setAmount(amt);
        tx.setPayment(payment);

        return tx;
      }
}
