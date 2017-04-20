package com.pluralsight;

import com.fasterxml.jackson.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.HashMap;

@JsonPropertyOrder({"id"})
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Book {
    @NotNull(message = "author is required")
    private String author;
    private Date published;

    @NotNull(message = "title is required")
    private String title;
    //    private String isbn;@JsonIgnoreProperties(ignoreUnknown = true)

    private String id;

    private HashMap<String, Object> extras = new HashMap<String, Object>();
/*

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
*/

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getPublished() {
        return published;
    }

    public void setPublished(Date published) {
        this.published = published;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }


    @JsonAnyGetter
    public HashMap<String, Object> getExtras() {
        return extras;
    }

    @JsonAnySetter
    public void set(String key, Object value) {
        this.extras.put(key, value);
    }

}
