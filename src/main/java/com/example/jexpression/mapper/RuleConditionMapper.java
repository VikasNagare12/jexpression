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
 * Converts RuleDefinition → FeelRule (compiled).
 * Converts RuleCondition → FEEL String.
 */
@Component
public class RuleConditionMapper {

    private static final Logger log = LoggerFactory.getLogger(RuleConditionMapper.class);

    private final FEEL feel = FEEL.newInstance();

    // ─────────────────────────────────────────────────────────────
    // Public API: RuleDefinition → FeelRule
    // ─────────────────────────────────────────────────────────────

    /**
     * Convert list of rule definitions to compiled FEEL rules.
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
                .map(RuleConditionMapper::toFeel)
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

    // ─────────────────────────────────────────────────────────────
    // Public API: RuleCondition → FEEL String
    // ─────────────────────────────────────────────────────────────

    /**
     * Convert a single RuleCondition to its FEEL expression string.
     */
    public static String toFeel(RuleCondition condition) {
        String field = condition.field();
        DataType type = DataType.from(condition.type());
        ExpressionOperator operator = ExpressionOperator.from(condition.op());
        List<String> values = prepareValues(condition.values(), type);

        // For string comparisons, wrap field in lower case
        if (type == DataType.STRING) {
            field = "lower case(" + field + ")";
        }

        return operator.buildFeelExpression(field, values, type);
    }

    /**
     * Prepare values (lowercase for strings).
     */
    private static List<String> prepareValues(List<String> values, DataType type) {
        if (values == null || type != DataType.STRING) return values;
        return values.stream()
                .map(v -> v == null ? null : v.toLowerCase())
                .toList();
    }
}
