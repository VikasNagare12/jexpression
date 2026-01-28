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
 * Formats and normalizes all values at the boundary before passing to
 * operators.
 */
@Component
public class RuleConditionMapper {

    private static final Logger log = LoggerFactory.getLogger(RuleConditionMapper.class);
    private static final String CONDITION_JOINER = " and ";

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
                .collect(Collectors.joining(CONDITION_JOINER));

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
        DataType type = DataType.from(condition.type());
        String normalizedOp = normalizeOperator(condition.op());
        ExpressionOperator operator = ExpressionOperator.from(normalizedOp);

        String field = wrapFieldForCaseInsensitiveMatch(condition.field(), type);
        List<String> formattedValues = formatLiterals(condition.values(), type);

        return operator.buildFeelExpression(field, formattedValues);
    }

    // ─────────────────────────────────────────────────────────────
    // Normalization Helpers
    // ─────────────────────────────────────────────────────────────

    /**
     * Normalize operator string once at the boundary.
     */
    private static String normalizeOperator(String op) {
        if (op == null)
            return null;
        return op.trim().toUpperCase();
    }

    /**
     * Wrap field in lower case() for case-insensitive string matching.
     */
    private static String wrapFieldForCaseInsensitiveMatch(String field, DataType type) {
        if (type == DataType.STRING) {
            return "lower case(" + field + ")";
        }
        return field;
    }

    /**
     * Format values: lowercase strings, then apply literal formatting.
     */
    private static List<String> formatLiterals(List<String> values, DataType type) {
        if (values == null)
            return null;

        return values.stream()
                .map(v -> {
                    if (v == null)
                        return "null";
                    String processed = (type == DataType.STRING) ? v.toLowerCase() : v;
                    return type.formatLiteral(processed);
                })
                .toList();
    }
}
