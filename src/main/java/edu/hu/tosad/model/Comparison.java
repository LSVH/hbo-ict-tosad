package edu.hu.tosad.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.util.StringUtils;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Comparison {
    private int order;
    private String value;
    private Column column;
    private Rule rule;

    @JsonIgnore
    private boolean last = false;

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

    public Column getColumn() {
        return column;
    }

    public void setColumn(Column column) {
        this.column = column;
    }

    public Rule getRule() {
        return rule;
    }

    public void setRule(Rule rule) {
        this.rule = rule;
    }

    public boolean getLast() {
        return last;
    }

    public void setLast(boolean b) {
        last = b;
    }

    private boolean hasValue() {
        return StringUtils.isEmpty(value);
    }

    private boolean hasColumn() {
        return getColumn() != null;
    }

    @Override
    public String toString() {
        // Default output is generic for last comparison
        String output =
                hasValue() && hasColumn()
                        ? getValue() + " " + getColumn().getName()
                        : (!hasValue() && hasColumn()
                        ? getColumn().getName()
                        : getValue());

        if (!last) {
            if ((hasValue() && hasColumn()) || !hasValue() && hasColumn()) {
                output = output + " ";
            } else if (hasValue() && !hasColumn()) {
                output = output + "', '";
            }
        }

        return output;
    }
}
