package it.polimi.tiw.controllers;

import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.UserDao;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.*;

@WebServlet("/upload")
public class UploadImageServlet extends HttpServlet {
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User u = (User) session.getAttribute("username");
        int id;

        try {
            id = getActiveID(u);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        String path = request.getParameter("path");
        String description = request.getParameter("description");
        String date = request.getParameter("date");
        String title = request.getParameter("title");

        if (id == 0 || path == null || path.isEmpty() ||
                description == null || description.isEmpty() ||
                title == null || title.isEmpty() ||
                date == null || date.isEmpty()) {
            WebContext ctx = new WebContext(request, response, getServletContext(), request.getLocale());
            ctx.setVariable("errorMessage", "Please fulfill all the fields.");
            templateEngine.process("upload", ctx, response.getWriter());
            return;
        }

    }

    private int getActiveID(User u) throws ClassNotFoundException {
        final String DB_URL = "jdbc:mysql://localhost:3306/tiw24?serverTimezone=UTC";
        final String USER = "root";
        final String PASS = "root";

        Class.forName("com.mysql.cj.jdbc.Driver");
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS)) {
            UserDao dao = new UserDao(connection);
            return dao.getIdByUsername(u.getUsername());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    @Override
    public void destroy() {
        super.destroy();
    }


}
