package edu.hu.tosad.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Arrays;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Rule {
    private String slug;
    private Category category;
    private RuleType ruleType;
    private Operator operator;
    private Table table;
    private Column column;
    private Comparison[] comparisons;

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public RuleType getRuleType() {
        return ruleType;
    }

    public void setRuleType(RuleType ruleType) {
        this.ruleType = ruleType;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public Column getColumn() {
        return column;
    }

    public void setColumn(Column column) {
        this.column = column;
    }

    public Comparison[] getComparisons() {
        return comparisons;
    }

    public void setComparisons(Comparison[] comparisons) {
        this.comparisons = comparisons;
    }

    public String ComparisonsToString() {
        StringBuilder sb = new StringBuilder();
        for (Comparison comparison : comparisons) {
            sb.append(comparison.toString());
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "{" +
                "slug:'" + slug + '\'' +
                ", category=" + category +
                ", ruleType=" + ruleType +
                ", operator=" + operator +
                ", table=" + table +
                ", column=" + column +
                ", comparisons=" + Arrays.toString(comparisons) +
                '}';
    }
}
