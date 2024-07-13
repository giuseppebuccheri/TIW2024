package it.polimi.tiw.controllers;

import it.polimi.tiw.dao.AlbumDao;
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
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;

@WebServlet("/createAlbum")
public class CreateAlbumServlet extends HttpServlet {
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

        try {
            String driver = context.getInitParameter("dbDriver");
            String DB_URL = context.getInitParameter("dbUrl");
            String USER = context.getInitParameter("dbUser");
            String PASS = context.getInitParameter("dbPassword");
            Class.forName(driver);
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (ClassNotFoundException | SQLException e) {
            throw new UnavailableException("Couldn't connect to database");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        String username= (String) session.getAttribute("username");
        String title = request.getParameter("title");
        Date date = new java.sql.Date(new java.util.Date().getTime());
        String[] selectedImages = request.getParameterValues("images");

        UserDao userDao = new UserDao(connection);
        AlbumDao albumDaodao = new AlbumDao(connection);
        try {
            int album_id = albumDaodao.createAlbum(userDao.getIdByUsername(username),title,date);
            if (selectedImages != null) {
                for (String imageId : selectedImages) {
                    albumDaodao.addImageToAlbum(Integer.parseInt(imageId), album_id);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in creating album");
            return;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        response.sendRedirect("/home");
    }

    @Override
    public void destroy() {
        super.destroy();
    }
}
