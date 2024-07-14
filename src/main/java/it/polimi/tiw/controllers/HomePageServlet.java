package it.polimi.tiw.controllers;

import it.polimi.tiw.beans.Album;
import it.polimi.tiw.beans.Comment;
import it.polimi.tiw.beans.Image;
import it.polimi.tiw.dao.AlbumDao;
import it.polimi.tiw.dao.UserDao;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
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
import java.util.List;

@WebServlet("/home")
public class HomePageServlet extends HttpServlet {
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
        handleRequest(request, response, false);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String choice = request.getParameter("toggle");
        boolean toggle = Boolean.parseBoolean(choice);
        handleRequest(request, response, !toggle);
    }

    private void handleRequest(HttpServletRequest request, HttpServletResponse response, boolean b) throws IOException {
        HttpSession session = request.getSession(false);
        String username= (String) session.getAttribute("username");
        String presentation;
        List<Album> userAlbums, otherAlbums, albums;
        List<Image> userImages;
        UserDao userDao = new UserDao(connection);
        AlbumDao albumDao = new AlbumDao(connection);
        try {
            userAlbums = userDao.findUserAlbums(userDao.getIdByUsername(username));
            otherAlbums = userDao.findOthersAlbums(userDao.getIdByUsername(username));
            setUsernames(otherAlbums,userDao);
            userImages = userDao.findUserImages(userDao.getIdByUsername(username));
            System.out.println("User ID: " + userDao.getIdByUsername(username));
            System.out.println("Number of album found: " + userAlbums.size());
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in worker's project database extraction");
            return;
        }

        if(userAlbums.isEmpty())
            presentation = "You have no album uploaded";
        else
            presentation = "Your albums";

        //Thymeleaf
        String path = "/WEB-INF/homepage.html";
        ServletContext servletContext = getServletContext();
        final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
        ctx.setVariable("username", username);
        ctx.setVariable("toggle", b);
        ctx.setVariable("presentation", presentation);
        ctx.setVariable("userAlbums",userAlbums);
        ctx.setVariable("otherAlbums",otherAlbums);
        ctx.setVariable("userImages",userImages);
        templateEngine.process(path, ctx, response.getWriter());
    }

    private void setUsernames(List<Album> albums, UserDao dao) {
        for(Album a: albums){
            a.setUsername(dao.getUsername(a.getAuthor()));
        }
    }
}
