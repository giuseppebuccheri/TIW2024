package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.tiw.dao.UserDao;

import static it.polimi.tiw.utils.ConnectionHandler.getConnection;
import static it.polimi.tiw.utils.ParamsChecker.checkParams;

@WebServlet("/signup")
@MultipartConfig
public class Signup extends HttpServlet {
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
        String repeat = request.getParameter("repeat");
        String email = request.getParameter("email");

        if (!checkParams(username) || !checkParams(username) || !checkParams(repeat) ||!checkParams(email)) {
            String errorMessage = "Incorrect or missing values";
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(errorMessage);
            return;
        }

        UserDao dao = new UserDao(connection);

        try {
            if (dao.isNew(username)) {
                if (repeat.equals(password)){
                    if (emailValid(email)){
                        if (dao.insertUser(username,password,email)){
                            request.getSession().setAttribute("username", username);
                            response.setStatus(HttpServletResponse.SC_OK);
                            response.getWriter().write(username);
                        }
                        else {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            String errorMessage = "Invalid username or password. Please try again.";
                            response.getWriter().write(errorMessage);
                        }
                    } else {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        response.getWriter().write("Invalid email. Please try again.");
                    }
                }else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    String errorMessage = "The two passwords are different.Please try again.";
                    response.getWriter().write(errorMessage);
                }
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                String errorMessage = "This username already exists.Please try again.";
                response.getWriter().write(errorMessage);
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            String errorMessage = "An error occurred while processing your request. Please try again.";
            response.getWriter().write(errorMessage);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req,resp);
    }

    public boolean emailValid(String email) {
        Pattern p = Pattern.compile(".+@.+\\.[a-z]+", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(email);
        return m.matches();
    }
}
