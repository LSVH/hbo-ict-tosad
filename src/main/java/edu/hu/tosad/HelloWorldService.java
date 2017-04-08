package edu.hu.tosad;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;


/**
 * Simple Hello World class, Jersey style
 *
 * @author Luuk S. van Houdt <info@lsvh.org>
 * @version 0.1
 * @since 8-4-17 @ 16:05
 */

@Path("/hello")
public class HelloWorldService {
    @GET
    @Path("/{param}")
    public Response getMessage(@PathParam("param") String message) {
        String output = "Jersey says " + message;
        return Response.status(200).entity(output).build();
    }

}