# Consolidating Codebase

This document contains the complete source code for the project, organized by package.

---

## 1. Package: `com.example.jexpression.ast`

### 1.1 `Expression.java`
```java
package com.example.jexpression.ast;

/**
 * Sealed AST Node hierarchy for FEEL expressions.
 * Enforces exhaustiveness in Visitors.
 */
public sealed interface Expression
        permits Expression.Field, Expression.Literal, Expression.List, Expression.Unary, Expression.Binary {

    /**
     * Accept a visitor.
     * Kept for visitor pattern mechanics.
     */
    <R> R accept(ExpressionVisitor<R> visitor);

    /**
     * Process this expression with a visitor.
     * Alias for accept() to improve readability.
     */
    default <R> R processWith(ExpressionVisitor<R> visitor) {
        return accept(visitor);
    }

    /**
     * Infer the data type of this node.
     */
    DataType type();

    /**
     * Represents a field reference (e.g., "transaction.amount").
     */
    record Field(String name, DataType type) implements Expression {
        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) {
            return visitor.handleField(this);
        }
    }

    /**
     * Represents a literal value (e.g., 100, "Active").
     */
    record Literal(String rawValue, DataType type) implements Expression {
        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) {
            return visitor.handleLiteral(this);
        }
    }

    /**
     * Represents a list of values (e.g., [1, 2, 3]).
     */
    record List(java.util.List<Expression> elements, DataType type) implements Expression {
        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) {
            return visitor.handleList(this);
        }
    }

    /**
     * Represents a unary operation (e.g., not(x), lower case(x)).
     */
    record Unary(ExpressionOperator operator, Expression operand) implements Expression {
        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) {
            return visitor.handleUnary(this);
        }

        @Override
        public DataType type() {
            // Logic operators return BOOLEAN, others depend on context
            if (operator == ExpressionOperator.NOT)
                return DataType.BOOLEAN;
            return operand.type();
        }
    }

    /**
     * Represents a binary operation (e.g., x = y, x > y).
     */
    record Binary(ExpressionOperator operator, Expression left, Expression right) implements Expression {
        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) {
            return visitor.handleBinary(this);
        }

        @Override
        public DataType type() {
            // Comparison/Logic returns BOOLEAN.
            // Can be enhanced if we support arithmetic.
            return DataType.BOOLEAN;
        }
    }
}
```

### 1.2 `ExpressionOperator.java`
```java
package com.example.jexpression.ast;

import java.util.function.Predicate;

/**
 * FEEL Operators with rich metadata for validation and rendering.
 * Refactored to use behavioral type validation instead of collection-based
 * checks.
 */
public enum ExpressionOperator {
    // Comparison
    EQUALS("=", TypeRules::isComparable, false),
    NOT_EQUALS("!=", TypeRules::isComparable, false),
    GREATER(">", TypeRules::isOrdered, false),
    GREATER_OR_EQUAL(">=", TypeRules::isOrdered, false),
    LESS("<", TypeRules::isOrdered, false),
    LESS_OR_EQUAL("<=", TypeRules::isOrdered, false),

    // Logic
    AND("and", TypeRules::isBoolean, false),
    OR("or", TypeRules::isBoolean, false),
    NOT("not", TypeRules::isBoolean, true), // Render as function: not(x)

    // Collection / String
    IN("in", TypeRules::allowAll, false),
    CONTAINS("contains", TypeRules::isString, true),
    STARTS_WITH("starts with", TypeRules::isString, true),
    ENDS_WITH("ends with", TypeRules::isString, true),
    LOWER_CASE("lower case", TypeRules::isString, true);

    private final String symbol;
    private final Predicate<DataType> typeValidator;
    private final boolean isFunctionStyle;

    ExpressionOperator(String symbol, Predicate<DataType> typeValidator, boolean isFunctionStyle) {
        this.symbol = symbol;
        this.typeValidator = typeValidator;
        this.isFunctionStyle = isFunctionStyle;
    }

    public String symbol() {
        return symbol;
    }

    public boolean isSupportedFor(DataType type) {
        return typeValidator.test(type);
    }

    public boolean isFunctionStyle() {
        return isFunctionStyle;
    }

    /**
     * Render the operator expression.
     */
    public String toFeelExpression(String left, String right) {
        if (isFunctionStyle) {
            // Function call style: op(left, right) or op(left)
            if (right == null) {
                return symbol + "(" + left + ")"; // Unary function
            }
            return symbol + "(" + left + ", " + right + ")"; // Binary function
        } else {
            // Infix style: left op right
            if (right == null) {
                return symbol + " " + left; // Unary prefix
            }
            return left + " " + symbol + " " + right;
        }
    }

    /**
     * Centralized type compatibility rules.
     * Replaces Sets and Wildcards with explicit, readable behavior.
     */
    private static class TypeRules {
        // Any non-void/non-unknown type is generally comparable for equality
        static boolean isComparable(DataType t) {
            return t != DataType.ANY;
        }

        static boolean isOrdered(DataType t) {
            return t == DataType.NUMBER || t == DataType.DATE;
        }

        static boolean isBoolean(DataType t) {
            return t == DataType.BOOLEAN;
        }

        static boolean isString(DataType t) {
            return t == DataType.STRING;
        }

        static boolean allowAll(DataType t) {
            return true;
        }
    }
}
```

### 1.3 `ExpressionVisitor.java`
```java
package com.example.jexpression.ast;

public interface ExpressionVisitor<R> {
    R handleField(Expression.Field expression);

    R handleLiteral(Expression.Literal expression);

    R handleList(Expression.List expression);

    R handleUnary(Expression.Unary expression);

    R handleBinary(Expression.Binary expression);
}
```

### 1.4 `ExpressionValidator.java`
```java
package com.example.jexpression.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * Validates AST semantic correctness (type compatibility, operator usage).
 */
public class ExpressionValidator implements ExpressionVisitor<List<String>> {

    public static List<String> validateExpression(Expression expression) {
        return expression.processWith(new ExpressionValidator());
    }

    @Override
    public List<String> handleField(Expression.Field expression) {
        // Field validation typically requires a context/schema, skipping for now.
        return List.of();
    }

    @Override
    public List<String> handleLiteral(Expression.Literal expression) {
        // Validate literal structure if needed (e.g. date format)
        return List.of();
    }

    @Override
    public List<String> handleList(Expression.List expression) {
        List<String> errors = new ArrayList<>();
        for (Expression element : expression.elements()) {
            errors.addAll(element.processWith(this));
        }
        return errors;
    }

    @Override
    public List<String> handleUnary(Expression.Unary expression) {
        List<String> errors = new ArrayList<>(expression.operand().processWith(this));

        if (!expression.operator().isSupportedFor(expression.operand().type())) {
            errors.add("Generic Error: Operator '" + expression.operator().symbol() +
                    "' does not support type " + expression.operand().type());
        }
        return errors;
    }

    @Override
    public List<String> handleBinary(Expression.Binary expression) {
        List<String> errors = new ArrayList<>();
        errors.addAll(expression.left().processWith(this));
        errors.addAll(expression.right().processWith(this));

        // Check compatibility
        DataType leftType = expression.left().type();

        if (!expression.operator().isSupportedFor(leftType)) {
            errors.add("Operator '" + expression.operator().symbol() +
                    "' does not support left operand type " + leftType);
        }

        // Special case: IN operator (Left=Any, Right=List)
        if (expression.operator() == ExpressionOperator.IN) {
            if (!(expression.right() instanceof Expression.List)) {
                errors.add("IN operator requires a List on the right side");
            } else if (expression.left().type() != expression.right().type()) {
                errors.add("Type mismatch: Cannot check if " + expression.left().type() + " is in List<"
                        + expression.right().type() + ">");
            }
        } else {
            // Strict type equality for all other binary operators
            DataType rightType = expression.right().type();
            if (leftType != rightType) {
                errors.add("Type mismatch: Operator '" + expression.operator().symbol() +
                        "' cannot compare " + leftType + " with " + rightType);
            }
        }

        return errors;
    }
}
```

### 1.5 `ExpressionToFeelRenderer.java`
```java
package com.example.jexpression.ast;

import java.util.stream.Collectors;

/**
 * Renders AST to FEEL expression strings using the Visitor pattern.
 */
public class ExpressionToFeelRenderer implements ExpressionVisitor<String> {

    public static String toFeel(Expression expression) {
        return expression.processWith(new ExpressionToFeelRenderer());
    }

    @Override
    public String handleField(Expression.Field expression) {
        return expression.name();
    }

    @Override
    public String handleLiteral(Expression.Literal expression) {
        return expression.type().formatLiteral(expression.rawValue());
    }

    @Override
    public String handleList(Expression.List expression) {
        String elements = expression.elements().stream()
                .map(e -> e.processWith(this))
                .collect(Collectors.joining(", "));
        return "[" + elements + "]";
    }

    @Override
    public String handleUnary(Expression.Unary expression) {
        String operand = expression.operand().processWith(this);
        return expression.operator().toFeelExpression(operand, null);
    }

    @Override
    public String handleBinary(Expression.Binary expression) {
        String left = expression.left().processWith(this);
        String right = expression.right().processWith(this);
        return expression.operator().toFeelExpression(left, right);
    }
}
```

### 1.6 `ExpressionBuilder.java`
```java
package com.example.jexpression.ast;

import java.util.List;

/**
 * Factory for creating valid FEEL AST nodes.
 */
public final class ExpressionBuilder {

    private ExpressionBuilder() {
    }

    public static Expression.Field field(String name, DataType type) {
        return new Expression.Field(name, type);
    }

    public static Expression.Literal literal(String rawValue, DataType t) {
        return new Expression.Literal(rawValue, t);
    }

    public static Expression.List list(DataType t, List<String> rawValues) {
        if (rawValues == null) {
            return new Expression.List(List.of(), t);
        }
        List<Expression> elements = rawValues.stream()
                .map(v -> (Expression) new Expression.Literal(v, t))
                .toList();
        return new Expression.List(elements, t);
    }

    public static Expression.Binary equalTo(Expression l, Expression r) {
        return new Expression.Binary(ExpressionOperator.EQUALS, l, r);
    }

    public static Expression.Binary greaterThan(Expression l, Expression r) {
        return new Expression.Binary(ExpressionOperator.GREATER, l, r);
    }

    public static Expression.Binary greaterThanOrEqualTo(Expression l, Expression r) {
        return new Expression.Binary(ExpressionOperator.GREATER_OR_EQUAL, l, r);
    }

    public static Expression.Binary lessThan(Expression l, Expression r) {
        return new Expression.Binary(ExpressionOperator.LESS, l, r);
    }

    public static Expression.Binary lessThanOrEqualTo(Expression l, Expression r) {
        return new Expression.Binary(ExpressionOperator.LESS_OR_EQUAL, l, r);
    }

    public static Expression.Binary and(Expression l, Expression r) {
        return new Expression.Binary(ExpressionOperator.AND, l, r);
    }

    public static Expression.Binary or(Expression l, Expression r) {
        return new Expression.Binary(ExpressionOperator.OR, l, r);
    }

    public static Expression.Unary not(Expression n) {
        return new Expression.Unary(ExpressionOperator.NOT, n);
    }

    public static Expression.Unary lowerCase(Expression n) {
        return new Expression.Unary(ExpressionOperator.LOWER_CASE, n);
    }

    public static Expression.Binary inList(Expression l, Expression.List r) {
        return new Expression.Binary(ExpressionOperator.IN, l, r);
    }

    public static Expression.Binary contains(Expression l, Expression r) {
        return new Expression.Binary(ExpressionOperator.CONTAINS, l, r);
    }

    public static Expression.Binary startsWith(Expression l, Expression r) {
        return new Expression.Binary(ExpressionOperator.STARTS_WITH, l, r);
    }

    public static Expression.Binary endsWith(Expression l, Expression r) {
        return new Expression.Binary(ExpressionOperator.ENDS_WITH, l, r);
    }
}
```

### 1.7 `DataType.java`
```java
package com.example.jexpression.ast;

/**
 * Supported data types in the FEEL engine with centralized formatting logic.
 */
public enum DataType {
    STRING {
        @Override
        public String formatLiteral(Object val) {
            return quote(String.valueOf(val));
        }
    },
    NUMBER {
        @Override
        public String formatLiteral(Object val) {
            // Numbers are raw (no quotes)
            return String.valueOf(val);
        }
    },
    BOOLEAN {
        @Override
        public String formatLiteral(Object val) {
            // Booleans are raw
            return String.valueOf(val);
        }
    },
    DATE {
        @Override
        public String formatLiteral(Object val) {
            // Dates are wrapped: date("2023-01-01")
            return "date(" + quote(String.valueOf(val)) + ")";
        }
    },
    ANY {
        @Override
        public String formatLiteral(Object val) {
            return quote(String.valueOf(val));
        }
    };

    /**
     * Format a raw value into a valid FEEL syntax string literal.
     * e.g. "foo" -> "\"foo\""
     * 100 -> "100"
     */
    public abstract String formatLiteral(Object val);

    private static String quote(String s) {
        if (s == null || "null".equals(s))
            return "null";
        // Escape quotes and wrap
        return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }
}
```

---

## 2. Package: `com.example.jexpression.mapper`

### 2.1 `RuleConditionToExpressionMapper.java`
```java
package com.example.jexpression.mapper;

import com.example.jexpression.ast.DataType;
import com.example.jexpression.ast.ExpressionBuilder;
import com.example.jexpression.ast.Expression;
import com.example.jexpression.ast.ExpressionToFeelRenderer;
import com.example.jexpression.model.RuleCondition;

import java.util.List;

public final class RuleConditionToExpressionMapper {

    private RuleConditionToExpressionMapper() {
    }

    /**
     * Convert RuleCondition to FEEL expression string using AST.
     */
    public static String toFeel(RuleCondition c) {
        Expression node = mapToExpression(c);
        return ExpressionToFeelRenderer.toFeel(node);
    }

    public static Expression mapToExpression(RuleCondition c) {
        String field = c.field();
        String op = c.op();
        DataType type;
        try {
            type = DataType.valueOf(c.type() == null ? "STRING" : c.type().toUpperCase());
        } catch (IllegalArgumentException e) {
            type = DataType.STRING;
        }
        List<String> values = c.values();

        // Handle Lower Case logic for Strings
        if (type == DataType.STRING) {
            // Apply lower case function to field
            Expression fieldNode = ExpressionBuilder.lowerCase(ExpressionBuilder.field(field, type));

            // Lower case values
            values = values == null ? null
                    : values.stream()
                            .map(v -> v == null ? null : v.toLowerCase())
                            .toList();

            return buildExpressionInternal(op, type, fieldNode, values);
        }

        return buildExpressionInternal(op, type, ExpressionBuilder.field(field, type), values);
    }

    private static Expression buildExpressionInternal(String op, DataType type, Expression fieldNode,
            List<String> values) {
        // Validate or normalize operator if needed
        String normalizedOp = op.trim().toUpperCase();

        return switch (normalizedOp) {
            case "EQUALS" -> ExpressionBuilder.equalTo(fieldNode, ExpressionBuilder.literal(first(values), type));
            case "NOTEQUALS" -> ExpressionBuilder
                    .not(ExpressionBuilder.equalTo(fieldNode, ExpressionBuilder.literal(first(values), type)));
            case "GREATER" -> ExpressionBuilder.greaterThan(fieldNode, ExpressionBuilder.literal(first(values), type));
            case "GREATEROREQUAL" ->
                ExpressionBuilder.greaterThanOrEqualTo(fieldNode, ExpressionBuilder.literal(first(values), type));
            case "LESS" -> ExpressionBuilder.lessThan(fieldNode, ExpressionBuilder.literal(first(values), type));
            case "LESSOREQUAL" ->
                ExpressionBuilder.lessThanOrEqualTo(fieldNode, ExpressionBuilder.literal(first(values), type));

            case "BETWEEN" -> {
                Expression val1 = ExpressionBuilder.literal(first(values), type);
                Expression val2 = ExpressionBuilder.literal(second(values), type);
                yield ExpressionBuilder.and(ExpressionBuilder.greaterThanOrEqualTo(fieldNode, val1),
                        ExpressionBuilder.lessThanOrEqualTo(fieldNode, val2));
            }

            case "IN" -> ExpressionBuilder.inList(fieldNode, ExpressionBuilder.list(type, values));
            case "NOTIN" ->
                ExpressionBuilder.not(ExpressionBuilder.inList(fieldNode, ExpressionBuilder.list(type, values)));

            case "CONTAINS" -> ExpressionBuilder.contains(fieldNode, ExpressionBuilder.literal(first(values), type));
            case "STARTSWITH" ->
                ExpressionBuilder.startsWith(fieldNode, ExpressionBuilder.literal(first(values), type));
            case "ENDSWITH" -> ExpressionBuilder.endsWith(fieldNode, ExpressionBuilder.literal(first(values), type));

            case "ISNULL" -> ExpressionBuilder.equalTo(fieldNode, ExpressionBuilder.literal(null, type));
            case "ISNOTNULL", "EXISTS" ->
                ExpressionBuilder.not(ExpressionBuilder.equalTo(fieldNode, ExpressionBuilder.literal(null, type)));

            default -> throw new IllegalArgumentException("Unsupported operator: " + op);
        };
    }

    private static String first(List<String> values) {
        return (values != null && !values.isEmpty()) ? values.get(0) : null;
    }

    private static String second(List<String> values) {
        return (values != null && values.size() > 1) ? values.get(1) : null;
    }
}
```

---

## 3. Package: `com.example.jexpression.model`

### 3.1 `EvaluationResult.java`
```java
package com.example.jexpression.model;

/**
 * Structured result of rule evaluation.
 *
 * @param ruleCode            Unique rule identifier
 * @param ruleName            Human-readable name
 * @param expression          FEEL expression evaluated
 * @param status              Evaluation outcome
 * @param errorMessage        Error details if applicable
 * @param evaluationTimeNanos Execution time in nanoseconds
 */
public record EvaluationResult(
        String ruleCode,
        String ruleName,
        String expression,
        Status status,
        String errorMessage,
        long evaluationTimeNanos) {

    public enum Status {
        PASSED, FAILED, ERROR, SKIPPED
    }

    // ─────────────────────────────────────────────────────────────
    // Factory Methods
    // ─────────────────────────────────────────────────────────────

    public static EvaluationResult passed(FeelRule rule, long nanos) {
        return new EvaluationResult(
                rule.code(), rule.name(), rule.expression(), Status.PASSED, null, nanos);
    }

    public static EvaluationResult failed(FeelRule rule, long nanos) {
        return new EvaluationResult(
                rule.code(), rule.name(), rule.expression(), Status.FAILED, null, nanos);
    }

    public static EvaluationResult error(FeelRule rule, String error, long nanos) {
        return new EvaluationResult(
                rule.code(), rule.name(), rule.expression(), Status.ERROR, error, nanos);
    }

    public static EvaluationResult skipped(FeelRule rule, String reason) {
        return new EvaluationResult(
                rule.code(), rule.name(), rule.expression(), Status.SKIPPED, reason, 0);
    }

    // ─────────────────────────────────────────────────────────────
    // Convenience Methods
    // ─────────────────────────────────────────────────────────────

    public boolean passed() {
        return status == Status.PASSED;
    }

    public boolean failed() {
        return status == Status.FAILED;
    }

    public boolean isError() {
        return status == Status.ERROR;
    }

    public boolean isSkipped() {
        return status == Status.SKIPPED;
    }

    /** Evaluation time in milliseconds. */
    public double evaluationTimeMs() {
        return evaluationTimeNanos / 1_000_000.0;
    }
}
```

### 3.2 `FeelRule.java`
```java
package com.example.jexpression.model;

import org.kie.dmn.feel.lang.CompiledExpression;

/**
 * Immutable rule with pre-compiled FEEL expression.
 * 
 * <p>
 * Expressions are compiled at load time for:
 * <ul>
 * <li>Fail-fast syntax validation</li>
 * <li>~2-5x faster repeated evaluations</li>
 * <li>Thread-safe evaluation</li>
 * </ul>
 * 
 * @param code               Unique rule identifier
 * @param name               Human-readable description
 * @param expression         FEEL validation expression
 * @param compiledExpression Pre-compiled expression (null if failed)
 * @param isValid            True if compilation succeeded
 */
public record FeelRule(
        String code,
        String name,
        String expression,
        CompiledExpression compiledExpression,
        boolean isValid) {

    /**
     * Create a valid rule with compiled expression.
     */
    public static FeelRule valid(String code, String name, String expression,
            CompiledExpression compiled) {
        return new FeelRule(code, name, expression, compiled, true);
    }

    /**
     * Create an invalid rule (compilation failed).
     */
    public static FeelRule invalid(String code, String name, String expression) {
        return new FeelRule(code, name, expression, null, false);
    }

    /**
     * Check if rule can be evaluated.
     */
    public boolean canEvaluate() {
        return isValid && compiledExpression != null;
    }
}
```

### 3.3 `RuleCondition.java`
```java
package com.example.jexpression.model;

import java.util.List;

/**
 * Single condition within a rule.
 * 
 * @param field  Field path (e.g., "transaction.amount")
 * @param type   Data type: "string", "number", "date", "boolean"
 * @param op     Operator name (e.g., "Equals", "GreaterOrEqual")
 * @param source Value source: "static", "prdm", "config"
 * @param values Comparison values
 */
public record RuleCondition(
        String field,
        String type,
        String op,
        String source,
        List<String> values) {

    public boolean isDate() {
        return "date".equalsIgnoreCase(type);
    }

    public boolean isNumber() {
        return "number".equalsIgnoreCase(type);
    }

    public boolean isString() {
        return "string".equalsIgnoreCase(type) || type == null;
    }

    public String firstValue() {
        return values != null && !values.isEmpty() ? values.get(0) : null;
    }

    public String secondValue() {
        return values != null && values.size() > 1 ? values.get(1) : null;
    }
}
```

### 3.4 `RuleDefinition.java`
```java
package com.example.jexpression.model;

import java.util.List;

/**
 * Rule definition from JSON configuration.
 * 
 * @param code       Unique rule identifier
 * @param name       Human-readable description
 * @param status     "Enabled" or "Disabled"
 * @param conditions List of conditions to evaluate
 */
public record RuleDefinition(
        String code,
        String name,
        String status,
        List<RuleCondition> conditions) {

    public boolean isEnabled() {
        return "Enabled".equalsIgnoreCase(status);
    }

    public boolean hasConditions() {
        return conditions != null && !conditions.isEmpty();
    }
}
```

### 3.5 `Transaction.java`
```java
package com.example.jexpression.model;

/**
 * Transaction DTO for FEEL rule evaluation.
 */
public class Transaction {
    private String country;
    private String channel;
    private String transactionDate;
    private Double amount;
    private String messageType;
    private String beneficiaryIban;
    private String purposeCode;
    private String requestedExecutionDate;
    private String currency;

    // Getters and Setters
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getBeneficiaryIban() {
        return beneficiaryIban;
    }

    public void setBeneficiaryIban(String beneficiaryIban) {
        this.beneficiaryIban = beneficiaryIban;
    }

    public String getPurposeCode() {
        return purposeCode;
    }

    public void setPurposeCode(String purposeCode) {
        this.purposeCode = purposeCode;
    }

    public String getRequestedExecutionDate() {
        return requestedExecutionDate;
    }

    public void setRequestedExecutionDate(String requestedExecutionDate) {
        this.requestedExecutionDate = requestedExecutionDate;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
```

---

## 4. Package: `com.example.jexpression.service`

### 4.1 `FeelRuleEngine.java`
```java
package com.example.jexpression.service;

import com.example.jexpression.model.EvaluationResult;
import com.example.jexpression.model.FeelRule;
import org.apache.commons.lang3.Validate;
import org.kie.dmn.feel.FEEL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Production-ready FEEL Rule Evaluation Engine.
 * Thread-safe, with timing metrics.
 */
@Service
public class FeelRuleEngine {

    private static final Logger log = LoggerFactory.getLogger(FeelRuleEngine.class);

    // Thread-safe FEEL instance per thread
    private static final ThreadLocal<FEEL> FEEL_INSTANCE = ThreadLocal.withInitial(FEEL::newInstance);

    // ─────────────────────────────────────────────────────────────
    // Public API
    // ─────────────────────────────────────────────────────────────

    /**
     * Evaluate all rules and return structured results.
     */
    public List<EvaluationResult> evaluate(List<FeelRule> rules, Object dto, String contextName) {
        Validate.notNull(rules, "rules must not be null");
        Validate.notNull(dto, "dto must not be null");
        Validate.notBlank(contextName, "contextName must not be blank");

        var context = Map.of(contextName, dto);
        var results = new ArrayList<EvaluationResult>(rules.size());

        for (var rule : rules) {
            results.add(evaluateRule(rule, context));
        }

        logSummary(results);
        return results;
    }

    /**
     * Evaluate rules and return only failed rule codes.
     */
    public List<String> getFailedCodes(List<FeelRule> rules, Object dto, String contextName) {
        return evaluate(rules, dto, contextName).stream()
                .filter(EvaluationResult::failed)
                .map(EvaluationResult::ruleCode)
                .toList();
    }

    /**
     * Evaluate rules and return only failures (including errors).
     */
    public List<EvaluationResult> getFailures(List<FeelRule> rules, Object dto, String contextName) {
        return evaluate(rules, dto, contextName).stream()
                .filter(r -> !r.passed())
                .toList();
    }

    /**
     * Evaluate a single rule.
     */
    public EvaluationResult evaluateSingle(FeelRule rule, Object dto, String contextName) {
        return evaluateRule(rule, Map.of(contextName, dto));
    }

    // ─────────────────────────────────────────────────────────────
    // Internal
    // ─────────────────────────────────────────────────────────────

    private EvaluationResult evaluateRule(FeelRule rule, Map<String, Object> context) {
        if (!rule.canEvaluate()) {
            log.warn("Skipping invalid rule: {}", rule.code());
            return EvaluationResult.skipped(rule, "Rule compilation failed");
        }

        var startNanos = System.nanoTime();

        try {
            var feel = FEEL_INSTANCE.get();
            var result = feel.evaluate(rule.compiledExpression(), context);
            var elapsed = System.nanoTime() - startNanos;

            var passed = Boolean.TRUE.equals(result);

            if (log.isDebugEnabled()) {
                log.debug("Rule [{}] → {} ({}ms)",
                        rule.code(), passed ? "PASS" : "FAIL", elapsed / 1_000_000.0);
            }

            return passed
                    ? EvaluationResult.passed(rule, elapsed)
                    : EvaluationResult.failed(rule, elapsed);

        } catch (Exception e) {
            var elapsed = System.nanoTime() - startNanos;
            log.error("Rule [{}] error: {}", rule.code(), e.getMessage());
            return EvaluationResult.error(rule, e.getMessage(), elapsed);
        }
    }

    private void logSummary(List<EvaluationResult> results) {
        var passed = results.stream().filter(EvaluationResult::passed).count();
        var failed = results.stream().filter(EvaluationResult::failed).count();
        var errors = results.stream().filter(EvaluationResult::isError).count();
        var skipped = results.stream().filter(EvaluationResult::isSkipped).count();

        log.info("Evaluation complete: {} passed, {} failed, {} errors, {} skipped",
                passed, failed, errors, skipped);
    }
}
```

### 4.2 `RuleConverter.java`
```java
package com.example.jexpression.service;

import com.example.jexpression.mapper.RuleConditionToExpressionMapper;
import com.example.jexpression.model.FeelRule;
import com.example.jexpression.model.RuleDefinition;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.lang.CompiledExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Converts rule definitions to compiled FEEL rules.
 */
@Component
public class RuleConverter {

    private static final Logger log = LoggerFactory.getLogger(RuleConverter.class);

    private final FEEL feel = FEEL.newInstance();

    /**
     * Convert rule definitions to compiled FEEL rules.
     */
    public List<FeelRule> convert(List<RuleDefinition> definitions) {
        log.info("Converting {} rule definitions to FEEL", definitions.size());

        List<FeelRule> rules = definitions.stream()
                .filter(RuleDefinition::isEnabled)
                .filter(RuleDefinition::hasConditions)
                .map(this::toFeelRule)
                .toList();

        long valid = rules.stream().filter(FeelRule::isValid).count();
        log.info("Conversion complete: {} rules ({} valid, {} invalid)",
                rules.size(), valid, rules.size() - valid);

        return rules;
    }

    private FeelRule toFeelRule(RuleDefinition def) {
        String expression = def.conditions().stream()
                .map(RuleConditionToExpressionMapper::toFeel)
                .collect(Collectors.joining(" and "));

        CompiledExpression compiled = compileExpression(def.code(), expression);

        if (compiled != null) {
            log.debug("Rule [{}] compiled: {}", def.code(), expression);
            return FeelRule.valid(def.code(), def.name(), expression, compiled);
        } else {
            log.error("Rule [{}] FAILED to compile: {}", def.code(), expression);
            return FeelRule.invalid(def.code(), def.name(), expression);
        }
    }

    private CompiledExpression compileExpression(String ruleCode, String expression) {
        if (expression == null || expression.isBlank()) {
            return null;
        }

        try {
            return feel.compile(expression, feel.newCompilerContext());
        } catch (Exception e) {
            log.error("Compilation error for [{}]: {}", ruleCode, e.getMessage());
            return null;
        }
    }
}
```

### 4.3 `RuleLoadingService.java`
```java
package com.example.jexpression.service;

import com.example.jexpression.model.FeelRule;
import com.example.jexpression.model.RuleDefinition;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Service for loading and compiling rules.
 */
@Service
public class RuleLoadingService {

    private static final Logger log = LoggerFactory.getLogger(RuleLoadingService.class);

    private final ObjectMapper objectMapper;
    private final RuleConverter ruleConverter;

    public RuleLoadingService(ObjectMapper objectMapper, RuleConverter ruleConverter) {
        this.objectMapper = objectMapper;
        this.ruleConverter = ruleConverter;
    }

    /**
     * Load rules from classpath resource.
     */
    public List<FeelRule> loadFromClasspath(String resourcePath) {
        log.info("Loading rules from classpath: {}", resourcePath);

        try (InputStream input = new ClassPathResource(resourcePath).getInputStream()) {
            return loadFromStream(input);
        } catch (IOException e) {
            throw new RuleLoadingException("Failed to load from classpath: " + resourcePath, e);
        }
    }

    /**
     * Load rules from file system.
     */
    public List<FeelRule> loadFromFile(Path filePath) {
        log.info("Loading rules from file: {}", filePath);

        try (InputStream input = Files.newInputStream(filePath)) {
            return loadFromStream(input);
        } catch (IOException e) {
            throw new RuleLoadingException("Failed to load from file: " + filePath, e);
        }
    }

    /**
     * Load rules from JSON string.
     */
    public List<FeelRule> loadFromJson(String jsonContent) {
        log.info("Loading rules from JSON string ({} chars)", jsonContent.length());

        try {
            List<RuleDefinition> definitions = parseJsonString(jsonContent);
            return ruleConverter.convert(definitions);
        } catch (IOException e) {
            throw new RuleLoadingException("Failed to parse JSON content", e);
        }
    }

    /**
     * Load rules from input stream.
     */
    public List<FeelRule> loadFromStream(InputStream input) throws IOException {
        List<RuleDefinition> definitions = parseStream(input);
        return ruleConverter.convert(definitions);
    }

    // ─────────────────────────────────────────────────────────────
    // Parsing
    // ─────────────────────────────────────────────────────────────

    private List<RuleDefinition> parseStream(InputStream input) throws IOException {
        Map<String, List<RuleDefinition>> wrapper = objectMapper.readValue(
                input, new TypeReference<>() {
                });
        return extractRules(wrapper);
    }

    private List<RuleDefinition> parseJsonString(String json) throws IOException {
        Map<String, List<RuleDefinition>> wrapper = objectMapper.readValue(
                json, new TypeReference<>() {
                });
        return extractRules(wrapper);
    }

    private List<RuleDefinition> extractRules(Map<String, List<RuleDefinition>> wrapper) {
        List<RuleDefinition> rules = wrapper.get("rules");
        if (rules == null) {
            throw new RuleLoadingException("JSON must contain 'rules' array");
        }
        log.debug("Parsed {} rule definitions", rules.size());
        return rules;
    }

    /**
     * Exception thrown when rule loading fails.
     */
    public static class RuleLoadingException extends RuntimeException {
        public RuleLoadingException(String message) {
            super(message);
        }

        public RuleLoadingException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
```
