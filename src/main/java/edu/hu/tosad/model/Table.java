package edu.hu.tosad.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Table {
    private String name;
    private Database database;
    private Rule rule;
    private Column[] columns;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Database getDatabase() {
        return database;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    public Rule getRule() {
        return rule;
    }

    public void setRule(Rule rule) {
        this.rule = rule;
    }

    public Column[] getColumns() {
        return columns;
    }

    public void setColumns(Column[] columns) {
        this.columns = columns;
    }

    public boolean hasColumn(String name) {
        boolean result = false;
        for (Column column : columns) {
            result = result || column.getName().equals(name);
        }
        return result;
    }
}
