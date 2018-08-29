package edu.hu.tosad.model.impl;

import edu.hu.tosad.model.Operator;
import edu.hu.tosad.util.dao.CommonDao;
import edu.hu.tosad.util.dao.CommonDaoImpl;

public class OperatorDaoImpl extends CommonDaoImpl implements CommonDao<Operator> {
    public Operator get(int id) {
        return getItem("operator/" + Integer.toString(id), Operator.class);
    }

    public Operator[] list() {
        return getItems("operators", Operator[].class);
    }
}
