package com.pluralsight;

public class BookNotFoundException extends Exception {

    BookNotFoundException(String m) {
        super(m);
    }
}
