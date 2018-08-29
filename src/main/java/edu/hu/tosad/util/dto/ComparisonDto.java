package edu.hu.tosad.util.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.hu.tosad.model.Column;
import edu.hu.tosad.model.Comparison;

public class ComparisonDto {
    private int order;
    private String value;
    @JsonProperty("columnname")
    private String columnName;

    public Comparison convertToComparison() {
        Comparison comparison = new Comparison();
        comparison.setOrder(getOrder());
        comparison.setValue(getValue());
        if (!getColumnName().equals("") || getColumnName() != null || getColumnName().trim().length() != 0) {
            Column column = new Column();
            column.setName(getColumnName());
            comparison.setColumn(column);
        }
        return comparison;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }
}
