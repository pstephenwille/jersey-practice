package com.pluralsight;

import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.Date;

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


    protected Response addBook(String author, String title, Date published, String isbn) {
        Book book = new Book();
        book.setAuthor(author);
        book.setTitle(title);
        book.setPublished(published);
        book.setIsbn(isbn);
        Entity<Book> bookEntity = Entity.entity(book, MediaType.APPLICATION_JSON_TYPE);
        return(target("books").request().post(bookEntity));
    }


    @Before
    public void setupBooks() {
        book1_id = addBook("author1", "title1", new Date(), "1234").readEntity(Book.class).getId();
        book2_id = addBook("author2", "title2", new Date(), "2345").readEntity(Book.class).getId();
    }


    @Test
    public void testAddBook() {
        Response response = addBook("author", "title", new Date(), "12345");

        assertEquals(200, response.getStatus());

        Book responseBook = response.readEntity(Book.class);
        assertNotNull(responseBook.getId());
        assertEquals("title", responseBook.getTitle());
    }


    @Test
    public void testGetBook() {
        Book response = target("books").path(book1_id).request().get(Book.class);
        assertNotNull(response);
    }

    @Test
    public void testGetBooks() {
        Collection<Book> response = target("books").request().get(new GenericType<Collection<Book>>(){});
        assertEquals(2, response.size());
    }


//    @Ignore
//    @Test
//    public void testDAO() {
//        Book book1 = target("books").path("1").request().get(Book.class);
//        Book book2 = target("books").path("1").request().get(Book.class);
//        Assert.assertEquals(book1.getPublished().getTime(), book2.getPublished().getTime());
//    }



}
