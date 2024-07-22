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

    public int getAuthorById(int id) throws SQLException{
        String sql = "SELECT author FROM images WHERE id_image = ?";
        int id_author = 0;
        try (PreparedStatement pstatement = connection.prepareStatement(sql);) {
            pstatement.setInt(1, id);
            try (ResultSet result = pstatement.executeQuery();) {
                if(result.next()) {
                    id_author = result.getInt("author");
                }
            }
        }
        return id_author;
    }

    public void deleteImage(int imageId) throws SQLException {
        String deleteImageSql = "DELETE FROM images WHERE id_image = ?";
        try (PreparedStatement pstatement = connection.prepareStatement(deleteImageSql);) {
            pstatement.setInt(1, imageId);
            pstatement.executeUpdate();
        }
    }

}
