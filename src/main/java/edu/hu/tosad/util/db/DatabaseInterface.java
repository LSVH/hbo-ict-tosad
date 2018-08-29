package edu.hu.tosad.util.db;

import edu.hu.tosad.model.Database;

import java.util.List;
import java.util.Map;

public interface DatabaseInterface {
    boolean addTrigger(String query, Database db);

    Map<String, List<String>> getStructure(Database db);

    List<Map<String, String>> select(String query, Database db);
}