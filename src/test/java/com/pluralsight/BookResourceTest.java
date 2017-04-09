package com.pluralsight;

import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.GenericType;
import java.util.Collection;

public class BookResourceTest extends JerseyTest {

    protected Application configure() {
//        enable(TestProperties.LOG_TRAFFIC);
//        enable(TestProperties.DUMP_ENTITY);
        final BookDao dao = new BookDao();

        return new BookApplication(dao);
    }


    @Test
    public void testDAO() {
        Book book1 = target("books").path("1").request().get(Book.class);
        Book book2 = target("books").path("1").request().get(Book.class);
        Assert.assertEquals(book1.getPublished().getTime(), book2.getPublished().getTime());
    }


    @Test
    public void testGetBooks() {
        Collection<Book> response = target("books").request().get(new GenericType<Collection<Book>>(){});
        Assert.assertEquals(2, response.size());
    }
}
