package com.google.sps.servlets;

import com.google.gson.Gson;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.sps.data.AuthReply;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/auth-status")
public class AuthStatusServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        UserService userService = UserServiceFactory.getUserService();
        Gson gson = new Gson();
        response.setContentType("application/json;");
        String authURL = "";
        boolean isLoggedIn = userService.isUserLoggedIn();

        if (!isLoggedIn) {
            authURL = userService.createLoginURL("/");
        } else {
            authURL = userService.createLogoutURL("/");
        }

        AuthReply authReply = new AuthReply(isLoggedIn, authURL);
        response.getWriter().println(gson.toJson(authReply));
    }
}
