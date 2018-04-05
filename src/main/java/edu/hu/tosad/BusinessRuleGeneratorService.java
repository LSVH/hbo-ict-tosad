package edu.hu.tosad;

import org.glassfish.jersey.client.ClientConfig;
import org.json.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;


/**
 * Simple Hello World class, Jersey style
 *
 * @author Luuk S. van Houdt <info@lsvh.org>
 * @version 0.1
 * @since 8-4-17 @ 16:05
 */

@Path("/generate")
public class BusinessRuleGeneratorService {
	private String dataAPI;
	private String ruleURI;
	private String rulesURI;

	public BusinessRuleGeneratorService() {
		this.dataAPI = "https://apex.oracle.com/pls/apex/tosad/brg/";
		this.ruleURI = "rule";
		this.rulesURI = "rules";
	}

    @GET
	@Path("/{dialect}/{requested}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response get(@PathParam("dialect") String dialect, @PathParam("requested") String requested) {
		JSONObject result;
		StringBuilder output = new StringBuilder();
		dialect = dialect.toUpperCase();
		if (requested.contains(",")) {
			result = this.getRules(requested);
			for (Object item : result.getJSONArray("items")) {
				if (item instanceof JSONObject) {
					output.append(this.generateSQL((JSONObject) item, dialect)).append("\n");
				}
			}
		} else {
			result = this.getRule(requested);
			output.append(this.generateSQL(result, dialect));
		}
		return Response.status(200).entity(output.toString()).build();
	}

	private JSONObject getRule(String id) {
		return getRequest(this.ruleURI, id);
	}

	private JSONObject getRules(String ids) {
		return getRequest(this.rulesURI, ids);
	}

	private JSONObject getRequest(String action, String param) {
		ClientConfig config = new ClientConfig();
		Client client = ClientBuilder.newClient(config);
		WebTarget target = client.target(generateURI());
		return new JSONObject(target.path(action).path(param).request().accept(MediaType.APPLICATION_JSON).get(String.class));
	}

	private URI generateURI() {
		return UriBuilder.fromUri(this.dataAPI).build();
	}

	private String generateSQL(JSONObject data, String dialect) {
		String output = null;
		RuleInterface r = null;
		String type = data.getString("type");
		System.out.println(type + ": " + ("ACMP".equals(type)));
		if ("ARNG".equals(type)) {
			r = new AttributeRangeRule(
				data.getInt("id"),
				data.getString("name"),
				data.getString("entity"),
				data.getString("attribute"),
				data.getInt("range_negate") > 0,
				data.getString("r_attribute_comparer1"),
				data.getString("r_attribute_comparer2")
			);
		} else if ("ACMP".equals(type)) {
			r = new AttributeCompareRule(
				data.getInt("id"),
				data.getString("name"),
				data.getString("entity"),
				data.getString("attribute"),
				data.getString("compare_operator"),
				data.getString("c_attribute_comparer")
			);
		} else if ("TCMP".equals(type)) {
			r = new AttributeTupleRule(
				data.getInt("id"),
				data.getString("name"),
				data.getString("entity"),
				data.getString("attribute"),
				data.getString("compare_operator"),
				data.getString("c_tuple_attribute")
			);
		} else if ("TCMP".equals(type)) {
			r = new AttributeInterRule(
				data.getInt("id"),
				data.getString("name"),
				data.getString("entity"),
				data.getString("attribute"),
				data.getString("compare_operator"),
				data.getString("c_inter_entity"),
				data.getString("c_inter_attribute"),
				data.getString("c_inter_timing"),
				data.getString("c_inter_action"),
				data.getInt("c_inter_foreach") > 0,
				data.getString("c_inter_fkeys"),
				data.getString("c_inter_rkeys")
			);
		}
		try {
			if ("ORACLESQL".equals(dialect)) {
				output = r.toOracleSQL();
			} else {
				output = r.toMySQL();
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		return output;
	}

}