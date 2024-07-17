package it.polimi.tiw.controllers;

import it.polimi.tiw.beans.Album;
import it.polimi.tiw.beans.Image;
import it.polimi.tiw.dao.AlbumDao;
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

import static it.polimi.tiw.utils.ConnectionHandler.getConnection;

@WebServlet("/album")
public class AlbumServlet extends HttpServlet {
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
        HttpSession session = request.getSession(false);
        int id = 0, offset = 0;

        if(request.getParameter("id") != null){
            try {
                id = Integer.parseInt(request.getParameter("id"));
            } catch (NumberFormatException | NullPointerException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid album id");
                return;
            }
            session.setAttribute("chosenAlbum", id);
        }
        else{
            if(session.getAttribute("chosenAlbum") != null)
                id = (Integer) session.getAttribute("chosenAlbum");
            else
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid album id");
        }

        try {
            if (request.getParameter("offset") != null) {
                offset = Integer.parseInt(request.getParameter("offset"));
                if (offset < 0)
                    offset=0;
            }
        } catch (NumberFormatException | NullPointerException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid offset");
            return;
        }

        List<Image> albumImages;
        AlbumDao dao = new AlbumDao(connection);
        Album album;
        String albumcreator;

        try {
            album = dao.getAlbumById(id);
            if (album == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Album not found");
                return;
            }
            albumImages = dao.findAlbumImages(id, offset);  //offset 0,5,15,...
            albumcreator = dao.getAuthorUsernameById(id);
            System.out.println("Album ID: " + id);
            System.out.println("Number of images found: " + albumImages.size());
            System.out.println("title: " + album.getTitle());
            System.out.println("date: " + album.getDate());
            System.out.println("author: " + albumcreator);
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in album's project database extraction");
            return;
        }

        int num = 0;
        try {
            num = dao.countAlbumImages(id);
            System.out.println("tot image: "+num);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        boolean previous = offset > 0;
        boolean next = offset + 5 < num;

        System.out.println("Next: " + next);
        System.out.println("Previous: " + previous);
        System.out.println("Offset: " + offset);


        //Thymeleaf
        String path = "/WEB-INF/album.html";
        ServletContext servletContext = getServletContext();
        final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
        ctx.setVariable("album", album);
        ctx.setVariable("albumcreator", albumcreator);
        ctx.setVariable("albumImages", albumImages);
        ctx.setVariable("next", offset+5);
        ctx.setVariable("previous", offset-5);
        ctx.setVariable("totalImagesNumber", num);
        templateEngine.process(path, ctx, response.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }

    @Override
    public void destroy() {
        super.destroy();
    }
}
