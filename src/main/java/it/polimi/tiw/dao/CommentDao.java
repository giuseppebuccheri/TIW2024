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

    public int addComment(int imageId, int userId, String text) throws SQLException {
        String sql = "INSERT INTO comments (text, image,user) VALUES (?, ?, ?)";

        int id;

        try (PreparedStatement pstatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);) {
            pstatement.setString(1, text);
            pstatement.setInt(2, imageId);
            pstatement.setInt(3, userId);
            int affectedRows = pstatement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating album failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    id = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating album failed, no ID obtained.");
                }
            }
        }

        return id;
    }

    public Comment getCommentById(int id) throws SQLException {
        String sql = "SELECT * FROM comments WHERE id_comment = ?";

        Comment comment = null;

        try (PreparedStatement pstatement = connection.prepareStatement(sql);) {
            pstatement.setInt(1, id);
            try (ResultSet result = pstatement.executeQuery();) {
                while (result.next()) {
                    comment = new Comment(result.getInt("id_comment"),
                            result.getString("text"),
                            result.getInt("image"),
                            result.getInt("user")
                    );
                }
            }
        }

        return comment;
    }

    public void deleteImagesComments(int imageId) throws SQLException {
        String sql = "DELETE FROM comments WHERE image = ?";
        try (PreparedStatement pstatement = connection.prepareStatement(sql);) {
            pstatement.setInt(1, imageId);
            pstatement.executeUpdate();
        }
    }
}
