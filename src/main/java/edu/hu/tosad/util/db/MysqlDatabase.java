package edu.hu.tosad.util.db;

import edu.hu.tosad.model.Database;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MysqlDatabase implements DatabaseInterface {
    private static final String TABLE = "TABLE_NAME";
    private static final String COLUMN = "COLUMN_NAME";
    private Connection conn = null;

    public boolean addTrigger(String query, Database db) {
        boolean succeeded = false;
        try {
            conn = DriverManager.getConnection(getDatabaseURI(db.getName(), db.getHost(), db.getPort()), db.getUser(), db.getPass());
            conn.createStatement().execute(query);
            succeeded = true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return succeeded;
    }

    public Map<String, List<String>> getStructure(Database db) {
        String query = "SELECT " + TABLE + ", " + COLUMN + " FROM `COLUMNS` WHERE TABLE_SCHEMA=\"" + db.getName() + "\"";
        db.setName("information_schema");

        Map<String, List<String>> result = new HashMap<String, List<String>>();
        List<Map<String, String>> rows = select(query, db);
        for (Map<String, String> row : rows) {
            if (result.containsKey(row.get(TABLE))) {
                result.get(row.get(TABLE)).add(row.get(COLUMN));
            } else {
                List<String> list = new LinkedList<String>();
                list.add(row.get(COLUMN));
                result.put(row.get(TABLE), list);
            }
        }
        return result;
    }

    public List<Map<String, String>> select(String query, Database db) {
        List<Map<String, String>> l = new LinkedList<Map<String, String>>();
        try {
            conn = DriverManager.getConnection(getDatabaseURI(db.getName(), db.getHost(), db.getPort()), db.getUser(), db.getPass());
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            ResultSetMetaData rsmd = rs.getMetaData();
            while (rs.next()) {
                Map<String, String> m = new HashMap<String, String>();
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    int type = rsmd.getColumnType(i);
                    String key = rsmd.getColumnName(i);

                    String value;
                    if (type == Types.INTEGER || type == Types.BIGINT || type == Types.SMALLINT || type == Types.TINYINT) {
                        value = Integer.toString(rs.getInt(i));
                    } else if (type == Types.DOUBLE || type == Types.DECIMAL || type == Types.NUMERIC) {
                        value = Double.toString(rs.getDouble(i));
                    } else if (type == Types.FLOAT) {
                        value = Float.toString(rs.getFloat(i));
                    } else if (type == Types.DATE) {
                        DateFormat df = new SimpleDateFormat("EEEEE MMMMM yyyy HH:mm:ss.SSSZ");
                        value = df.format(rs.getDate(i));
                    } else if (type == Types.BOOLEAN) {
                        value = Boolean.toString(rs.getBoolean(i));
                    } else {
                        value = rs.getString(i);
                    }

                    m.put(key, value);
                }
                l.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return l;
    }

    private String getDatabaseURI(String database, String host, int port) {
        return "jdbc:mysql://" + host + ":" + port + "/" + database +
                "?useLegacyDatetimeCode=false" +
                "&serverTimezone=Europe/Amsterdam" +
                "&allowMultiQueries=true" +
                "&useSSL=false";
    }
}
