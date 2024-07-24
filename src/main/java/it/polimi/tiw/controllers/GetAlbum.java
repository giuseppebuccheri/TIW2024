package it.polimi.tiw.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.tiw.beans.Album;
import it.polimi.tiw.beans.Comment;
import it.polimi.tiw.beans.Image;
import it.polimi.tiw.dao.AlbumDao;
import it.polimi.tiw.dao.CommentDao;
import it.polimi.tiw.dao.UserDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static it.polimi.tiw.utils.ConnectionHandler.getConnection;

@WebServlet("/album")
@MultipartConfig
public class GetAlbum extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection = null;

    @Override
    public void init() throws ServletException {
        connection = getConnection(getServletContext());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id = 0;

        if(request.getParameter("id") != null){
            try {
                id = Integer.parseInt(request.getParameter("id"));
            } catch (NumberFormatException | NullPointerException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Not possible to recover album, try later");
                return;
            }
        }
        else{
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("no album id found");
            return;
        }

        List<Image> albumImages;
        AlbumDao albumDao = new AlbumDao(connection);
        UserDao userDao = new UserDao(connection);
        Album album;

        try {
            album = albumDao.getAlbumById(id);
            if (album == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("Album not found");
                return;
            }
            albumImages = albumDao.findAlbumImages(id);  //ritorna tutte le immagini
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
            response.getWriter().write("Failure in album's project database extraction");
            return;
        }

        //Ritorno subito anche i commenti

        CommentDao commentDao = new CommentDao(connection);
        List<Comment> comments;

        for (Image i: albumImages){
            try {
                i.setAuthorUsername(userDao.getUsernameById(i.getIdAuthor()));
                comments = commentDao.findCommentsByImage(i.getId());
                for (Comment c:comments){
                    c.setUsername(userDao.getUsernameById(c.getUser()));
                }
                i.setCommentList(comments);     //Aggiungi alle immagini i propri commenti
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        Gson gson = new GsonBuilder().setDateFormat("yyyy MMM dd").create();
        String json_images = gson.toJson(albumImages);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json_images);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }

    @Override
    public void destroy() {
        super.destroy();
    }
}
