package edu.hu.tosad.util.api;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class ApexAPI implements APIInterface {
    private static final APIInterface INSTANCE = new ApexAPI();
    private static final String REST_URI = "https://apex.oracle.com/pls/apex/tosad/brg";

    private Client client = ClientBuilder.newClient();

    private ApexAPI() {
    }

    static APIInterface getInstance() {
        return INSTANCE;
    }

    public Response get(String query) {
        return client
                .target(REST_URI)
                .path(query)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();
    }

    public String post(String query, String content) {
        return client
                .target(REST_URI)
                .path(query)
                .request()
                .post(Entity.json(content))
                .readEntity(String.class);
    }
}
