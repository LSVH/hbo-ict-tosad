package edu.hu.tosad.model.impl;

import edu.hu.tosad.model.Column;
import edu.hu.tosad.model.Table;
import edu.hu.tosad.util.dao.CommonDaoImpl;
import edu.hu.tosad.util.dao.TableDao;

public class TableDaoImpl extends CommonDaoImpl implements TableDao {
    public Table get(int id) {
        return getItem("table/" + Integer.toString(id), Table.class);
    }

    public Table[] list() {
        return getItems("tables", Table[].class);
    }

    public Column[] getColumns(int id) {
        return getItems("table/" + Integer.toString(id) + "/columns", Column[].class);
    }
}
