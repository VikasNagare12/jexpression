package com.example.jexpression.mapper;

import com.example.jexpression.model.RuleCondition;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RuleConditionMapperTest {

    @Test
    void testScalarEquals() {
        RuleCondition c = new RuleCondition("amount", "number", "Equals", "static", Collections.singletonList("100"));
        String result = RuleConditionMapper.toFeel(c);
        // AST renders doubles as doubles
        assertEquals("amount = 100.0", result);
    }

    @Test
    void testStringScalar() {
        RuleCondition c = new RuleCondition("status", "string", "Equals", "static",
                Collections.singletonList("Active"));
        String result = RuleConditionMapper.toFeel(c);
        assertEquals("lower case(status) = \"active\"", result);
    }

    @Test
    void testListIn() {
        RuleCondition c = new RuleCondition("status", "string", "In", "static", Arrays.asList("A", "B"));
        String result = RuleConditionMapper.toFeel(c);
        assertEquals("lower case(status) in [\"a\", \"b\"]", result);
    }

    @Test
    void testRangeBetween() {
        RuleCondition c = new RuleCondition("age", "number", "Between", "static", Arrays.asList("18", "65"));
        String result = RuleConditionMapper.toFeel(c);
        // AST renders: age >= 18.0 and age <= 65.0
        assertEquals("age >= 18.0 and age <= 65.0", result);
    }

    @Test
    void testIsNull() {
        RuleCondition c = new RuleCondition("data", "any", "IsNull", "static", Collections.emptyList());
        String result = RuleConditionMapper.toFeel(c);
        assertEquals("data = null", result);
    }
}
