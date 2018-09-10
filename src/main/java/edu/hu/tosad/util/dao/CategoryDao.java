package edu.hu.tosad.util.dao;

import edu.hu.tosad.model.Category;
import edu.hu.tosad.model.Database;
import edu.hu.tosad.model.Table;

import java.util.List;
import java.util.Map;

public interface CategoryDao extends CommonDao<Category> {
    Table[] getTables(int id);

    Database getDatabase(int id);

    void syncStructure(int id, Map<String, List<String>> structure);
}
