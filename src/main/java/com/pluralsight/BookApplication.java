package com.pluralsight;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;

public class BookApplication extends ResourceConfig /* extends jax, and is easier to use. */{

    BookApplication(final BookDao dao){
//        packages("com.pluralsight");
//        register(new AbstractBinder() {
//            @Override
//            protected void configure() {
//                bind(dao).to(BookDao.class);
//            }
//        });

        JacksonJsonProvider json= new JacksonJsonProvider()
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true)
                .configure(SerializationFeature.INDENT_OUTPUT, true);


        packages("com.pluralsight");
        register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(dao).to(BookDao.class);
            }
        });
        register(json);
        property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);
    }










}
