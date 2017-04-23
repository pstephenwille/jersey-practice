package com.pluralsight;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Created by paul on 4/26/2014.
 */
@Provider
public class BookNotFoundMapper implements ExceptionMapper<BookNotFoundException> {

    public Response toResponse(BookNotFoundException ex) {
        return Response.status(404).entity(ex.getMessage()).type("text/plain").build();
    }
}
