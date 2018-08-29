package edu.hu.tosad.model.impl;

import edu.hu.tosad.model.Operator;
import edu.hu.tosad.model.RuleType;
import edu.hu.tosad.model.Template;
import edu.hu.tosad.util.dao.CommonDaoImpl;
import edu.hu.tosad.util.dao.RuleTypeDao;

public class RuleTypeDaoImpl extends CommonDaoImpl implements RuleTypeDao {
    public RuleType get(int id) {
        RuleType ruleType = getItem("rule-type/" + Integer.toString(id), RuleType.class);
        Template template = getItem("rule-type/" + Integer.toString(id) + "/template", Template.class);
        ruleType.setTemplate(template);
        return ruleType;
    }

    public RuleType[] list() {
        return getItems("rule-types", RuleType[].class);
    }

    public Operator[] getOperators(int id) {
        return getItems("rule-type/" + Integer.toString(id) + "/operators", Operator[].class);
    }
}
