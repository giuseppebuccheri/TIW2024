package it.polimi.tiw.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.tiw.beans.Album;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.AlbumDao;
import it.polimi.tiw.dao.UserDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static it.polimi.tiw.utils.ConnectionHandler.getConnection;

@WebServlet("/getAlbums")
@MultipartConfig
public class GetAllAlbums extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection = null;

    public void init() throws ServletException {
        connection = getConnection(getServletContext());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Album> albums = null;
        AlbumDao albumDao = new AlbumDao(connection);

        try {
            albums = albumDao.findAllAlbums();
            setUsernames(albums);
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Not possible to recover albums, try later");
            return;
        }

        Gson gson = new GsonBuilder().setDateFormat("yyyy MMM dd").create();
        String json_albums = gson.toJson(albums);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json_albums);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    private void setUsernames(List<Album> albums) {
        UserDao dao = new UserDao(connection);

        for(Album a: albums){
            a.setUsername(dao.getUsernameById(a.getAuthor()));
        }
    }
}
