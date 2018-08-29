package edu.hu.tosad.util.db;

public class DatabaseFactory {
    public DatabaseInterface getMysqlDatabase() {
        return new MysqlDatabase();
    }
}