package com.example.jexpression.droolsfeel.model;

import org.kie.dmn.feel.lang.CompiledExpression;

/**
 * Immutable rule with pre-compiled FEEL expression.
 * 
 * <p>Expressions are compiled at load time for:
 * <ul>
 *   <li>Fail-fast syntax validation</li>
 *   <li>~2-5x faster repeated evaluations</li>
 *   <li>Thread-safe evaluation</li>
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
    boolean isValid
) {
    
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
