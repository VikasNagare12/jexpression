package com.example.jexpression;


import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class JExpressionApplication implements CommandLineRunner {

	private final DroolsFeelDemo demo;
	private final ApplicationContext context;

	public JExpressionApplication(DroolsFeelDemo demo, ApplicationContext context) {
		this.demo = demo;
		this.context = context;
	}

	public static void main(String[] args) {
		SpringApplication.run(JExpressionApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		demo.run();
		System.exit(SpringApplication.exit(context, () -> 0));
	}
}
