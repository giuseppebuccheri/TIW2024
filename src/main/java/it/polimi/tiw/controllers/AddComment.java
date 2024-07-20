package it.polimi.tiw.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.tiw.beans.Comment;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.CommentDao;
import it.polimi.tiw.dao.UserDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import static it.polimi.tiw.utils.ConnectionHandler.getConnection;
import static it.polimi.tiw.utils.ParamsChecker.checkParams;

@WebServlet("/addComment")
@MultipartConfig
public class AddComment extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection = null;

    @Override
    public void init() throws ServletException {
        connection = getConnection(getServletContext());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        User user = (User) session.getAttribute("user");
        String commentText = request.getParameter("commentText");
        int imageId = 0;

        if(request.getParameter("imageId") != null){
            try {
                imageId = Integer.parseInt(request.getParameter("imageId"));
            } catch (NumberFormatException | NullPointerException e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("Not possible to recover image, try later");
                return;
            }
        }
        else{
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("no image id found");
            return;
        }

        UserDao userDao= new UserDao(connection);

        if (!checkParams(commentText)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("please provide a commentId text");
            return;
        }

        CommentDao commentDao = new CommentDao(connection);
        int commentId = 0;
        Comment comment = null;
        try {
            commentId = commentDao.addComment(imageId, user.getId(), commentText);
            comment = commentDao.getCommentById(commentId);
            comment.setUsername(user.getUsername());
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to add commentId");
        }

        Gson gson = new GsonBuilder().setDateFormat("yyyy MMM dd").create();
        String json_comment = null;
        json_comment = gson.toJson(comment);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json_comment);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    public void destroy() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}