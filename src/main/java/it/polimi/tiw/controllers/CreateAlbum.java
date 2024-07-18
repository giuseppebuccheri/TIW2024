package it.polimi.tiw.controllers;

import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.AlbumDao;
import it.polimi.tiw.dao.UserDao;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
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
import java.sql.Date;
import java.sql.SQLException;
import java.util.Arrays;

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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request,response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        User user = (User) session.getAttribute("user");
        String title = request.getParameter("title");
        Date date = new java.sql.Date(new java.util.Date().getTime());
        String[] selectedImages = request.getParameterValues("images");

        if (!checkParams(title) || selectedImages == null) {
            response.sendRedirect("home");
            return;
        }

        UserDao userDao = new UserDao(connection);
        AlbumDao albumDaodao = new AlbumDao(connection);
        try {
            int album_id = albumDaodao.createAlbum(user.getId(),title,date);
            if (selectedImages != null) {
                for (String imageId : selectedImages) {
                    try {
                        albumDaodao.addImageToAlbum(Integer.parseInt(imageId), album_id);
                    } catch (Exception e) {
                        response.sendRedirect("home");
                        return;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in creating album");
            return;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        String path = getServletContext().getContextPath() + "/home";
        response.sendRedirect(path);
    }

    @Override
    public void destroy() {
        super.destroy();
    }
}
