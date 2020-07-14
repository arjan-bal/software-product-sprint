// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.gson.Gson;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.sps.data.Comment;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/comments")
public class DataServlet extends HttpServlet {
  private final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);
    PreparedQuery results = datastore.prepare(query);

    List<Comment> comments = new ArrayList<>();
    for (Entity entity : results.asIterable()) {
      long id = entity.getKey().getId();
      String authorEmail = (String) entity.getProperty("authorEmail");
      String message = (String) entity.getProperty("message");
      long timestamp = (long) entity.getProperty("timestamp");

      Comment comment = new Comment(id, timestamp, authorEmail, message);
      comments.add(comment);
    }

    Gson gson = new Gson();

    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(comments));
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // check auth status
    final UserService userService = UserServiceFactory.getUserService();

    if (!userService.isUserLoggedIn()) {
      response.sendRedirect("/");
      return;
    }

    String message = request.getParameter("message");
    String authorEmail = userService.getCurrentUser().getEmail();
    long timestamp = System.currentTimeMillis();

    if (message == null) {
      System.err.println("Message field missing");
      response.setContentType("text/html");
      response.getWriter().println("Message field missing.");
      return;
    }

    Entity commentEntity = new Entity("Comment");
    commentEntity.setProperty("authorEmail", authorEmail);
    commentEntity.setProperty("message", message);
    commentEntity.setProperty("timestamp", timestamp);

    datastore.put(commentEntity);

    response.sendRedirect("/index.html");
  }
}
