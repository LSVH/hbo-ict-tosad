package edu.hu.tosad.util.dao;

import edu.hu.tosad.model.Category;
import edu.hu.tosad.model.Database;
import edu.hu.tosad.model.Table;
import org.json.JSONArray;

public interface CategoryDao extends CommonDao<Category> {
    Table[] getTables(int id);

    Database getDatabase(int id);

    void syncTable(int id, String table, JSONArray columns);
}
