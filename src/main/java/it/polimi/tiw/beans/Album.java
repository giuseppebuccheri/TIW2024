package it.polimi.tiw.beans;

import java.sql.Date;

public class Album {
    private int id;
    private int author;
    private String title;
    private Date date;
    private String username;
    private String imagesOrder;

    public Album(int id, int author, String title, Date date, String imagesOrder) {
        this.id = id;
        this.author = author;
        this.title = title;
        this.date = date;
        this.imagesOrder = imagesOrder;
    }


    public int getAuthor() {
        return author;
    }

    public void setAuthor(int author) {
        this.author = author;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
