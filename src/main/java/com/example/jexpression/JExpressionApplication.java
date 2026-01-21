package com.example.jexpression;

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
		System.out.println("--- Starting Rule Evaluation ---");

		// 1. Read Rule from JSON file
		InputStream inputStream = new ClassPathResource("rule.json").getInputStream();
		Rule rule = objectMapper.readValue(inputStream, Rule.class);
		System.out.println("Loaded Rule: " + rule.getRuleId());

		// 2. Create Match Data
		Transaction matchData = new Transaction();
		matchData.setCountry("SA");
		matchData.setChannel("SWIFT");
		matchData.setTransactionDate("2026-06-15"); // Within range

		Payment payment = new Payment();
		Amount amount = new Amount();
		amount.setValue(29.0);
		amount.setCurrency("SAR");
		payment.setAmount(amount);
		matchData.setPayment(payment);

		// 3. Evaluate Match
		boolean matchResult = ruleService.evaluate(rule, matchData);
		System.out.println("Evaluation Result (Match Data): " + matchResult);
		if (matchResult) {
			System.out.println("Action: " + rule.getAction().getStatus() + " - " + rule.getAction().getReasonCode());
		}

		// 4. Create Index Mismatch Data (Channel = DIFFERENT)
		Transaction indexMismatchData = new Transaction();
		indexMismatchData.setCountry("SA");
		indexMismatchData.setChannel("DIFFERENT"); // Index mismatch
		indexMismatchData.setTransactionDate("2026-06-15");
		indexMismatchData.setPayment(payment);

		boolean indexMismatchResult = ruleService.evaluate(rule, indexMismatchData);
		System.out.println("Evaluation Result (Index Mismatch Data): " + indexMismatchResult);

		// 5. Create Logic Mismatch Data (Value > Limit 1000)
		Transaction logicMismatchData = new Transaction();
		logicMismatchData.setCountry("SA");
		logicMismatchData.setChannel("SWIFT");
		logicMismatchData.setTransactionDate("2026-06-15");

		Payment p3 = new Payment();
		Amount a3 = new Amount();
		a3.setValue(5000.0); // > 1000
		a3.setCurrency("SAR");
		p3.setAmount(a3);
		logicMismatchData.setPayment(p3);

		boolean logicMismatchResult = ruleService.evaluate(rule, logicMismatchData);
		System.out.println("Evaluation Result (Logic Mismatch Data): " + logicMismatchResult);

		// 6. Create Date Mismatch Data (Outside Range)
		Transaction dateMismatchData = new Transaction();
		dateMismatchData.setCountry("SA");
		dateMismatchData.setChannel("SWIFT");
		dateMismatchData.setTransactionDate("2025-12-31"); // Too old

		Payment p4 = new Payment();
		Amount a4 = new Amount();
		a4.setValue(50.0);
		a4.setCurrency("SAR");
		p4.setAmount(a4);
		dateMismatchData.setPayment(p4);

		boolean dateMismatchResult = ruleService.evaluate(rule, dateMismatchData);
		System.out.println("Evaluation Result (Date Mismatch Data): " + dateMismatchResult);

		System.out.println("--- Evaluation Completed ---");

		// Stop the system
		System.exit(SpringApplication.exit(context, () -> 0));
	}

}
