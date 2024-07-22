package it.polimi.tiw.controllers;

import it.polimi.tiw.beans.Comment;
import it.polimi.tiw.beans.Image;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.CommentDao;
import it.polimi.tiw.dao.ImageDao;
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

@WebServlet("/image")
public class GetImage extends HttpServlet {
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
        User user= (User) session.getAttribute("user");

        int id = 0;

        try {
            id = Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException | NullPointerException e) {
            response.sendRedirect("home");
            return;
        }

        ImageDao imageDaodao = new ImageDao(connection);
        CommentDao commentDaodao = new CommentDao(connection);
        UserDao userDao = new UserDao(connection);
        Image image;
        List<Comment> comments;
        String imageauthor;

        try {
            image = imageDaodao.getImageById(id);
            if (image == null) {
                response.sendRedirect("home");
                return;
            }
            comments = commentDaodao.findCommentsByImage(id);
            setUsernames(comments,userDao);
            imageauthor = imageDaodao.getAuthorUsernameById(id);
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in image's project database extraction");
            return;
        }


        //Thymeleaf
        String path = "/WEB-INF/image.html";
        ServletContext servletContext = getServletContext();
        final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
        ctx.setVariable("image", image);
        ctx.setVariable("comments", comments);
        ctx.setVariable("imageauthor", imageauthor);
        ctx.setVariable("userId", user.getId());
        templateEngine.process(path, ctx, response.getWriter());
    }

    private void setUsernames(List<Comment> comments,UserDao dao) {
        for(Comment c: comments){
            c.setUsername(dao.getUsername(c.getUser()));
        }
    }
}
