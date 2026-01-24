package com.example.jexpression.droolsfeel.converter;

import com.example.jexpression.droolsfeel.model.FeelRule;
import com.example.jexpression.droolsfeel.model.ValidationRule;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.lang.CompiledExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Converts raw validation rules to compiled FEEL rules.
 * 
 * <p>
 * Expressions are compiled at load time for:
 * <ul>
 * <li>Fail-fast syntax validation</li>
 * <li>~2-5x faster repeated evaluations</li>
 * </ul>
 */
@Component
public class RuleConverter {

    private static final Logger log = LoggerFactory.getLogger(RuleConverter.class);

    private final FEEL feel = FEEL.newInstance();

    /**
     * Convert raw rules to compiled FEEL rules.
     * Filters disabled rules automatically.
     */
    public List<FeelRule> convert(List<ValidationRule> rawRules) {
        log.info("Converting {} raw rules to FEEL", rawRules.size());

        List<FeelRule> rules = rawRules.stream()
                .filter(ValidationRule::isEnabled)
                .filter(ValidationRule::hasValidations)
                .map(this::toFeelRule)
                .toList();

        long valid = rules.stream().filter(FeelRule::isValid).count();
        long invalid = rules.size() - valid;

        log.info("Conversion complete: {} rules ({} valid, {} invalid)",
                rules.size(), valid, invalid);

        return rules;
    }

    private FeelRule toFeelRule(ValidationRule raw) {
        // Build combined FEEL expression from validations
        String expression = raw.validations().stream()
                .map(FeelExpressionBuilder::toFeel)
                .collect(Collectors.joining(" and "));

        // Compile expression
        CompiledExpression compiled = compileExpression(raw.code(), expression);

        if (compiled != null) {
            log.debug("Rule [{}] compiled: {}", raw.code(), expression);
            return FeelRule.valid(raw.code(), raw.name(), expression, compiled);
        } else {
            log.error("Rule [{}] FAILED to compile: {}", raw.code(), expression);
            return FeelRule.invalid(raw.code(), raw.name(), expression);
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
