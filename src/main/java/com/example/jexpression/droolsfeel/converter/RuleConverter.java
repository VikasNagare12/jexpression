package com.example.jexpression.droolsfeel.converter;

import com.example.jexpression.droolsfeel.model.FeelRule;
import com.example.jexpression.droolsfeel.model.RuleDefinition;
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
                .map(FeelAstExpressionBuilder::toFeel)
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
