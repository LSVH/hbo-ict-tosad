package edu.hu.tosad.model.impl;

import edu.hu.tosad.model.Category;
import edu.hu.tosad.model.Database;
import edu.hu.tosad.model.Table;
import edu.hu.tosad.util.dao.CategoryDao;
import edu.hu.tosad.util.dao.CommonDaoImpl;
import org.json.JSONArray;

import java.util.List;
import java.util.Map;

public class CategoryDaoImpl extends CommonDaoImpl implements CategoryDao {
    public Category get(int id) {
        return getItem("category/" + Integer.toString(id), Category.class);
    }

    public Table[] getTables(int id) {
        return getItems("category/" + Integer.toString(id) + "/tables", Table[].class);
    }

    public Database getDatabase(int id) {
        return getItem("category/" + Integer.toString(id) + "/database", Database.class);
    }

    public void syncStructure(int id, Map<String, List<String>> structure) {
        for (Map.Entry<String, List<String>> entry : structure.entrySet()) {
            JSONArray columns = new JSONArray();
            for (String column : entry.getValue()) {
                columns.put(column);
            }
            api.post("category/" + Integer.toString(id) + "/sync/" + entry.getKey(), columns.toString());
        }
    }

    public Category[] list() {
        return getItems("categories", Category[].class);
    }
}
