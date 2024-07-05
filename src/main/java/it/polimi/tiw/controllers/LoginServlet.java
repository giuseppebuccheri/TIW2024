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

import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.UserDao;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");


        UserDao dao = new UserDao(connection);
        User user = null;
        try {
            user = dao.checkUser(username,password);
            if (user!=null) {
                HttpSession session = request.getSession();
                session.setAttribute("username", username);

                response.sendRedirect("home");
            } else {
                //todo fix controllo che la sessione sia null
                HttpSession session = request.getSession();
                if(session == null || session.isNew()){
                    WebContext ctx = new WebContext(request, response, getServletContext(), request.getLocale());
                    ctx.setVariable("errorMessage", "Invalid username or password. Please try again.");
                    templateEngine.process("index", ctx, response.getWriter());
                }
            }
        } catch (SQLException e) {
            WebContext ctx = new WebContext(request, response, getServletContext(), request.getLocale());
            ctx.setVariable("errorMessage", "An error occurred while processing your request. Please try again later.");
            templateEngine.process("index", ctx, response.getWriter());
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
