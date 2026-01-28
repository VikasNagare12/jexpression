package com.example.jexpression.mapper;

import com.example.jexpression.model.RuleCondition;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RuleConditionMapperTest {

    @Test
    void testEqualsString() {
        var condition = new RuleCondition("country", "string", "Equals", "static", List.of("US"));
        String feel = RuleConditionMapper.toFeel(condition);
        assertEquals("lower case(country) = \"us\"", feel);
    }

    @Test
    void testGreaterNumber() {
        var condition = new RuleCondition("amount", "number", "Greater", "static", List.of("1000"));
        String feel = RuleConditionMapper.toFeel(condition);
        assertEquals("amount > 1000", feel);
    }

    @Test
    void testBetweenDate() {
        var condition = new RuleCondition("transactionDate", "date", "Between", "static", 
                List.of("2023-01-01", "2023-12-31"));
        String feel = RuleConditionMapper.toFeel(condition);
        assertEquals("transactionDate >= date(\"2023-01-01\") and transactionDate <= date(\"2023-12-31\")", feel);
    }

    @Test
    void testInList() {
        var condition = new RuleCondition("channel", "string", "In", "static", 
                List.of("WEB", "MOBILE", "API"));
        String feel = RuleConditionMapper.toFeel(condition);
        assertEquals("lower case(channel) in [\"web\", \"mobile\", \"api\"]", feel);
    }

    @Test
    void testContains() {
        var condition = new RuleCondition("beneficiaryIban", "string", "Contains", "static", List.of("DE"));
        String feel = RuleConditionMapper.toFeel(condition);
        assertEquals("contains(lower case(beneficiaryIban), \"de\")", feel);
    }

    @Test
    void testNullValuesThrowsException() {
        var condition = new RuleCondition("amount", "number", "Greater", "static", null);
        assertThrows(IllegalArgumentException.class, () -> RuleConditionMapper.toFeel(condition));
    }

    @Test
    void testEmptyValuesThrowsException() {
        var condition = new RuleCondition("amount", "number", "Greater", "static", List.of());
        assertThrows(IllegalArgumentException.class, () -> RuleConditionMapper.toFeel(condition));
    }
}
