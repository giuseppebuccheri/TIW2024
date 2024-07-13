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

    public List<Image> findAlbumImages(int id,int offset) throws SQLException{
        List<Image> images = new ArrayList<Image>();
        String sql = "SELECT * " +
                "FROM images " +
                "JOIN album_image ON images.id_image = album_image.image_id " +
                "WHERE album_id = ? " +
                "ORDER BY images.date DESC " +
                "LIMIT 5 " +
                "OFFSET ?";     //Numero righe da saltare
        try (PreparedStatement pstatement = connection.prepareStatement(sql);) {
            pstatement.setInt(1, id);
            pstatement.setInt(2, offset);
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
                            result.getDate("date")
                    );
                }
            }
        }
        return album;
    }

    public int getAlbumIdByTitle(String title) throws SQLException {
        String sql = "SELECT id_album FROM albums WHERE title = ?";
        int id = 0;
        try (PreparedStatement pstatement = connection.prepareStatement(sql);) {
            pstatement.setString(1, title);
            try (ResultSet result = pstatement.executeQuery();) {
                if (result.next()) {
                    id = result.getInt("id_album");
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error while fetching album ID by title: " + e.getMessage());
        }
        return id;
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

    public int countAlbumImages(int albumId) throws SQLException {
        String sql = "SELECT COUNT(*) AS total FROM album_image WHERE album_id = ?";
        try (PreparedStatement pstatement = connection.prepareStatement(sql);) {
            pstatement.setInt(1, albumId);
            try (ResultSet result = pstatement.executeQuery();) {
                if (result.next()) {
                    return result.getInt("total");
                }
            }
        }
        return 0;
    }
}
