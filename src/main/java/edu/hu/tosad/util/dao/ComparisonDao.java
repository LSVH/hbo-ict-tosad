package edu.hu.tosad.util.dao;

import edu.hu.tosad.model.Comparison;

public interface ComparisonDao extends CommonDao<Comparison> {
    int add(int id, Comparison comparison);
}
