package com.example.jexpression.droolsfeel.converter;

import com.example.jexpression.droolsfeel.model.RuleCondition;

/**
 * Converts RuleCondition to FEEL expression using type-based template lookup.
 * 
 * <p>
 * Uses {@link FeelTemplate#forOperator(String, String)} for automatic template
 * selection.
 *
 * @deprecated Use FeelAstExpressionBuilder instead.
 */
@Deprecated
public final class FeelExpressionBuilder {

    private FeelExpressionBuilder() {}

    /**
     * Convert RuleCondition to FEEL expression string.
     */
    public static String toFeel(RuleCondition c) {
        FeelTemplate template = FeelTemplate.forOperator(c.op(), c.type());
        return template.apply(c.field(), c.values());
    }
}
