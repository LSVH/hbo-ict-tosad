package edu.hu.tosad.util.api;

public class APIFactory {
    public APIInterface getApexAPI() {
        return ApexAPI.getInstance();
    }
}