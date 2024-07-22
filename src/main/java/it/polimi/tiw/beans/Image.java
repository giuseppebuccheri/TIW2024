package it.polimi.tiw.beans;

import java.util.Date;
import java.util.List;

public class Image {
    private int id;
    private String title;
    private Date date;
    private String description;
    private String path;
    private int idAuthor;
    private String authorUsername;

    //Serve per passare tutti i commenti di un immagine in una sola chiamta
    private List<Comment> commentList;

    public Image(int id, String title, Date date, String description, String path, int idAuthor) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.description = description;
        this.path = path;
        this.idAuthor = idAuthor;
    }

    public void setAuthorUsername(String authorUsername) {
        this.authorUsername = authorUsername;
    }

    public void setCommentList(List<Comment> commentList) {
        this.commentList = commentList;
    }

    public String getAuthorUsername() {
        return authorUsername;
    }

    public List<Comment> getCommentList() {
        return commentList;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getIdAuthor() {
        return idAuthor;
    }

    public void setIdAuthor(int idAuthor) {
        this.idAuthor = idAuthor;
    }
}
