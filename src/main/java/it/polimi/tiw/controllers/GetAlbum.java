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
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.net.URLEncoder;

import static it.polimi.tiw.utils.ConnectionHandler.getConnection;
import static it.polimi.tiw.utils.ParamsChecker.checkParams;

@WebServlet("/album")
public class GetAlbum extends HttpServlet {
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

        String errorMessage = null;

        if (!checkParams(request.getParameter("id"))) {
            errorMessage = "incorret or missing album id";
            response.sendRedirect("home?errorMessage=" + URLEncoder.encode(errorMessage, "UTF-8"));
            return;
        }

        try {
            id = Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException | NullPointerException e) {
            errorMessage = "error processing album id";
            response.sendRedirect("home?errorMessage=" + URLEncoder.encode(errorMessage, "UTF-8"));
            return;
        }

        //Controllo offset se presente

        try {
            if (request.getParameter("offset") != null) {
                offset = Integer.parseInt(request.getParameter("offset"));
                //accetta solo offset positivi multipli di 5
                if (offset < 0 || offset % 5 != 0 )
                    offset=0;
            }
        } catch (NumberFormatException | NullPointerException e) {
            session.setAttribute("chosenAlbum",null);
            errorMessage = "error processing offset.";
            response.sendRedirect("home?errorMessage=" + URLEncoder.encode(errorMessage, "UTF-8"));
            return;
        }

        List<Image> albumImages;
        AlbumDao dao = new AlbumDao(connection);
        Album album;
        String albumcreator;

        try {
            album = dao.getAlbumById(id);
            if (album == null) {
                errorMessage = "album not found";
                response.sendRedirect("home?errorMessage=" + URLEncoder.encode(errorMessage, "UTF-8"));
                return;
            }

            albumImages = dao.findAlbumImages(id, offset);  //offset 0,5,15,...
            albumcreator = dao.getAuthorUsernameById(id);
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in album's project database extraction");
            return;
        }

        int num = 0;
        try {
            num = dao.countAlbumImages(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

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
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    @Override
    public void destroy() {
        super.destroy();
    }
}
