package it.polimi.tiw.dao;

import it.polimi.tiw.beans.Image;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ImageDao {
    private Connection connection;

    public ImageDao(Connection connection) {
        this.connection = connection;
    }


    public Image getImageById(int id) throws SQLException {
        String sql = "SELECT * FROM images WHERE id_image = ?";
        Image image = null;
        try (PreparedStatement pstatement = connection.prepareStatement(sql);) {
            pstatement.setInt(1, id);
            try (ResultSet result = pstatement.executeQuery();) {
                if(result.next()) {
                    image = new Image(
                            result.getInt("id_image"),
                            result.getString("title"),
                            result.getDate("date"),
                            result.getString("description"),
                            result.getString("path"),
                            result.getInt("author")
                    );
                }
            }
        }
        return image;
    }

    public String getAuthorUsernameById(int id) throws SQLException{
        String sql = "SELECT users.username FROM images JOIN users ON images.author = users.id_user WHERE id_image = ?";
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

    public void deleteImage(int imageId) throws SQLException {
        String deleteImageSql = "DELETE FROM images WHERE id_image = ?";
        try (PreparedStatement pstatement = connection.prepareStatement(deleteImageSql);) {
            pstatement.setInt(1, imageId);
            pstatement.executeUpdate();
        }
    }
}
