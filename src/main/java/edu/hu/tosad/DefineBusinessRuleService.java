package edu.hu.tosad;

import edu.hu.tosad.model.*;
import edu.hu.tosad.model.impl.*;
import edu.hu.tosad.util.dao.*;
import edu.hu.tosad.util.db.DatabaseFactory;
import edu.hu.tosad.util.db.DatabaseInterface;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/brg/define")
public class DefineBusinessRuleService {
    @GET
    @Path("/init")
    public Response init() {
        CommonDao<Category> categoryDao = new CategoryDaoImpl();
        Category[] categories = categoryDao.list();

        CommonDao<RuleType> ruleTypeDao = new RuleTypeDaoImpl();
        RuleType[] ruleTypes = ruleTypeDao.list();

        JSONObject json = new JSONObject();
        json.put("categories", new JSONArray(categories))
                .put("rule_types", new JSONArray(ruleTypes));

        return Response.ok(json.toString(), MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/category/{category_ID}/tables")
    public Response categoryTables(@PathParam("category_ID") String id) {
        if (id == null || id.trim().length() == 0) {
            return Response.serverError().entity("Category ID cannot be blank").build();
        }

        int ID = Integer.parseInt(id);
        CategoryDao categoryDao = new CategoryDaoImpl();


        // Synchronize the database structure
        Database database = categoryDao.getDatabase(ID);
        DatabaseFactory factory = new DatabaseFactory();
        DatabaseInterface db = factory.getMysqlDatabase();
        categoryDao.syncStructure(ID, db.getStructure(database));

        Table[] tables = categoryDao.getTables(ID);

        if (tables == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("No Tables found for the category with ID: " + id).build();
        }

        return Response.ok(tables, MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/database/{database_ID}/{table_name}")
    public Response hasTable(@PathParam("database_ID") String id, @PathParam("table_name") String table) {
        if (id == null || id.trim().length() == 0) {
            return Response.serverError().entity("Database ID cannot be blank").build();
        }

        if (table == null || table.trim().length() == 0) {
            return Response.serverError().entity("Table name cannot be blank").build();
        }

        CommonDao<Database> databaseDao = new DatabaseDaoImpl();
        Database database = databaseDao.get(Integer.parseInt(id));

        JSONObject json = new JSONObject();
        json.put("hasTable", database.hasTable(table));

        return Response.ok(json.toString(), MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/table/{table_ID}/columns")
    public Response tableColumns(@PathParam("table_ID") String id) {
        if (id == null || id.trim().length() == 0) {
            return Response.serverError().entity("Table ID cannot be blank").build();
        }

        TableDao tableDao = new TableDaoImpl();
        Column[] columns = tableDao.getColumns(Integer.parseInt(id));

        if (columns == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("No Columns found for the Table ID: " + id).build();
        }

        JSONObject json = new JSONObject();
        json.put("columns", columns);

        return Response.ok(json.toString(), MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/table/{table_ID}/{column_name}")
    public Response hasColumn(@PathParam("table_ID") String id, @PathParam("column_name") String column) {
        if (id == null || id.trim().length() == 0) {
            return Response.serverError().entity("Table ID cannot be blank").build();
        }

        if (column == null || column.trim().length() == 0) {
            return Response.serverError().entity("Column name cannot be blank").build();
        }

        CommonDao<Table> tableDao = new TableDaoImpl();
        Table table = tableDao.get(Integer.parseInt(id));

        JSONObject json = new JSONObject();
        json.put("hasColumn", table.hasColumn(column));

        return Response.ok(json.toString(), MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/ruletype/{rule_type_ID}/operators")
    public Response ruleTypeOperators(@PathParam("rule_type_ID") String id) {
        if (id == null || id.trim().length() == 0) {
            return Response.serverError().entity("Rule Type ID cannot be blank").build();
        }

        RuleTypeDao ruleTypeDao = new RuleTypeDaoImpl();
        Operator[] operators = ruleTypeDao.getOperators(Integer.parseInt(id));

        if (operators == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("No Operators found for the Rule Type ID: " + id).build();
        }

        return Response.ok(operators, MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/ruleType/{rule_type_ID}/{operator_value}")
    public Response hasOperator(@PathParam("rule_type_ID") String id, @PathParam("operator_value") String operator) {
        if (id == null || id.trim().length() == 0) {
            return Response.serverError().entity("Rule Type ID cannot be blank").build();
        }

        if (operator == null || operator.trim().length() == 0) {
            return Response.serverError().entity("Operator value cannot be blank").build();
        }

        RuleTypeDao ruleTypeDao = new RuleTypeDaoImpl();
        RuleType ruleType = ruleTypeDao.get(Integer.parseInt(id));

        JSONObject json = new JSONObject();
        json.put("hasOperator", ruleType.hasOperator(operator));

        return Response.ok(json.toString(), MediaType.APPLICATION_JSON).build();
    }

    @POST
    @Path("rule")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
    public Response addRule(Rule rule) {
        RuleType ruleType = rule.getRuleType();
        if (ruleType == null) {
            return Response.serverError().entity("Rule type ID cannot be blank").build();
        }

        Category category = rule.getCategory();
        if (category == null) {
            return Response.serverError().entity("Category ID cannot be blank").build();
        }

        Operator operator = rule.getOperator();
        if (operator == null) {
            return Response.serverError().entity("Operator ID cannot be blank").build();
        }

        Table table = rule.getTable();
        if (table == null) {
            return Response.serverError().entity("Table ID cannot be blank").build();
        }

        Column column = rule.getColumn();
        if (column == null) {
            return Response.serverError().entity("Column ID cannot be blank").build();
        }

        Comparison[] comparisons = rule.getComparisons();
        if (comparisons == null) {
            return Response.serverError().entity("Please supply comparisons").build();
        }

        // Add the rule
        RuleDao ruleDao = new RuleDaoImpl();
        int id = ruleDao.add(rule);

        // Add all comparisons to the rule
        ComparisonDao comparisonDao = new ComparisonDaoImpl();
        for (Comparison comparison : comparisons) {
            comparisonDao.add(id, comparison);
        }

        return Response.ok(rule, MediaType.APPLICATION_JSON).build();
    }
}