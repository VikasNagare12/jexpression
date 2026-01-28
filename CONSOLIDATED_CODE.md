# Simplified FEEL Rule Mapper - Complete Codebase

Production-ready codebase for the simplified FEEL rule mapper.

---

## `pom.xml`
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.4.1</version>
    </parent>
    <groupId>com.example</groupId>
    <artifactId>jexpression</artifactId>
    <version>0.0.1-SNAPSHOT</version>

    <properties>
        <java.version>17</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.kie</groupId>
            <artifactId>kie-dmn-feel</artifactId>
            <version>7.74.0.Final</version>
        </dependency>
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-lang3</artifactId>
        </dependency>
    </dependencies>
</project>
```

---

## `com.example.jexpression.ast`

### DataType.java
```java
package com.example.jexpression.ast;

/**
 * Data types with centralized literal formatting for FEEL expressions.
 */
public enum DataType {
    STRING {
        @Override
        public String formatLiteral(String value) {
            if (value == null) return "null";
            return "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
        }
    },
    NUMBER {
        @Override
        public String formatLiteral(String value) { return value; }
    },
    BOOLEAN {
        @Override
        public String formatLiteral(String value) { return value; }
    },
    DATE {
        @Override
        public String formatLiteral(String value) {
            if (value == null) return "null";
            return "date(\"" + value + "\")";
        }
    };

    public abstract String formatLiteral(String value);

    public static DataType from(String type) {
        if (type == null) return STRING;
        try {
            return valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            return STRING;
        }
    }
}
```

### ExpressionOperator.java
```java
package com.example.jexpression.ast;

import java.util.List;

/**
 * FEEL operators with self-contained expression generation.
 * Expects pre-formatted literal values. Operator strings must be normalized before calling from().
 */
public enum ExpressionOperator {

    EQUALS {
        @Override
        public String buildFeelExpression(String field, List<String> formattedValues) {
            requireValues(formattedValues, "EQUALS");
            return field + " = " + first(formattedValues);
        }
    },
    NOT_EQUALS {
        @Override
        public String buildFeelExpression(String field, List<String> formattedValues) {
            requireValues(formattedValues, "NOT_EQUALS");
            return field + " != " + first(formattedValues);
        }
    },
    GREATER {
        @Override
        public String buildFeelExpression(String field, List<String> formattedValues) {
            requireValues(formattedValues, "GREATER");
            return field + " > " + first(formattedValues);
        }
    },
    GREATER_OR_EQUAL {
        @Override
        public String buildFeelExpression(String field, List<String> formattedValues) {
            requireValues(formattedValues, "GREATER_OR_EQUAL");
            return field + " >= " + first(formattedValues);
        }
    },
    LESS {
        @Override
        public String buildFeelExpression(String field, List<String> formattedValues) {
            requireValues(formattedValues, "LESS");
            return field + " < " + first(formattedValues);
        }
    },
    LESS_OR_EQUAL {
        @Override
        public String buildFeelExpression(String field, List<String> formattedValues) {
            requireValues(formattedValues, "LESS_OR_EQUAL");
            return field + " <= " + first(formattedValues);
        }
    },
    BETWEEN {
        @Override
        public String buildFeelExpression(String field, List<String> formattedValues) {
            requireMinValues(formattedValues, 2, "BETWEEN");
            return field + " >= " + first(formattedValues) + " and " + field + " <= " + second(formattedValues);
        }
    },
    IN {
        @Override
        public String buildFeelExpression(String field, List<String> formattedValues) {
            requireValues(formattedValues, "IN");
            return field + " in [" + String.join(", ", formattedValues) + "]";
        }
    },
    NOT_IN {
        @Override
        public String buildFeelExpression(String field, List<String> formattedValues) {
            requireValues(formattedValues, "NOT_IN");
            return "not(" + field + " in [" + String.join(", ", formattedValues) + "])";
        }
    },
    CONTAINS {
        @Override
        public String buildFeelExpression(String field, List<String> formattedValues) {
            requireValues(formattedValues, "CONTAINS");
            return "contains(" + field + ", " + first(formattedValues) + ")";
        }
    },
    STARTS_WITH {
        @Override
        public String buildFeelExpression(String field, List<String> formattedValues) {
            requireValues(formattedValues, "STARTS_WITH");
            return "starts with(" + field + ", " + first(formattedValues) + ")";
        }
    },
    ENDS_WITH {
        @Override
        public String buildFeelExpression(String field, List<String> formattedValues) {
            requireValues(formattedValues, "ENDS_WITH");
            return "ends with(" + field + ", " + first(formattedValues) + ")";
        }
    },
    IS_NULL {
        @Override
        public String buildFeelExpression(String field, List<String> formattedValues) {
            return field + " = null";
        }
    },
    IS_NOT_NULL {
        @Override
        public String buildFeelExpression(String field, List<String> formattedValues) {
            return field + " != null";
        }
    };

    public abstract String buildFeelExpression(String field, List<String> formattedValues);

    public static ExpressionOperator from(String normalizedOp) {
        if (normalizedOp == null) throw new IllegalArgumentException("Operator cannot be null");

        return switch (normalizedOp) {
            case "EQUALS" -> EQUALS;
            case "NOTEQUALS" -> NOT_EQUALS;
            case "GREATER" -> GREATER;
            case "GREATEROREQUAL" -> GREATER_OR_EQUAL;
            case "LESS" -> LESS;
            case "LESSOREQUAL" -> LESS_OR_EQUAL;
            case "BETWEEN" -> BETWEEN;
            case "IN" -> IN;
            case "NOTIN" -> NOT_IN;
            case "CONTAINS" -> CONTAINS;
            case "STARTSWITH" -> STARTS_WITH;
            case "ENDSWITH" -> ENDS_WITH;
            case "ISNULL" -> IS_NULL;
            case "ISNOTNULL", "EXISTS" -> IS_NOT_NULL;
            default -> throw new IllegalArgumentException("Unknown operator: " + normalizedOp);
        };
    }

    private static void requireValues(List<String> values, String name) {
        if (values == null || values.isEmpty()) throw new IllegalArgumentException(name + " requires at least one value");
    }

    private static void requireMinValues(List<String> values, int min, String name) {
        if (values == null || values.size() < min) throw new IllegalArgumentException(name + " requires at least " + min + " values");
    }

    private static String first(List<String> v) { return v.get(0); }
    private static String second(List<String> v) { return v.get(1); }
}
```

---

## `com.example.jexpression.mapper`

### RuleConditionMapper.java
```java
package com.example.jexpression.mapper;

import com.example.jexpression.ast.DataType;
import com.example.jexpression.ast.ExpressionOperator;
import com.example.jexpression.model.FeelRule;
import com.example.jexpression.model.RuleCondition;
import com.example.jexpression.model.RuleDefinition;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.lang.CompiledExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Single entry point for all rule conversion.
 * Normalizes all values at the boundary before passing to operators.
 */
@Component
public class RuleConditionMapper {

    private static final Logger log = LoggerFactory.getLogger(RuleConditionMapper.class);
    private static final String CONDITION_JOINER = " and ";

    private final FEEL feel = FEEL.newInstance();

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
                .map(RuleConditionMapper::toFeel)
                .collect(Collectors.joining(CONDITION_JOINER));

        CompiledExpression compiled = compileExpression(def.code(), expression);

        return compiled != null
                ? FeelRule.valid(def.code(), def.name(), expression, compiled)
                : FeelRule.invalid(def.code(), def.name(), expression);
    }

    private CompiledExpression compileExpression(String ruleCode, String expression) {
        if (expression == null || expression.isBlank()) return null;
        try {
            return feel.compile(expression, feel.newCompilerContext());
        } catch (Exception e) {
            log.error("Compilation error for [{}]: {}", ruleCode, e.getMessage());
            return null;
        }
    }

    public static String toFeel(RuleCondition condition) {
        DataType type = DataType.from(condition.type());
        String normalizedOp = normalizeOperator(condition.op());
        ExpressionOperator operator = ExpressionOperator.from(normalizedOp);

        String field = wrapFieldForCaseInsensitiveMatch(condition.field(), type);
        List<String> formattedValues = formatLiterals(condition.values(), type);

        return operator.buildFeelExpression(field, formattedValues);
    }

    private static String normalizeOperator(String op) {
        return op == null ? null : op.trim().toUpperCase();
    }

    private static String wrapFieldForCaseInsensitiveMatch(String field, DataType type) {
        return type == DataType.STRING ? "lower case(" + field + ")" : field;
    }

    private static List<String> formatLiterals(List<String> values, DataType type) {
        if (values == null) return null;
        return values.stream()
                .map(v -> {
                    if (v == null) return "null";
                    String processed = (type == DataType.STRING) ? v.toLowerCase() : v;
                    return type.formatLiteral(processed);
                })
                .toList();
    }
}
```

---

## `com.example.jexpression.model`

### RuleCondition.java
```java
package com.example.jexpression.model;

import java.util.List;

/** Single condition within a rule. Dumb data carrier only. */
public record RuleCondition(String field, String type, String op, String source, List<String> values) {}
```

### RuleDefinition.java
```java
package com.example.jexpression.model;

import java.util.List;

public record RuleDefinition(String code, String name, String status, List<RuleCondition> conditions) {
    public boolean isEnabled() { return "Enabled".equalsIgnoreCase(status); }
    public boolean hasConditions() { return conditions != null && !conditions.isEmpty(); }
}
```

### FeelRule.java
```java
package com.example.jexpression.model;

import org.kie.dmn.feel.lang.CompiledExpression;

public record FeelRule(String code, String name, String expression, CompiledExpression compiledExpression, boolean isValid) {
    public static FeelRule valid(String code, String name, String expr, CompiledExpression compiled) {
        return new FeelRule(code, name, expr, compiled, true);
    }
    public static FeelRule invalid(String code, String name, String expr) {
        return new FeelRule(code, name, expr, null, false);
    }
    public boolean canEvaluate() { return isValid && compiledExpression != null; }
}
```

### EvaluationResult.java
```java
package com.example.jexpression.model;

public record EvaluationResult(String ruleCode, String ruleName, String expression, Status status, String errorMessage, long evaluationTimeNanos) {
    public enum Status { PASSED, FAILED, ERROR, SKIPPED }

    public static EvaluationResult passed(FeelRule r, long nanos) { return new EvaluationResult(r.code(), r.name(), r.expression(), Status.PASSED, null, nanos); }
    public static EvaluationResult failed(FeelRule r, long nanos) { return new EvaluationResult(r.code(), r.name(), r.expression(), Status.FAILED, null, nanos); }
    public static EvaluationResult error(FeelRule r, String err, long nanos) { return new EvaluationResult(r.code(), r.name(), r.expression(), Status.ERROR, err, nanos); }
    public static EvaluationResult skipped(FeelRule r, String reason) { return new EvaluationResult(r.code(), r.name(), r.expression(), Status.SKIPPED, reason, 0); }

    public boolean passed() { return status == Status.PASSED; }
    public boolean failed() { return status == Status.FAILED; }
    public boolean isError() { return status == Status.ERROR; }
    public boolean isSkipped() { return status == Status.SKIPPED; }
    public double evaluationTimeMs() { return evaluationTimeNanos / 1_000_000.0; }
}
```

---

## `com.example.jexpression.service`

### FeelRuleService.java
```java
package com.example.jexpression.service;

import com.example.jexpression.model.EvaluationResult;
import com.example.jexpression.model.FeelRule;
import org.apache.commons.lang3.Validate;
import org.kie.dmn.feel.FEEL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FeelRuleService {
    private static final Logger log = LoggerFactory.getLogger(FeelRuleService.class);
    private static final ThreadLocal<FEEL> FEEL_INSTANCE = ThreadLocal.withInitial(FEEL::newInstance);

    public List<EvaluationResult> evaluate(List<FeelRule> rules, Object dto, String contextName) {
        Validate.notNull(rules, "rules must not be null");
        Validate.notNull(dto, "dto must not be null");
        Validate.notBlank(contextName, "contextName must not be blank");

        var context = Map.of(contextName, dto);
        var results = new ArrayList<EvaluationResult>(rules.size());
        for (var rule : rules) results.add(evaluateRule(rule, context));
        logSummary(results);
        return results;
    }

    public List<String> getFailedCodes(List<FeelRule> rules, Object dto, String ctx) {
        return evaluate(rules, dto, ctx).stream().filter(EvaluationResult::failed).map(EvaluationResult::ruleCode).toList();
    }

    public List<EvaluationResult> getFailures(List<FeelRule> rules, Object dto, String ctx) {
        return evaluate(rules, dto, ctx).stream().filter(r -> !r.passed()).toList();
    }

    public EvaluationResult evaluateSingle(FeelRule rule, Object dto, String ctx) {
        return evaluateRule(rule, Map.of(ctx, dto));
    }

    private EvaluationResult evaluateRule(FeelRule rule, Map<String, Object> context) {
        if (!rule.canEvaluate()) return EvaluationResult.skipped(rule, "Rule compilation failed");

        var start = System.nanoTime();
        try {
            var result = FEEL_INSTANCE.get().evaluate(rule.compiledExpression(), context);
            var elapsed = System.nanoTime() - start;
            return Boolean.TRUE.equals(result) ? EvaluationResult.passed(rule, elapsed) : EvaluationResult.failed(rule, elapsed);
        } catch (Exception e) {
            return EvaluationResult.error(rule, e.getMessage(), System.nanoTime() - start);
        }
    }

    private void logSummary(List<EvaluationResult> results) {
        log.info("Evaluation: {} passed, {} failed, {} errors, {} skipped",
                results.stream().filter(EvaluationResult::passed).count(),
                results.stream().filter(EvaluationResult::failed).count(),
                results.stream().filter(EvaluationResult::isError).count(),
                results.stream().filter(EvaluationResult::isSkipped).count());
    }
}
```

### RuleLoadingService.java
```java
package com.example.jexpression.service;

import com.example.jexpression.mapper.RuleConditionMapper;
import com.example.jexpression.model.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;
import java.util.*;

@Service
public class RuleLoadingService {
    private static final Logger log = LoggerFactory.getLogger(RuleLoadingService.class);
    private final ObjectMapper objectMapper;
    private final RuleConditionMapper mapper;

    public RuleLoadingService(ObjectMapper objectMapper, RuleConditionMapper mapper) {
        this.objectMapper = objectMapper;
        this.mapper = mapper;
    }

    public List<FeelRule> loadFromClasspath(String path) {
        try (var input = new ClassPathResource(path).getInputStream()) { return loadFromStream(input); }
        catch (IOException e) { throw new RuleLoadingException("Failed to load: " + path, e); }
    }

    public List<FeelRule> loadFromFile(Path path) {
        try (var input = Files.newInputStream(path)) { return loadFromStream(input); }
        catch (IOException e) { throw new RuleLoadingException("Failed to load: " + path, e); }
    }

    public List<FeelRule> loadFromJson(String json) {
        try {
            Map<String, List<RuleDefinition>> wrapper = objectMapper.readValue(json, new TypeReference<>() {});
            return mapper.convert(wrapper.get("rules"));
        } catch (IOException e) { throw new RuleLoadingException("Failed to parse JSON", e); }
    }

    public List<FeelRule> loadFromStream(InputStream input) throws IOException {
        Map<String, List<RuleDefinition>> wrapper = objectMapper.readValue(input, new TypeReference<>() {});
        return mapper.convert(wrapper.get("rules"));
    }

    public static class RuleLoadingException extends RuntimeException {
        public RuleLoadingException(String msg, Throwable cause) { super(msg, cause); }
    }
}
```
