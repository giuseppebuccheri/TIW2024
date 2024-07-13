package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.dao.UserDao;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

@WebServlet("/signup")
public class SignupServlet extends HttpServlet {
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String repeat = request.getParameter("repeat");
        String email = request.getParameter("email");

        if (username == null || username.isEmpty() ||
                password == null || password.isEmpty() ||
                repeat == null || repeat.isEmpty() ||
                email == null || email.isEmpty()) {
            WebContext ctx = new WebContext(request, response, getServletContext(), request.getLocale());
            ctx.setVariable("errorMessage", "Please fulfill all the fields.");
            templateEngine.process("signup", ctx, response.getWriter());
            return;
        }

        UserDao dao = new UserDao(connection);

        try {
            if (dao.isNew(username)) {
                if (repeat.equals(password)){
                    if (dao.emailValid(email)){
                        if (dao.insertUser(username,password,email)){
                            HttpSession session = request.getSession();
                            session.setAttribute("username", username);

                            response.sendRedirect("home");
                        }
                        else {
                            WebContext ctx = new WebContext(request, response, getServletContext(), request.getLocale());
                            ctx.setVariable("errorMessage", "Please try again.");
                            templateEngine.process("signup", ctx, response.getWriter());
                        }
                    } else {
                        WebContext ctx = new WebContext(request, response, getServletContext(), request.getLocale());
                        ctx.setVariable("errorMessage", "Invalid email. Please try again. ");
                        templateEngine.process("signup", ctx, response.getWriter());
                    }
                }else {
                    WebContext ctx = new WebContext(request, response, getServletContext(), request.getLocale());
                    ctx.setVariable("errorMessage", "The two passwords are different.Please try again.");
                    templateEngine.process("signup", ctx, response.getWriter());
                }
            } else {
                WebContext ctx = new WebContext(request, response, getServletContext(), request.getLocale());
                ctx.setVariable("errorMessage", "This username already exists.Please try again.");
                templateEngine.process("signup", ctx, response.getWriter());
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            WebContext ctx = new WebContext(request, response, getServletContext(), request.getLocale());
            ctx.setVariable("errorMessage", "An error occurred while processing your request. Please try again.");
            templateEngine.process("signup", ctx, response.getWriter());
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req,resp);
    }
}
