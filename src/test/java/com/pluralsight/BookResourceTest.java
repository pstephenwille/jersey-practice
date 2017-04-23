package com.pluralsight;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.grizzly.connector.GrizzlyConnectorProvider;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class BookResourceTest extends JerseyTest {

    private String book1_id;
    private String book2_id;

    protected Application configure() {
        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);
        final BookDao dao = new BookDao();
        return new BookApplication(dao);
    }

    protected void configureClient(ClientConfig clientConfig) {
        JacksonJsonProvider json = new JacksonJsonProvider();
        json.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
        clientConfig.register(json);
        clientConfig.connectorProvider(new GrizzlyConnectorProvider());
    }

    @Before
    public void setupBooks() {
        book1_id = addBook("author1", "title1", new Date(), "1234").readEntity(Book.class).getId();
        book2_id = addBook("author2", "title2", new Date(), "2345").readEntity(Book.class).getId();
    }

    protected Response addBook(String author, String title, Date published, String isbn, String... extras) {
        HashMap<String, Object> book = new HashMap<String, Object>();
        book.put("author", author);
        book.put("title", title);
        book.put("published", published);
        book.put("isbn", isbn);
        if (extras != null) {
            int count = 1;
            for (String s : extras) {
                book.put("extra" + count++, s);
            }
        }

        Entity<HashMap<String, Object>> bookEntity = Entity.entity(book, MediaType.APPLICATION_JSON_TYPE);
        return (target("books").request().post(bookEntity));
    }

    @Test
    public void testAddBook() {
        Response response = addBook("author", "title", new Date(), "12345");
        assertEquals(200, response.getStatus());

        HashMap<String, Object> responseBook = toHashMap(response);
        assertNotNull(responseBook.get("id"));
        assertEquals("title", responseBook.get("title"));
    }

    protected HashMap<String, Object> toHashMap(Response response) {
        return (response.readEntity(new GenericType<HashMap<String, Object>>() {}));
    }

    @Test
    public void testGetBook() {
//        Book response = target("books").path(book1_id).request().get(Book.class);
        HashMap<String, Object> response = toHashMap(target("books").path(book1_id).request().get());
        assertNotNull(response);
    }

    @Test
    public void testGetBooks() {
//        Collection<Book> response = target("books").request().get(new GenericType<Collection<Book>>(){});
        Collection<HashMap<String, Object>> response = target("books").request().get(
            new GenericType<Collection<HashMap<String, Object>>>() {});
        assertEquals(2, response.size());
    }

    @Test
    public void testAddExtraField() {
        Response response = addBook("author", "title", new Date(), "1111", "hello world", "woot");
        Assert.assertEquals(200, response.getStatus());

        HashMap<String, Object> book = toHashMap(response);
        Assert.assertNotNull(book.get("id"));
        Assert.assertEquals(book.get("extra1"), "hello world");
        Assert.assertEquals(book.get("extra2"), "woot");
    }


    @Test
    public void AddBookNoAuthor() {
        Response response = addBook(null, "title", new Date(), "5555");
        assertEquals(400, response.getStatus());
    }

    @Test
    public void BookNotFoundWithMessage() {
        Response response = target("books").path("1").request().get();
        assertEquals(404, response.getStatus());
    }

    @Test
    public void BookEntityTagNotModified() {
        EntityTag entityTag = target("books")
            .path(book1_id)
            .request()
            .get()
            .getEntityTag();
        assertNotNull(entityTag);

        Response response = target("books").path(book1_id)
            .request()
            .header("If-None-Match", entityTag)
            .get();
        assertEquals(304, response.getStatus());
    }


    @Test
    public void testUpdateBookAuthor() {
        HashMap<String, Object> updates = new HashMap<String, Object>();
        updates.put("author", "updatedAuthor");
        Entity<HashMap<String, Object>> updateEntity = Entity.entity(updates, MediaType.APPLICATION_JSON);
        Response updateResponse = target("books").path(book1_id).request().build("PATCH", updateEntity).invoke();

        Assert.assertEquals(200, updateResponse.getStatus());

        Response getResponse = target("books").path(book1_id).request().get();
        HashMap<String, Object> getResponseMap = toHashMap(getResponse);

        Assert.assertEquals("updatedAuthor", getResponseMap.get("author"));
    }

    @Test
    public void testPatchMethodOverride(){
        HashMap<String, Object> updates = new HashMap<String, Object>();
        updates.put("author", "updatedAuthor");
        Entity<HashMap<String, Object>> updateEntity = Entity.entity(updates, MediaType.APPLICATION_JSON);
        Response updateResponse = target("books").path(book1_id).queryParam("_method", "PATCH").
            request().post(updateEntity);

        assertEquals(200, updateResponse.getStatus());

        Response getResponse = target("books").path(book1_id).request().get();
        HashMap<String, Object> getResponseMap = toHashMap(getResponse);

        assertEquals("updatedAuthor", getResponseMap.get("author"));
    }

    @Test
    public void testContentNegotiationExtensions(){
        Response xmlResponse = target("books").path(book1_id + ".xml").request().get();
        assertEquals(MediaType.APPLICATION_XML, xmlResponse.getHeaderString("Content-Type"));
    }


    @Test
    public void testPoweredByHeader(){
        Response response = target("books").path(book1_id).request().get();
        assertEquals("PluralsightXXX", response.getHeaderString("X-Powered-By"));

        Response response1 = target("books").path(book2_id).request().get();
        assertEquals("PluralsightXXX", response1.getHeaderString("X-Powered-By"));
    }

}


