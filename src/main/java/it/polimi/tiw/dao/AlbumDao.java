package it.polimi.tiw.dao;

import it.polimi.tiw.beans.Album;
import it.polimi.tiw.beans.Image;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AlbumDao {
    private Connection connection;

    public AlbumDao(Connection connection) {
        this.connection = connection;
    }

    public List<Album> findAllAlbums() throws SQLException {
        List<Album> albums = new ArrayList<Album>();
        String sql = "SELECT * FROM albums";
        try (PreparedStatement pstatement = connection.prepareStatement(sql);) {
            try (ResultSet result = pstatement.executeQuery();) {
                while (result.next()) {
                    Album a = new Album(result.getInt("id_album"),
                            result.getInt("id_author"),
                            result.getString("title"),
                            result.getDate("date"),
                            result.getString("imagesOrder"));
                    albums.add(a);
                }
            }
        }
        return albums;
    }

    public List<Image> findAlbumImages(int id) throws SQLException{
        List<Image> images = new ArrayList<Image>();
        String sql = "SELECT * " +
                "FROM images " +
                "JOIN album_image ON images.id_image = album_image.image_id " +
                "WHERE album_id = ? " +
                "ORDER BY images.date DESC ";
        try (PreparedStatement pstatement = connection.prepareStatement(sql)) {
            pstatement.setInt(1, id);
            try (ResultSet result = pstatement.executeQuery();) {
                while (result.next()) {
                    Image i = new Image(result.getInt("id_image"),
                            result.getString("title"),
                            result.getDate("date"),
                            result.getString("description"),
                            result.getString("path"),
                            result.getInt("author")
                    );
                    images.add(i);
                }
            }
        }
        return images;
    }

    public Album getAlbumById(int id) throws SQLException {
        String sql = "SELECT * FROM albums WHERE id_album = ?";
        Album album = null;
        try (PreparedStatement pstatement = connection.prepareStatement(sql);) {
            pstatement.setInt(1, id);
            try (ResultSet result = pstatement.executeQuery();) {
                if(result.next()) {
                    album = new Album(
                            result.getInt("id_album"),
                            result.getInt("id_author"),
                            result.getString("title"),
                            result.getDate("date"),
                            result.getString("imagesOrder")
                    );
                }
            }
        }
        return album;
    }

    public String getAuthorUsernameById(int id) throws SQLException{
        String sql = "SELECT users.username FROM albums JOIN users ON albums.id_author = users.id_user WHERE id_album = ?";
        String nick = null;
        try (PreparedStatement pstatement = connection.prepareStatement(sql);) {
            pstatement.setInt(1, id);
            try (ResultSet result = pstatement.executeQuery();) {
                if(result.next()) {
                    nick = result.getString("username");
                }
            }
        }
        return nick;
    }

    public int createAlbum(int id_author, String title, Date date) throws ClassNotFoundException, SQLException {
        String sql = "INSERT INTO albums (id_author, title, date) VALUES (?, ?, ?)";
        int album_id = 0;

        try (PreparedStatement pstatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);) {
            pstatement.setInt(1, id_author);
            pstatement.setString(2, title);
            pstatement.setDate(3, date);

            int affectedRows = pstatement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating album failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    album_id = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating album failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error creating album: " + e.getMessage());
        }

        return album_id;
    }


    public void addImageToAlbum(int image_id, int album_id) throws SQLException {
        String sql = "INSERT INTO album_image (album_id, image_id) VALUES (?, ?)";
        try (PreparedStatement pstatement = connection.prepareStatement(sql);) {
            pstatement.setInt(1, album_id);
            pstatement.setInt(2, image_id);
            pstatement.executeUpdate();
        }
    }

    public void addOrder(int id, String order) throws SQLException {
        String sql = "UPDATE albums SET imagesOrder = ? WHERE id_album = ?";
        try (PreparedStatement pstatement = connection.prepareStatement(sql);) {
            pstatement.setString(1, order);
            pstatement.setInt(2, id);
            pstatement.executeUpdate();
        }
    }
}
