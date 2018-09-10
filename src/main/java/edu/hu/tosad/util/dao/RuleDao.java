package edu.hu.tosad.util.dao;

import edu.hu.tosad.model.Comparison;
import edu.hu.tosad.model.Rule;

public interface RuleDao extends CommonDao<Rule> {
    Comparison[] getComparisons(int id);

    int add(Rule rule);
}
