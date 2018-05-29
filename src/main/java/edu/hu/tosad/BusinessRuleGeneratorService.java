package edu.hu.tosad;

import org.glassfish.jersey.client.ClientConfig;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@Path("/brg")
public class BusinessRuleGeneratorService {
	private static final String dataAPI = "https://apex.oracle.com/pls/apex/tosad/brg/";

	@GET
	@Path("/generate/{rule}")
	public Response generate(@PathParam("rule") String id) {
		System.out.println("generating: "+id);
		Response.ResponseBuilder r = Response.status(200);

		JSONObject data = this.select("rule", id);

		Dialect dialect = new Dialect(
			data.getString("URI"),
			data.getString("DRIVER"),
			data.getString("PROTOCOL"));

		Database db = new Database(
			data.getString("NAME"),
			data.getString("USER"),
			data.getString("PASS"),
			data.getString("HOST"),
			data.getInt("PORT"),
			dialect);

		ArrayList<Comparison> comparisons = new ArrayList<Comparison>();
		JSONArray arr = this.select("comparisons", id).getJSONArray("items");
		for (int i = 0; i < arr.length(); i++) {
			comparisons.add(new Comparison(
				arr.getJSONObject(i).getString("VALUE"),
				arr.getJSONObject(i).getString("COLUMN")));
		}

		Rule rule = new Rule(
			data.getString("SLUG"),
			data.getString("TYPE"),
			data.getString("CATEGORY"),
			data.getString("OPERATOR"),
			data.getString("TABLE"),
			data.getString("COLUMN"),
			data.getString("TEMPLATE"),
			db,	comparisons);

		Generator g = new GenerateFromText(rule);

		try {
			db.store(g.generate());
		} catch (SQLException sqle) {
			r = Response.status(500);
		}
		return r.build();
	}

	@GET
	@Path("/sync/{database}")
	public Response sync(@PathParam("database") String database) {
		System.out.println("syncing: "+database);

		Response.ResponseBuilder r = Response.status(200);

		JSONObject data = this.select("database", database);

		Dialect dialect = new Dialect(
			data.getString("URI"),
			data.getString("DRIVER"),
			data.getString("PROTOCOL"),
			data.getString("QUERY"));

		Database db = new Database(
			data.getString("NAME"),
			data.getString("USER"),
			data.getString("PASS"),
			data.getString("HOST"),
			data.getInt("PORT"),
			dialect);

		try {
			ResultSet rs = db.retrieve();
			while (rs.next()) {
				this.createTable(database, rs.getString("TABLE"));
				this.createColumn(database, rs.getString("TABLE"), rs.getString("COLUMN"));
			}
		} catch (SQLException sqle) {
			r = Response.status(500);
		}

		return r.build();
	}

	private JSONObject select(String view, String identifier) {
		return new JSONObject(getTarget().path(view).path(identifier)
			.request().accept(MediaType.APPLICATION_JSON).get(String.class));
	}

	private void createTable(String database, String table) {
		this.getTarget().path("define").path(database).path(table).request();
	}

	private void createColumn(String database, String table, String column) {
		this.getTarget().path("define").path(database).path(table).path(column).request();
	}

	private WebTarget getTarget() {
		ClientConfig config = new ClientConfig();
		Client client = ClientBuilder.newClient(config);
		return client.target(UriBuilder.fromUri(dataAPI).build());
	}

}