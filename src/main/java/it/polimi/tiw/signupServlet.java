package it.polimi.tiw;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

@WebServlet("/signup")
public class signupServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private TemplateEngine templateEngine;

    @Override
    public void init() throws ServletException {
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(getServletContext());
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML5");
        this.templateEngine = new TemplateEngine();
        this.templateEngine.setTemplateResolver(templateResolver);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String repeat = request.getParameter("repeat");
        String email = request.getParameter("email");

        try {
            if (isNew(username)) {
                if (repeat.equals(password)){
                    if (emailValid(email)){
                        if (insertUser(username,password,email)){
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

    private boolean emailValid(String email) {
        boolean isValid = true;

        //TODO check how to do

        return isValid;
    }

    private boolean insertUser(String username, String password, String email) throws ClassNotFoundException, SQLException {
        boolean isValid = false;

        final String DB_URL = "jdbc:mysql://localhost:3306/tiw24?serverTimezone=UTC";
        final String USER = "root";
        final String PASS = "root";

        Class.forName("com.mysql.cj.jdbc.Driver");
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS)) {
            String sql = "INSERT INTO users (username, password,email) VALUES (?, ?,?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, username);
                statement.setString(2, password);
                statement.setString(3, email);
                int rowsAffected = statement.executeUpdate();
                if (rowsAffected > 0) {
                    isValid = true;
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        return isValid;
    }

    private boolean isNew(String username) throws ClassNotFoundException, SQLException {
        boolean isValid = true;

        final String DB_URL = "jdbc:mysql://localhost:3306/tiw24?serverTimezone=UTC";
        final String USER = "root";
        final String PASS = "root";

        Class.forName("com.mysql.cj.jdbc.Driver");
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS)) {
            String sql = "SELECT * FROM users WHERE username = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, username);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        isValid = false;
                    }
                }
            }
        }

        return isValid;
    }
}
