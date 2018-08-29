package edu.hu.tosad.model.impl;

import edu.hu.tosad.model.Dialect;
import edu.hu.tosad.util.dao.CommonDao;
import edu.hu.tosad.util.dao.CommonDaoImpl;

public class DialectDaoImpl extends CommonDaoImpl implements CommonDao<Dialect> {
    public Dialect get(int id) {
        return getItem("dialect/" + Integer.toString(id), Dialect.class);
    }

    public Dialect[] list() {
        return getItems("dialects", Dialect[].class);
    }
}
