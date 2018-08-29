package edu.hu.tosad.util.dao;

import edu.hu.tosad.model.Operator;
import edu.hu.tosad.model.RuleType;

public interface RuleTypeDao extends CommonDao<RuleType> {
    Operator[] getOperators(int id);
}
