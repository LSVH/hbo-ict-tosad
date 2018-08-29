package edu.hu.tosad.model.impl;

import edu.hu.tosad.model.Template;
import edu.hu.tosad.util.dao.CommonDao;
import edu.hu.tosad.util.dao.CommonDaoImpl;

public class TemplateDaoImpl extends CommonDaoImpl implements CommonDao<Template> {
    public Template get(int id) {
        return getItem("template/" + Integer.toString(id), Template.class);
    }

    public Template[] list() {
        return getItems("templates", Template[].class);
    }
}
