package edu.hu.tosad.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Column {
    private String name;
    private Rule rule;
    private Table column;
    private Comparison comparison;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Comparison getComparison() {
        return comparison;
    }

    public void setComparison(Comparison comparison) {
        this.comparison = comparison;
    }

    public Table getColumn() {
        return column;
    }

    public void setColumn(Table column) {
        this.column = column;
    }

    public Rule getRule() {
        return rule;
    }

    public void setRule(Rule rule) {
        this.rule = rule;
    }
}
