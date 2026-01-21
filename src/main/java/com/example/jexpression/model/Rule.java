package com.example.jexpression.model;

import lombok.Data;
import java.util.Map;
import java.util.List;

public class Rule {
    private String ruleId;
    private Integer version;
    private Integer priority;
    private String status;
    private String effectiveFrom;
    private String effectiveTo;

    private Map<String, List<String>> index;
    private Map<String, Object> logic; // Represents the JsonLogic expression
    private Action action;

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEffectiveFrom() {
        return effectiveFrom;
    }

    public void setEffectiveFrom(String effectiveFrom) {
        this.effectiveFrom = effectiveFrom;
    }

    public String getEffectiveTo() {
        return effectiveTo;
    }

    public void setEffectiveTo(String effectiveTo) {
        this.effectiveTo = effectiveTo;
    }

    public Map<String, List<String>> getIndex() {
        return index;
    }

    public void setIndex(Map<String, List<String>> index) {
        this.index = index;
    }

    public Map<String, Object> getLogic() {
        return logic;
    }

    public void setLogic(Map<String, Object> logic) {
        this.logic = logic;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }
}
