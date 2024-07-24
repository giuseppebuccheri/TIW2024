package it.polimi.tiw.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import java.sql.Date;
import java.sql.SQLException;

import static it.polimi.tiw.utils.ConnectionHandler.getConnection;
import static it.polimi.tiw.utils.ParamsChecker.checkParams;

@WebServlet("/createAlbum")
@MultipartConfig
public class CreateAlbum extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection = null;

    @Override
    public void init() throws ServletException {
        connection = getConnection(getServletContext());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        User user = (User) session.getAttribute("user");
        String title = request.getParameter("title");
        Date date = new java.sql.Date(new java.util.Date().getTime());
        String[] selectedImages = request.getParameterValues("images");

        if (!checkParams(title) || selectedImages == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("please provide a title and at least one image");
            return;
        }

        UserDao userDao = new UserDao(connection);
        AlbumDao dao = new AlbumDao(connection);

        int album_id;

        try {
            album_id = dao.createAlbum(user.getId(),title,date);
            for (String imageId : selectedImages) {
                try {
                    dao.addImageToAlbum(Integer.parseInt(imageId), album_id);
                } catch (Exception e) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().write("a database error occurred.");
                    return;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in creating album");
            return;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        Gson gson = new GsonBuilder().setDateFormat("yyyy MMM dd").create();
        String json_album = null;
        try {
            json_album = gson.toJson(dao.getAlbumById(album_id));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json_album);
        response.setStatus(HttpServletResponse.SC_OK);
        System.out.println(json_album);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req,resp);
    }

    @Override
    public void destroy() {
        super.destroy();
    }
}
