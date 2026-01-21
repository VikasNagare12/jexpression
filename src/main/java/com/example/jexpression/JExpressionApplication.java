package com.example.jexpression;

import com.example.jexpression.model.Action;
import com.example.jexpression.model.Amount;
import com.example.jexpression.model.Payment;
import com.example.jexpression.model.Rule;
import com.example.jexpression.model.Transaction;
import com.example.jexpression.service.RuleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.util.Optional;

@SpringBootApplication
public class JExpressionApplication implements CommandLineRunner {

	private final RuleService ruleService;
	private final ObjectMapper objectMapper;
	private final ApplicationContext context;

	public JExpressionApplication(RuleService ruleService, ObjectMapper objectMapper, ApplicationContext context) {
		this.ruleService = ruleService;
		this.objectMapper = objectMapper;
		this.context = context;
	}

	public static void main(String[] args) {
		SpringApplication.run(JExpressionApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("=== Rule Engine - Returns Action DTO ===\n");

		InputStream inputStream = new ClassPathResource("rule.json").getInputStream();
		Rule rule = objectMapper.readValue(inputStream, Rule.class);
		System.out.println("Rule: " + rule.getRuleId());

		// Test 1: Filter ✓ Logic ✓ → Get Action
		System.out.println("\n--- Test 1: SA, SWIFT, amount=50 ---");
		Optional<Action> action1 = ruleService.evaluate(rule, createTransaction("SA", "SWIFT", 50.0, "SAR"));
		printAction(action1);

		// Test 2: Filter ✗ → Skip
		System.out.println("\n--- Test 2: AE, SWIFT, amount=50 ---");
		Optional<Action> action2 = ruleService.evaluate(rule, createTransaction("AE", "SWIFT", 50.0, "SAR"));
		printAction(action2);

		// Test 3: Filter ✓ Logic ✗ → No Action
		System.out.println("\n--- Test 3: SA, SWIFT, amount=150 ---");
		Optional<Action> action3 = ruleService.evaluate(rule, createTransaction("SA", "SWIFT", 150.0, "SAR"));
		printAction(action3);

		System.out.println("\n=== Demo Complete ===");
		System.exit(SpringApplication.exit(context, () -> 0));
	}

	private void printAction(Optional<Action> action) {
		if (action.isPresent()) {
			Action a = action.get();
			System.out.println("Action: " + a.getStatus() + " - " + a.getReasonCode());
		} else {
			System.out.println("No Action (skipped or logic didn't match)");
		}
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
