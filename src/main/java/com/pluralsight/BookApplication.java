package com.pluralsight;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.fasterxml.jackson.jaxrs.xml.JacksonXMLProvider;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.server.filter.HttpMethodOverrideFilter;
import org.glassfish.jersey.server.filter.UriConnegFilter;

import java.util.HashMap;

import javax.ws.rs.core.MediaType;


public class BookApplication extends ResourceConfig {

    BookApplication(final BookDao dao) {

        JacksonJsonProvider json = new JacksonJsonProvider().
                configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false).
                configure(SerializationFeature.INDENT_OUTPUT, true);

        JacksonXMLProvider xml = new JacksonXMLProvider().
                configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false).
                configure(SerializationFeature.INDENT_OUTPUT, true);

        HashMap<String, MediaType> mappings = new HashMap<String, MediaType>();
        mappings.put("xml", MediaType.APPLICATION_XML_TYPE);
        mappings.put("json", MediaType.APPLICATION_JSON_TYPE);
        UriConnegFilter uriConnegFilter = new UriConnegFilter(mappings, null);


        packages("com.pluralsight");
        register(new AbstractBinder() {
            protected void configure() {
                bind(dao).to(BookDao.class);
            }
        });
        register(json);
        register(xml);
        property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);
        register(HttpMethodOverrideFilter.class);
        register(uriConnegFilter);
    }
}
