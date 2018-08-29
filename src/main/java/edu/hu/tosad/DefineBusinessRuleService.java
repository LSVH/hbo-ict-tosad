package edu.hu.tosad;

import edu.hu.tosad.model.*;
import edu.hu.tosad.model.impl.CategoryDaoImpl;
import edu.hu.tosad.model.impl.RuleTypeDaoImpl;
import edu.hu.tosad.model.impl.TableDaoImpl;
import edu.hu.tosad.util.dao.CategoryDao;
import edu.hu.tosad.util.dao.CommonDao;
import edu.hu.tosad.util.dao.RuleTypeDao;
import edu.hu.tosad.util.dao.TableDao;
import edu.hu.tosad.util.db.DatabaseFactory;
import edu.hu.tosad.util.db.DatabaseInterface;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

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
        json.put("categories", categories)
                .put("rule_types", ruleTypes);

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
        Map<String, List<String>> map = db.getStructure(database);
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            JSONArray columns = new JSONArray();
            for (String column : entry.getValue()) {
                columns.put(column);
            }
            categoryDao.syncTable(ID, entry.getKey(), columns);
        }

        Table[] tables = categoryDao.getTables(ID);

        if (tables == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("No Category found for the ID: " + id).build();
        }

        JSONObject json = new JSONObject();
        json.put("tables", tables);

        return Response.ok(json.toString(), MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/ruleType/{rule_type_ID}/operators")
    public Response ruleTypeOperators(@PathParam("rule_type_ID") String id) {
        if (id == null || id.trim().length() == 0) {
            return Response.serverError().entity("Rule Type ID cannot be blank").build();
        }

        RuleTypeDao ruleTypeDao = new RuleTypeDaoImpl();
        Operator[] operators = ruleTypeDao.getOperators(Integer.parseInt(id));

        if (operators == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("No Operators found for the Rule Type ID: " + id).build();
        }

        JSONObject json = new JSONObject();
        json.put("operators", operators);

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
}