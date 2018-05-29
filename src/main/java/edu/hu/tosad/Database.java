package edu.hu.tosad;

import java.sql.*;
import java.util.Properties;

public class Database {
	private String name;
	private String user;
	private String pass;
	private String host;
	private int port;
	private Dialect dialect;
	private Connection conn;

	public Database(String name, String user, String pass, String host, int port, Dialect dialect) {
		this.name = name;
		this.user = user;
		this.pass = pass;
		this.host = host;
		this.port = port;
		this.dialect = dialect;
		this.setupConnection();
	}

	public void store(String sql) throws SQLException {
		Statement stmt = null;
		try {
			stmt = this.conn.createStatement();
			stmt.executeUpdate(sql);
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			if (stmt != null) { stmt.close(); }
		}
	}

	public ResultSet retrieve() throws SQLException {
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = this.conn.createStatement();
			rs = stmt.executeQuery(this.dialect.getQuery(this.name));
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			if (stmt != null) { stmt.close(); }
		}
		return rs;
	}

	public void setupConnection() {
		Properties credentials = new Properties();
		credentials.put("user", this.user);
		credentials.put("password", this.pass);

		this.dialect.loadDriver();

		try {
			this.conn = DriverManager.getConnection(this.dialect.getURI(this.name, this.host, this.port), credentials);
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
	}
}
