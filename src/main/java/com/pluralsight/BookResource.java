package com.pluralsight;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import org.apache.commons.codec.digest.DigestUtils;
import org.glassfish.jersey.server.ManagedAsync;

import java.util.Collection;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

@Path("/books")
public class BookResource {
//    BookDao dao = new BookDao();

    @Context
    BookDao dao;

    @Context
    Request request;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ManagedAsync
    public void getBooks(@Suspended final AsyncResponse response) {
//        return (dao.getBooks());
        ListenableFuture<Collection<Book>> bookFuture = dao.getBooksAsync();
        Futures.addCallback(bookFuture, new FutureCallback<Collection<Book>>() {
            @Override
            public void onSuccess(Collection<Book> books) {
                response.resume(books);
            }

            @Override
            public void onFailure(Throwable throwable) {
                response.resume(throwable);
            }
        });
    }


    @Path("/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ManagedAsync
    public void getBook(@PathParam("id") String id, @Suspended final AsyncResponse response) {
        ListenableFuture<Book> bookFuture = dao.getBookAsync(id);
        Futures.addCallback(bookFuture, new FutureCallback<Book>() {
            @Override
            public void onSuccess(Book book) {
//                response.resume(book);

                EntityTag entityTag = generateEntityTag(book);
                Response.ResponseBuilder rb = request.evaluatePreconditions(entityTag);
                if (rb != null) {
                    response.resume(rb.build());
                } else {
                    response.resume(Response.ok().tag(entityTag).entity(book).build());
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                response.resume(throwable);
            }
        });

    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ManagedAsync
    public void addBook(@Valid @NotNull Book book, @Suspended final AsyncResponse response) {
//        response.resume(dao.addBook(book));
        ListenableFuture<Book> bookFuture = dao.addBookAsync(book);
        Futures.addCallback(bookFuture, new FutureCallback<Book>() {
            @Override
            public void onSuccess(Book addedBook) {
                response.resume(addedBook);
            }

            @Override
            public void onFailure(Throwable throwable) {
                response.resume(throwable);
            }
        });
    }

    EntityTag generateEntityTag(Book book) {
        return (new EntityTag(DigestUtils.md5Hex(
            book.getAuthor() +
            book.getTitle() +
            book.getPublished() +
            book.getExtras())));
    }

}


