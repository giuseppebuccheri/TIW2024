package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.UserDao;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.utils.ConnectionHandler;

import static it.polimi.tiw.utils.ParamsChecker.checkParams;

@WebServlet("/login")
public class Login extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private TemplateEngine templateEngine = null;
    private Connection connection = null;

    @Override
    public void init() throws ServletException {
        ServletContext context = getServletContext();

        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(context);
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML5");
        this.templateEngine = new TemplateEngine();
        this.templateEngine.setTemplateResolver(templateResolver);

        connection = ConnectionHandler.getConnection(context);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getSession(false) != null && request.getSession(false).getAttribute("username") != null) {
            response.sendRedirect("home");
            return;
        }
        ServletContext servletContext = getServletContext();
        final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
        String path = "/index.html";
        templateEngine.process(path, ctx, response.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        String path = "/index.html";
        ServletContext servletContext = getServletContext();
        final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());

        //Controllo parametri ricevuti
        if (!checkParams(username) || !checkParams(password)) {
            String errorMessage = "Incorrect or missing values";
            ctx.setVariable("errorMessage", errorMessage);
            templateEngine.process(path, ctx, response.getWriter());
            return;
        }

        UserDao dao = new UserDao(connection);
        User user = null;

        try {
            user = dao.checkUser(username, password);
        } catch (SQLException e) {
            String errorMessage = "An error occurred while processing your request. Please try again later.";
            ctx.setVariable("errorMessage", errorMessage);
            templateEngine.process(path, ctx, response.getWriter());
        }

        if (user != null) {
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            response.sendRedirect(getServletContext().getContextPath()+"/home");
        } else {
            String errorMessage = "Invalid username or password. Please try again.";
            ctx.setVariable("errorMessage", errorMessage);
            templateEngine.process(path, ctx, response.getWriter());
        }
    }

    public void destroy() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
        }
    }
}
