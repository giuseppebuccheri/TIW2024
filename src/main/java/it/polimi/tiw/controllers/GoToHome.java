package it.polimi.tiw.controllers;

import it.polimi.tiw.beans.Album;
import it.polimi.tiw.beans.Image;
import it.polimi.tiw.beans.User;
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
import java.sql.SQLException;
import java.util.List;

import static it.polimi.tiw.utils.ConnectionHandler.getConnection;

@WebServlet("/home")
public class GoToHome extends HttpServlet {
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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");
        int id = user.getId();

        String errorMessage = null;

        if (request.getParameter("errorMessage")!=null)
            errorMessage = request.getParameter("errorMessage");

        System.out.println(errorMessage);

        String presentation;
        List<Album> userAlbums, otherAlbums;
        List<Image> userImages;

        UserDao userDao = new UserDao(connection);

        try {
            userAlbums = userDao.findUserAlbums(id);
            otherAlbums = userDao.findOthersAlbums(id);
            setUsernames(otherAlbums, userDao);
            userImages = userDao.findUserImages(id);
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Database error.");
            return;
        }

        if (userAlbums.isEmpty())
            presentation = "You have no album uploaded";
        else
            presentation = "Your albums";

        //Thymeleaf
        String path = "/WEB-INF/homepage.html";
        ServletContext servletContext = getServletContext();
        final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
        ctx.setVariable("username", user.getUsername());
        ctx.setVariable("presentation", presentation);
        ctx.setVariable("userAlbums", userAlbums);
        ctx.setVariable("otherAlbums", otherAlbums);
        ctx.setVariable("userImages", userImages);
        if (errorMessage != null)
            ctx.setVariable("errorMessage", errorMessage);
        templateEngine.process(path, ctx, response.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    private void setUsernames(List<Album> albums, UserDao dao) {
        for (Album a : albums) {
            a.setUsername(dao.getUsername(a.getAuthor()));
        }
    }
}
