package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.UserDao;

import static it.polimi.tiw.utils.ConnectionHandler.getConnection;
import static it.polimi.tiw.utils.ParamsChecker.checkParams;

@WebServlet("/login")
@MultipartConfig
public class CheckLogin extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection = null;

    @Override
    public void init() throws ServletException {
        connection = getConnection(getServletContext());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (!checkParams(username) || !checkParams(username)) {
            String errorMessage = "Incorrect or missing values";
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println(errorMessage);
            return;
        }

        UserDao dao = new UserDao(connection);
        User user = null;

        try {
            user = dao.checkUser(username, password);
            if (user != null) {
                request.getSession().setAttribute("user", user);
                String Serialized_User = new Gson().toJson(user);
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().println(Serialized_User);
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                String errorMessage = "Invalid username or password. Please try again.";
                response.getWriter().write(errorMessage);
            }
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            String errorMessage = "An error occurred while processing your request. Please try again later.";
            response.getWriter().write(errorMessage);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getSession(false) != null && request.getSession(false).getAttribute("user") != null)
            response.sendRedirect("home.html");
        else
            response.sendRedirect("index.html");
    }

    public void destroy() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
        }
    }
}
