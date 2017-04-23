package com.pluralsight;

/**
 * Created by paul on 4/26/2014.
 */
public class BookNotFoundException extends Exception {

    BookNotFoundException(String m) {
        super(m);
    }
}
