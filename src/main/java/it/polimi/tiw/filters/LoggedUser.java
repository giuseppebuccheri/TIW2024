package it.polimi.tiw.filters;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter({"/home", "/logout","/album","/createAlbum","/image","/addComment","/deleteImage"})
public class LoggedUser implements Filter{
    private TemplateEngine templateEngine = null;
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        ServletContext context = filterConfig.getServletContext();

        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(context);
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML5");
        this.templateEngine = new TemplateEngine();
        this.templateEngine.setTemplateResolver(templateResolver);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession s = req.getSession();
        String loginPath = "/index.html";

        //torna alla pagina di login se non autenticato
        if (s == null || s.getAttribute("user") == null) {
            ServletContext servletContext = req.getServletContext();
            final WebContext ctx = new WebContext(req, res, servletContext, req.getLocale());
            ctx.setVariable("errorMessage", "Please login");
            templateEngine.process(loginPath, ctx, res.getWriter());
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
