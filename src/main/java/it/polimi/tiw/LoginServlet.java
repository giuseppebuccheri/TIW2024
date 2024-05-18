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

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
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

        try {
            if (validateUser(username, password)) {
                HttpSession session = request.getSession();
                session.setAttribute("username", username);

                response.sendRedirect("home");
            } else {
                WebContext ctx = new WebContext(request, response, getServletContext(), request.getLocale());
                ctx.setVariable("errorMessage", "Invalid username or password. Please try again.");
                templateEngine.process("index", ctx, response.getWriter());
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            WebContext ctx = new WebContext(request, response, getServletContext(), request.getLocale());
            ctx.setVariable("errorMessage", "An error occurred while processing your request. Please try again later.");
            templateEngine.process("index", ctx, response.getWriter());
        }
    }

    private boolean validateUser(String username, String password) throws ClassNotFoundException, SQLException {
        boolean isValid = false;

        final String DB_URL = "jdbc:mysql://localhost:3306/tiw24?serverTimezone=UTC";
        final String USER = "root";
        final String PASS = "root";

        Class.forName("com.mysql.cj.jdbc.Driver");
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS)) {
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, username);
                statement.setString(2, password);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        isValid = true;
                    }
                }
            }
        }

        return isValid;
    }
}
