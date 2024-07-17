package it.polimi.tiw.controllers;

import it.polimi.tiw.dao.CommentDao;
import it.polimi.tiw.dao.ImageDao;
import it.polimi.tiw.dao.UserDao;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static it.polimi.tiw.utils.ConnectionHandler.getConnection;

@WebServlet("/deleteImage")
public class DeleteImageServlet extends HttpServlet {
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

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();

        int imageId = Integer.parseInt(request.getParameter("imageId"));
        String username = (String) session.getAttribute("username");

        ImageDao imageDao = new ImageDao(connection);
        CommentDao commentDao = new CommentDao(connection);
        try {
            String imageAuthor = imageDao.getAuthorUsernameById(imageId);
            if (!username.equals(imageAuthor)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "You are not authorized to delete this image");
                return;
            }
            commentDao.deleteImagesComments(imageId);
            imageDao.deleteImage(imageId);
            response.sendRedirect("home");
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to delete image");
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