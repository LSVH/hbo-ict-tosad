package edu.hu.tosad.util.dao;

import edu.hu.tosad.model.Column;
import edu.hu.tosad.model.Table;

public interface TableDao extends CommonDao<Table> {
    Column[] getColumns(int id);
}
