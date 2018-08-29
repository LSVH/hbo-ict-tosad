package edu.hu.tosad.util.api;

import javax.ws.rs.core.Response;

public interface APIInterface {
    Response get(String query);

    void post(String query, String content);
}
