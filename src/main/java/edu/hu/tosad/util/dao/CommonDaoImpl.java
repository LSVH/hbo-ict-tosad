package edu.hu.tosad.util.dao;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.hu.tosad.util.api.APIFactory;
import edu.hu.tosad.util.api.APIInterface;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;

public class CommonDaoImpl {
    private static final String KEY_ITEMS = "items";
    protected APIInterface api;
    protected ObjectMapper om;

    public CommonDaoImpl() {
        APIFactory factory = new APIFactory();
        api = factory.getApexAPI();
        om = new ObjectMapper();
    }

    private JSONArray getRoot(String query) {
        JSONTokener token = new JSONTokener(api.get(query).readEntity(String.class));
        JSONObject root = new JSONObject(token);
        return root.getJSONArray(KEY_ITEMS);
    }

    protected <T> T getItem(String query, Class<T> type) {
        T item = null;
        try {
            item = om.readValue(getRoot(query).get(0).toString(), type);
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return item;
    }

    protected <T> T getItems(String query, Class<T> type) {
        T items = null;
        try {
            items = om.readValue(getRoot(query).toString(), type);
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return items;
    }
}
