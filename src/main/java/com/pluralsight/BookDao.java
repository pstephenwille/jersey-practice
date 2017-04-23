package com.pluralsight;


import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

public class BookDao {

    private Map<String,Book> books;
    private ListeningExecutorService service;

    BookDao() {
        books = new ConcurrentHashMap<String,Book>();
        service = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(10));
    }


    Collection<Book> getBooks() {
        return(books.values());
    }

    ListenableFuture<Collection<Book>> getBooksAsync() {
        ListenableFuture<Collection<Book>> future = service.submit(new Callable<Collection<Book>>() {
            @Override
            public Collection<Book> call() throws Exception {
                return getBooks();
            }
        });
        return future;
    }


    Book getBook(String id) throws BookNotFoundException {
        if (books.containsKey(id)) {
            return (books.get(id));
        } else {
            throw new BookNotFoundException("BOOK " + id + " is not found");
        }

    }
    ListenableFuture<Book> getBookAsync(final String id) {
        ListenableFuture<Book> future = service.submit(new Callable<Book>() {
            @Override
            public Book call() throws Exception {
                return getBook(id);
            }
        });
        return future;
    }

    Book addBook(Book book) {
        book.setId(UUID.randomUUID().toString());
        books.put(book.getId(), book);
        return(book);
    }
    ListenableFuture<Book> addBookAsync(final Book book) {
        ListenableFuture<Book> future =
                service.submit(new Callable<Book>() {
                    @Override
                    public Book call() throws Exception {
                        return addBook(book);
                    }
                });
        return future;
    }
}
