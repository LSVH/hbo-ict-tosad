package edu.hu.tosad.util.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.hu.tosad.model.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RuleDto {
    private String slug;
    @JsonProperty("categoryslug")
    private String categorySlug;
    @JsonProperty("ruletypeslug")
    private String ruleTypeSlug;
    @JsonProperty("operatorvalue")
    private String operatorValue;
    @JsonProperty("tablename")
    private String tableName;
    @JsonProperty("columnname")
    private String columnName;
    @JsonProperty("templatecode")
    private String templateCode;
    @JsonProperty("dialectvalue")
    private String dialectValue;
    @JsonProperty("databasename")
    private String databaseName;
    @JsonProperty("databaseuser")
    private String databaseUser;
    @JsonProperty("databasepass")
    private String databasePass;
    @JsonProperty("databasehost")
    private String databaseHost;
    @JsonProperty("databaseport")
    private int databasePort;

    public Rule convertToRule() {
        Template template = new Template();
        template.setCode(getTemplateCode());

        RuleType ruleType = new RuleType();
        ruleType.setSlug(getRuleTypeSlug());
        ruleType.setTemplate(template);

        Dialect dialect = new Dialect();
        dialect.setValue(getDialectValue());

        Database database = new Database();
        database.setName(getDatabaseName());
        database.setUser(getDatabaseUser());
        database.setPass(getDatabasePass());
        database.setHost(getDatabaseHost());
        database.setPort(getDatabasePort());
        database.setDialect(dialect);

        Category category = new Category();
        category.setSlug(getCategorySlug());
        category.setDatabase(database);

        Operator operator = new Operator();
        operator.setValue(getOperatorValue());
        operator.setRuleType(ruleType);

        Table table = new Table();
        table.setName(getTableName());

        Column column = new Column();
        column.setName(getColumnName());

        Rule rule = new Rule();
        rule.setSlug(getSlug());
        rule.setRuleType(ruleType);
        rule.setCategory(category);
        rule.setOperator(operator);
        rule.setTable(table);
        rule.setColumn(column);
        return rule;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getCategorySlug() {
        return categorySlug;
    }

    public void setCategorySlug(String categorySlug) {
        this.categorySlug = categorySlug;
    }

    public String getRuleTypeSlug() {
        return ruleTypeSlug;
    }

    public void setRuleTypeSlug(String ruleTypeSlug) {
        this.ruleTypeSlug = ruleTypeSlug;
    }

    public String getOperatorValue() {
        return operatorValue;
    }

    public void setOperatorValue(String operatorValue) {
        this.operatorValue = operatorValue;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public String getDialectValue() {
        return dialectValue;
    }

    public void setDialectValue(String dialectValue) {
        this.dialectValue = dialectValue;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getDatabaseUser() {
        return databaseUser;
    }

    public void setDatabaseUser(String databaseUser) {
        this.databaseUser = databaseUser;
    }

    public String getDatabasePass() {
        return databasePass;
    }

    public void setDatabasePass(String databasePass) {
        this.databasePass = databasePass;
    }

    public String getDatabaseHost() {
        return databaseHost;
    }

    public void setDatabaseHost(String databaseHost) {
        this.databaseHost = databaseHost;
    }

    public int getDatabasePort() {
        return databasePort;
    }

    public void setDatabasePort(int databasePort) {
        this.databasePort = databasePort;
    }
}
