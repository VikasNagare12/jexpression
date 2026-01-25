package com.example.jexpression.droolsfeel.converter;

import com.example.jexpression.droolsfeel.model.RuleCondition;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FeelExpressionBuilderTest {

    @Test
    void testScalarEquals() {
        // RuleCondition(field, type, op, source, values)
        RuleCondition c = new RuleCondition("amount", "number", "Equals", "static", Collections.singletonList("100"));
        String result = FeelExpressionBuilder.toFeel(c);
        assertEquals("amount = 100.0", result);
    }

    @Test
    void testStringScalar() {
        RuleCondition c = new RuleCondition("status", "string", "Equals", "static",
                Collections.singletonList("Active"));
        String result = FeelExpressionBuilder.toFeel(c);
        assertEquals("lower case(status) = \"active\"", result);
    }

    @Test
    void testListIn() {
        RuleCondition c = new RuleCondition("status", "string", "In", "static", Arrays.asList("A", "B"));
        String result = FeelExpressionBuilder.toFeel(c);
        assertEquals("lower case(status) in [\"a\", \"b\"]", result);
    }

    @Test
    void testRangeBetween() {
        RuleCondition c = new RuleCondition("age", "number", "Between", "static", Arrays.asList("18", "65"));
        String result = FeelExpressionBuilder.toFeel(c);
        assertEquals("age >= 18.0 and age <= 65.0", result);
    }

    @Test
    void testIsNull() {
        RuleCondition c = new RuleCondition("data", "any", "IsNull", "static", Collections.emptyList());
        String result = FeelExpressionBuilder.toFeel(c);
        assertEquals("data = null", result);
    }
}
