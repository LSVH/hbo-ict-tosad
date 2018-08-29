package edu.hu.tosad.model.impl;

import edu.hu.tosad.model.Database;
import edu.hu.tosad.model.Dialect;
import edu.hu.tosad.util.dao.CommonDao;
import edu.hu.tosad.util.dao.CommonDaoImpl;

public class DatabaseDaoImpl extends CommonDaoImpl implements CommonDao<Database> {
    public Database get(int id) {
        Database database = getItem("database/" + Integer.toString(id), Database.class);
        Dialect dialect = getItem("database/" + Integer.toString(id) + "/dialect", Dialect.class);
        database.setDialect(dialect);
        return database;
    }

    public Database[] list() {
        return getItems("databases", Database[].class);
    }
}
