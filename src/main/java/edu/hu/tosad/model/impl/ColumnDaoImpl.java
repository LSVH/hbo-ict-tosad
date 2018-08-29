package edu.hu.tosad.model.impl;

import edu.hu.tosad.model.Column;
import edu.hu.tosad.util.dao.CommonDao;
import edu.hu.tosad.util.dao.CommonDaoImpl;

public class ColumnDaoImpl extends CommonDaoImpl implements CommonDao<Column> {
    public Column get(int id) {
        return getItem("column/" + Integer.toString(id), Column.class);
    }

    public Column[] list() {
        return getItems("columns", Column[].class);
    }
}
