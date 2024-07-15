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
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.UserDao;

import static it.polimi.tiw.utils.ConnectionHandler.getConnection;
import static it.polimi.tiw.utils.ParamsChecker.checkParam;

@WebServlet("/login")
@MultipartConfig
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection = null;

    @Override
    public void init() throws ServletException {
        connection = getConnection(getServletContext());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        System.out.println(username);
        System.out.println(password);

        if (!checkParam(username) || !checkParam(username)) {
            String errorMessage = "Incorrect or missing values";
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMessage);
            return;
        }

        UserDao dao = new UserDao(connection);
        User user = null;
        int id = dao.getIdByUsername(username);

        try {
            user = dao.checkUser(username, password);
            if (user != null) {
                request.getSession().setAttribute("username", username);
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(username);
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

    public void destroy() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
        }
    }
}
