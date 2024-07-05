package it.polimi.tiw.controllers;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
        String author = request.getParameter("author");
        String path = request.getParameter("path");
        String description = request.getParameter("description");
        String date = request.getParameter("date");
        String title = request.getParameter("title");

        if (author == null || author.isEmpty() ||
                path == null || path.isEmpty() ||
                description == null || description.isEmpty() ||
                title == null || title.isEmpty() ||
                date == null || date.isEmpty()) {
            WebContext ctx = new WebContext(request, response, getServletContext(), request.getLocale());
            ctx.setVariable("errorMessage", "Please fulfill all the fields.");
            templateEngine.process("upload", ctx, response.getWriter());
            return;
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
