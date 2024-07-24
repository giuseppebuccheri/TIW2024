package it.polimi.tiw.controllers;

import it.polimi.tiw.beans.User;
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
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static it.polimi.tiw.utils.ConnectionHandler.getConnection;
import static it.polimi.tiw.utils.ParamsChecker.checkParams;

@WebServlet("/deleteImage")
public class DeleteImage extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private TemplateEngine templateEngine = null;
    private Connection connection = null;

    @Override
    public void init() throws ServletException {
        ServletContext context = getServletContext();
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(context);
        templateResolver.setTemplateMode(TemplateMode.HTML);
        this.templateEngine = new TemplateEngine();
        this.templateEngine.setTemplateResolver(templateResolver);
        templateResolver.setSuffix(".html");

        connection = getConnection(context);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();

        User user = (User) session.getAttribute("user");
        int imageId;

        String errorMessage;

        if (!checkParams(request.getParameter("imageId"))) {
            errorMessage = "incorret or missing image id";
            response.sendRedirect("home?errorMessage=" + URLEncoder.encode(errorMessage, "UTF-8"));
            return;
        }

        try {
            imageId = Integer.parseInt(request.getParameter("imageId"));
        } catch (NumberFormatException e) {
            errorMessage = "error processing image id";
            response.sendRedirect("home?errorMessage=" + URLEncoder.encode(errorMessage, "UTF-8"));
            return;
        }

        ImageDao imageDao = new ImageDao(connection);
        CommentDao commentDao = new CommentDao(connection);

        try {
            if (user.getId() != imageDao.getImageById(imageId).getIdAuthor()) {
                errorMessage = "you're not authorized to deleted this image";
                response.sendRedirect("home?errorMessage=" + URLEncoder.encode(errorMessage, "UTF-8"));
                return;
            }
            commentDao.deleteImagesComments(imageId);
            imageDao.deleteImage(imageId);
            response.sendRedirect("home");
        } catch (SQLException | NumberFormatException | NullPointerException e) {
            e.printStackTrace();
            errorMessage = "an error occurred while deleting this image";
            response.sendRedirect("home?errorMessage=" + URLEncoder.encode(errorMessage, "UTF-8"));
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    public void destroy() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}