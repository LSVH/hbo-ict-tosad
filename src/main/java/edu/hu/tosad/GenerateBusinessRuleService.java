package edu.hu.tosad;

import edu.hu.tosad.model.Comparison;
import edu.hu.tosad.model.Rule;
import edu.hu.tosad.model.impl.RuleDaoImpl;
import edu.hu.tosad.util.dao.RuleDao;
import edu.hu.tosad.util.db.DatabaseFactory;
import edu.hu.tosad.util.db.DatabaseInterface;
import org.json.JSONObject;
import org.stringtemplate.v4.ST;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/brg/generate")
public class GenerateBusinessRuleService {
    @GET
    @Path("/rule/{rule_ID}")
    public Response generate(@PathParam("rule_ID") String id) {
        /*
         * 1. Error handling
         */
        if (id == null || id.trim().length() == 0) {
            return Response.serverError().entity("Rule ID cannot be blank").build();
        }

        /*
         * 2. Retrieve the rule configuration
         */
        int ID = Integer.parseInt(id);
        RuleDao ruleDao = new RuleDaoImpl();
        Comparison[] comparisons = ruleDao.getComparisons(ID);
        Rule rule = ruleDao.get(ID);
        rule.setComparisons(comparisons);

        /*
         * 4. Generate the SQL
         */
        ST tpl = new ST(rule.getRuleType().getTemplate().getCode());
        tpl.add("rule", rule.getSlug())
                .add("type", rule.getRuleType().getSlug())
                .add("category", rule.getCategory().getSlug())
                .add("table", rule.getTable().getName())
                .add("column", rule.getColumn().getName())
                .add("operator", rule.getOperator().getValue())
                .add("comparisons", rule.ComparisonsToString());
        String generatedCode = tpl.render();

        /*
         * 5. Install the business rule on the target database
         */
        DatabaseFactory factory = new DatabaseFactory();
        DatabaseInterface db = factory.getMysqlDatabase();
        boolean succeeded = db.addTrigger(generatedCode, rule.getCategory().getDatabase());

        /*
         * 6. Response with the result
         */
        JSONObject json = new JSONObject();
        json.put("succeeded", succeeded)
                .put("ruleID", id)
                .put("executedQuery", generatedCode);
        return succeeded
                ? Response.ok(json.toString(), MediaType.APPLICATION_JSON).build()
                : Response.serverError().entity("Failed to add the business rule").build();
    }

}