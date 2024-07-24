package it.polimi.tiw.dao;

import it.polimi.tiw.beans.Album;
import it.polimi.tiw.beans.Image;
import it.polimi.tiw.beans.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserDao {
    private Connection connection;

    public UserDao(Connection connection) {
        this.connection = connection;
    }

    public User checkUser(String username, String password) throws SQLException {
        String sql = "SELECT id_user,email,username,password FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setString(2, password);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.isBeforeFirst()) {
                    return null;
                } else {
                    resultSet.next();
                    User user = new User(resultSet.getInt("id_user"),
                            resultSet.getString("email"),
                            resultSet.getString("username"),
                            resultSet.getString("password"));
                    return user;
                }
            }
        }

    }

    public List<Album> findUserAlbums(int id) throws SQLException {
        List<Album> albums = new ArrayList<Album>();
        String sql = "SELECT * FROM albums WHERE id_author = ? ORDER BY date DESC";
        try (PreparedStatement pstatement = connection.prepareStatement(sql);) {
            pstatement.setInt(1, id);
            try (ResultSet result = pstatement.executeQuery();) {
                while (result.next()) {
                    Album a = new Album(result.getInt("id_album"),
                            result.getInt("id_author"),
                            result.getString("title"),
                            result.getDate("date"));
                    albums.add(a);
                }
            }
        }
        return albums;
    }

    public List<Image> findUserImages(int id) throws SQLException{
        List<Image> images = new ArrayList<Image>();
        String sql = "SELECT * FROM images WHERE author = ? ORDER BY date DESC";
        try (PreparedStatement pstatement = connection.prepareStatement(sql);) {
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

    public List<Album> findOthersAlbums(int id) throws SQLException {
        List<Album> albums = new ArrayList<Album>();
        String sql = "SELECT * FROM albums WHERE id_author <> ? ORDER BY date DESC";
        try (PreparedStatement pstatement = connection.prepareStatement(sql);) {
            pstatement.setInt(1, id);
            try (ResultSet result = pstatement.executeQuery();) {
                while (result.next()) {
                    Album a = new Album(result.getInt("id_album"),
                            result.getInt("id_author"),
                            result.getString("title"),
                            result.getDate("date"));
                    albums.add(a);
                }
            }
        }
        return albums;
    }

    public User insertUser(String username, String password, String email) throws ClassNotFoundException, SQLException {

        String sql = "INSERT INTO users (username, password,email) VALUES (?, ?,?)";
        User user = null;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setString(2, password);
            statement.setString(3, email);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                user = checkUser(username,password);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return user;
    }

    public boolean isNew(String username) throws ClassNotFoundException, SQLException {
        boolean isValid = true;

        final String DB_URL = "jdbc:mysql://localhost:3306/tiw24?serverTimezone=UTC";
        final String USER = "root";
        final String PASS = "root";

        Class.forName("com.mysql.cj.jdbc.Driver");
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS)) {
            String sql = "SELECT * FROM users WHERE username = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, username);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        isValid = false;
                    }
                }
            }
        }

        return isValid;
    }

    public String getUsername(int id) {
        String sql = "SELECT username FROM users WHERE id_user = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("username");
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
