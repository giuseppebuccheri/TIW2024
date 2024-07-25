package it.polimi.tiw.controllers;

import com.google.gson.Gson;
import it.polimi.tiw.beans.Image;
import it.polimi.tiw.beans.User;
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

@WebServlet("/getUserImages")
@MultipartConfig
public class GetUserImages extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection = null;
    @Override
    public void init() throws ServletException {
        connection = getConnection(getServletContext());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");

        if (user == null){
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Not logged");
            return;
        }

        int id = user.getId();

        List<Image> userImages;

        UserDao userDao = new UserDao(connection);

        try {
            userImages = userDao.findUserImages(id);
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Not possible to recover images, try later");
            return;
        }
        String json = new Gson().toJson(userImages);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
