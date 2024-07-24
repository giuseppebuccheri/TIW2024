package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import static it.polimi.tiw.utils.ConnectionHandler.getConnection;
import static it.polimi.tiw.utils.ParamsChecker.checkParams;

@WebServlet("/signup")
public class Signup extends HttpServlet {
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
        ServletContext servletContext = getServletContext();
        final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
        String path = "/signup.html";
        String errorMessage = "Please fill the form";
        ctx.setVariable("errorMessage", errorMessage);
        templateEngine.process(path, ctx, response.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String repeat = request.getParameter("repeat");
        String email = request.getParameter("email");

        String path = "/signup.html";
        WebContext ctx = new WebContext(request, response, getServletContext(), request.getLocale());

        if (!checkParams(username) || !checkParams(password) || !checkParams(repeat) || !checkParams(email)) {
            ctx.setVariable("errorMessage", "Please fulfill all the fields.");
            templateEngine.process(path, ctx, response.getWriter());
            return;
        }

        UserDao dao = new UserDao(connection);

        try {
            if (dao.isNew(username)) {
                if (repeat.equals(password)){
                    if (emailValid(email)){
                        User user = dao.insertUser(username,password,email);
                        if (user != null){
                            HttpSession session = request.getSession();
                            session.setAttribute("user", user);

                            response.sendRedirect("home");
                        }
                        else {
                            ctx.setVariable("errorMessage", "Please try again.");
                            templateEngine.process(path, ctx, response.getWriter());
                        }
                    } else {
                        ctx.setVariable("errorMessage", "Invalid email. Please try again. ");
                        templateEngine.process(path, ctx, response.getWriter());
                    }
                }else {
                    ctx.setVariable("errorMessage", "The two passwords are different.Please try again.");
                    templateEngine.process(path, ctx, response.getWriter());
                }
            } else {
                ctx.setVariable("errorMessage", "This username already exists.Please try again.");
                templateEngine.process(path, ctx, response.getWriter());
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            ctx.setVariable("errorMessage", "An error occurred while processing your request. Please try again.");
            templateEngine.process(path, ctx, response.getWriter());
        }
    }

    //ritorna true se è valida
    public boolean emailValid(String email) {
        Pattern p = Pattern.compile(".+@.+\\.[a-z]+", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(email);
        return m.matches();
    }
}
