package it.polimi.tiw.beans;

public class Comment {
    private int id;
    private String text;
    private int user;
    private int image;
    private String username;

    public Comment(int id, String text, int image, int user) {
        this.id = id;
        this.text = text;
        this.image = image;
        this.user = user;
    }

    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }


}