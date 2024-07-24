package it.polimi.tiw.controllers;

import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.AlbumDao;
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
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;

import static it.polimi.tiw.utils.ConnectionHandler.getConnection;
import static it.polimi.tiw.utils.ParamsChecker.checkParams;

@WebServlet("/createAlbum")
public class CreateAlbum extends HttpServlet {
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
        HttpSession session = request.getSession(false);

        User user = (User) session.getAttribute("user");
        String title = request.getParameter("title");
        Date date = new java.sql.Date(new java.util.Date().getTime());
        String[] selectedImages = request.getParameterValues("images");

        String errorMessage = null;

        if (!checkParams(title)) {
            errorMessage = "incorret or missing title";
            response.sendRedirect("home?errorMessage=" + URLEncoder.encode(errorMessage, "UTF-8"));
            return;
        }

        if (selectedImages == null){
            errorMessage = "please select at least one image";
            response.sendRedirect("home?errorMessage=" + URLEncoder.encode(errorMessage, "UTF-8"));
            return;
        }

        AlbumDao dao = new AlbumDao(connection);
        try {
            int album_id = dao.createAlbum(user.getId(),title,date);
            if (selectedImages != null) {
                for (String imageId : selectedImages) {
                    try {
                        dao.addImageToAlbum(Integer.parseInt(imageId), album_id);
                    } catch (Exception e) {
                        errorMessage = "error processing image id";
                        response.sendRedirect("home?errorMessage=" + URLEncoder.encode(errorMessage, "UTF-8"));
                        return;
                    }
                }
            }
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            errorMessage = "an error occurred while creating album";
            response.sendRedirect("home?errorMessage=" + URLEncoder.encode(errorMessage, "UTF-8"));
            return;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        response.sendRedirect("home");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request,response);
    }

    @Override
    public void destroy() {
        super.destroy();
    }
}
