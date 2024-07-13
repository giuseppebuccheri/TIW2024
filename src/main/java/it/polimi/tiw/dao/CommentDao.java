package it.polimi.tiw.dao;

import it.polimi.tiw.beans.Comment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentDao {
    private Connection connection;

    public CommentDao(Connection connection) {
        this.connection = connection;
    }

    public List<Comment> findCommentsByImage(int id) throws SQLException {
        List<Comment> comments = new ArrayList<Comment>();
        String sql = "SELECT * FROM comments WHERE image = ?";
        try (PreparedStatement pstatement = connection.prepareStatement(sql);) {
            pstatement.setInt(1, id);
            try (ResultSet result = pstatement.executeQuery();) {
                while (result.next()) {
                    Comment c = new Comment(result.getInt("id_comment"),
                            result.getString("text"),
                            result.getInt("image"),
                            result.getInt("user")
                    );
                    comments.add(c);
                }
            }
        }
        return comments;
    }

    public void addComment(int imageId, int userId, String text) throws SQLException {
        String sql = "INSERT INTO comments (text, image,user) VALUES (?, ?, ?)";

        try (PreparedStatement pstatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);) {
            pstatement.setString(1, text);
            pstatement.setInt(2, imageId);
            pstatement.setInt(3, userId);
            pstatement.executeUpdate();
        }
    }

    public void deleteImagesComments(int imageId) throws SQLException {
        String sql = "DELETE FROM comments WHERE image = ?";
        try (PreparedStatement pstatement = connection.prepareStatement(sql);) {
            pstatement.setInt(1, imageId);
            pstatement.executeUpdate();
        }
    }
}
