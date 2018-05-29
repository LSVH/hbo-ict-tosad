package edu.hu.tosad;

public class Dialect {
	private String uri;
	private String protocol;
	private String driver;
	private String query;

	public Dialect(String uri, String driver, String protocol) {
		this.uri = uri;
		this.driver = driver;
		this.protocol = protocol;
	}

	public Dialect(String uri, String driver, String protocol, String query) {
		this.uri = uri;
		this.driver = driver;
		this.protocol = protocol;
		this.query = query;
	}

	public String getQuery(String database) {
		return this.query.replaceAll(":database", database);
	}

	public String getURI(String database, String host, int port) {
		String output = this.uri;
		output = output.replaceAll("\\[database]", database);
		output = output.replaceAll("\\[host]", host);
		output = output.replaceAll("\\[port]", String.valueOf(port));
		output = output.replaceAll("\\[protocol]", this.protocol);
		return output;
	}

	public void loadDriver() {
		try {
			Class.forName(this.driver).newInstance();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}