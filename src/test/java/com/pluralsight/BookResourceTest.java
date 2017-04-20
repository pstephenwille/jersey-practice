package com.pluralsight;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

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


    protected HashMap<String,Object> toHashMap(Response response) {
        return(response.readEntity(new GenericType<HashMap<String,Object>>() {}));
    }


    protected void conigureClient(ClientConfig clientConfig) {
        JacksonJsonProvider json = new JacksonJsonProvider();
        json.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
        clientConfig.register(json);
    }


    protected Response addBook(String author, String title, Date published, String isbn, String... extras) {
        HashMap<String,Object> book = new HashMap<String,Object>();
        book.put("author",author);
        book.put("title",title);
        book.put("published",published);
        book.put("isbn",isbn);
        if (extras != null) {
            int count = 1;
            for (String s : extras) {
                book.put("extra"+ count++, s);
            }
        }

        Entity<HashMap<String,Object>> bookEntity = Entity.entity(book, MediaType.APPLICATION_JSON_TYPE);
        return(target("books").request().post(bookEntity));
    }


//    protected Response addBook(String author, String title, Date published, String isbn) {
//        Book book = new Book();
//        book.setAuthor(author);
//        book.setTitle(title);
//        book.setPublished(published);
//        book.setIsbn(isbn);
//        Entity<Book> bookEntity = Entity.entity(book, MediaType.APPLICATION_JSON_TYPE);
//        return(target("books").request().post(bookEntity));
//    }


    @Before
    public void setupBooks() {
        book1_id = addBook("author1", "title1", new Date(), "1234").readEntity(Book.class).getId();
        book2_id = addBook("author2", "title2", new Date(), "2345").readEntity(Book.class).getId();
    }


    @Test
    public void testAddBook() {
        Response response = addBook("author", "title", new Date(), "12345");
        assertEquals(200, response.getStatus());

        HashMap<String, Object> responseBook = toHashMap(response);
        assertNotNull(responseBook.get("id"));
        assertEquals("title", responseBook.get("title"));
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
        Collection< HashMap<String, Object> > response = target("books").request().get(
                new GenericType< Collection<HashMap<String, Object>> >(){} );
        assertEquals(2, response.size());
    }

    @Test
    public void testAddExtraField() {
        Response response = addBook("author", "title", new Date(), "1111", "hello world", "woot");
        Assert.assertEquals(200, response.getStatus());

        HashMap<String,Object> book = toHashMap(response);
        Assert.assertNotNull(book.get("id"));
        Assert.assertEquals(book.get("extra1"),"hello world");
        Assert.assertEquals(book.get("extra2"),"woot");
    }


    @Test
    public void AddBookNoAuthor() {
        Response response = addBook(null, "title", new Date(), "5555");
        assertEquals(400, response.getStatus());
    }
}
