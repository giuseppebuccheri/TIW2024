package it.polimi.tiw.controllers;

import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.CommentDao;
import it.polimi.tiw.dao.UserDao;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import static it.polimi.tiw.utils.ConnectionHandler.getConnection;
import static it.polimi.tiw.utils.ParamsChecker.checkParams;

@WebServlet("/addComment")
public class AddComment extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private TemplateEngine templateEngine = null;
    private Connection connection = null;

    public void init() throws ServletException {
        ServletContext context = getServletContext();
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(context);
        templateResolver.setTemplateMode(TemplateMode.HTML);
        this.templateEngine = new TemplateEngine();
        this.templateEngine.setTemplateResolver(templateResolver);
        templateResolver.setSuffix(".html");

        connection = getConnection(context);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request,response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();

        String commentText = request.getParameter("commentText");
        User user = (User) session.getAttribute("user");
        int imageId;

        UserDao userDao = new UserDao(connection);

        if (!checkParams(commentText) || !checkParams(request.getParameter("imageId"))) {
            response.sendRedirect("home");
            return;
        }

        try {
            imageId = Integer.parseInt(request.getParameter("imageId"));
        } catch (NumberFormatException e) {
            response.sendRedirect("home");
            return;
        }


        CommentDao commentDao = new CommentDao(connection);
        try {
            commentDao.addComment(imageId, user.getId(), commentText);
            response.sendRedirect("image?id=" + imageId);
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to add comment");
        }
    }

    public void destroy() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}