package edu.hu.tosad;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

@Provider
public class CorsFilter implements ContainerResponseFilter {
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
		responseContext.getHeaders().add("Access-Control-Allow-Origin", "*");
	}
}
