package com.example.jexpression.model;

import lombok.Data;
import java.util.Map;

public class EvaluateRequest {
    private Rule rule;
    private Transaction data;

    public Rule getRule() {
        return rule;
    }

    public void setRule(Rule rule) {
        this.rule = rule;
    }

    public Transaction getData() {
        return data;
    }

    public void setData(Transaction data) {
        this.data = data;
    }
}
