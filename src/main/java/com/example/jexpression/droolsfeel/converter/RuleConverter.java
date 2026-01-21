package com.example.jexpression.droolsfeel.converter;

import com.example.jexpression.droolsfeel.model.FeelRule;
import com.example.jexpression.droolsfeel.model.ValidationRule;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Converts raw rules to FEEL rules with combined expressions.
 */
@Component
public class RuleConverter {

    /**
     * Convert raw rules to FEEL rules.
     */
    public List<FeelRule> convert(List<ValidationRule> rawRules) {
        return rawRules.stream()
                .filter(ValidationRule::isEnabled)
                .map(this::toFeelRule)
                .toList();
    }

    private FeelRule toFeelRule(ValidationRule raw) {
        String combinedExpression = raw.validations().stream()
                .map(FeelExpressionBuilder::toFeel)
                .collect(Collectors.joining(" and "));
        
        return new FeelRule(raw.code(), raw.name(), combinedExpression);
    }
}
