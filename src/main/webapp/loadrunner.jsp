<%@ page import="com.example.servlet.LoadRunnerServlet" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
  <head>
    <title>Load Runner</title>
    <meta charset="UTF-8">
    <meta name="theme-color" content="#ffffff">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <style type="text/css">
      body {
        margin: 40px auto;
        max-width: 650px;
        line-height: 1.6;
        font-size: 18px;
        color: #444;
        padding:0 10px;
      }
      
      h1, h2, h3 {
        line-height:1.2;
      }
      
      .fullwidth {
        width: 100%;
      }
      
      input[type=submit] {
         padding: 10px;
      }
      
      .notification {
        font-weight: bold;
        color: red;
        padding-left: 18px;
        padding-right: 18px;
        border: 1px solid black;
      }
      </style>
  </head>
  <body>
    <h1>Load Runner</h1>
    <%
    if (request.getParameter("started") != null) {
    %>
      <div class="notification">
        <p>
        Started at <%= request.getParameter("started") %>. Check messages.log for results.
				</p>
      </div>
    <%
    }
     %>
    <p>Execute concurrent requests</p>
    <form action="<%= LoadRunnerServlet.URL %>" method="get">
      <p>
        <label for="url">Target URL:</label><br />
        <input type="text" name="url" placeholder="Target URL" class="fullwidth" value="<%= request.getParameter("url") == null ? "" : request.getParameter("url") %>" required />
      </p>
      <p>
        <label for="method">Method:</label><br />
        <select name="method">
          <option value="get" <%= "get".equals(request.getParameter("method")) ? "selected" : "" %>>GET</option>
          <option value="post" <%= "post".equals(request.getParameter("method")) ? "selected" : "" %>>POST</option>
          <option value="put" <%= "put".equals(request.getParameter("method")) ? "selected" : "" %>>PUT</option>
          <option value="patch" <%= "patch".equals(request.getParameter("method")) ? "selected" : "" %>>PATCH</option>
          <option value="delete" <%= "delete".equals(request.getParameter("method")) ? "selected" : "" %>>DELETE</option>
          <option value="head" <%= "head".equals(request.getParameter("method")) ? "selected" : "" %>>HEAD</option>
        </select>
      </p>
      <p>
        <label for="entity">Entity (for POST/PUT/PATCH):</label><br />
        <textarea name="entity" rows="5" class="fullwidth"><%= request.getParameter("entity") == null ? "" : request.getParameter("entity") %></textarea>
      </p>
      <p>
        <label for="concurrentusers">Concurrent users:</label><br />
        <input type="number" name="concurrentusers" placeholder="Concurrent users" value="<%= request.getParameter("concurrentusers") == null ? "5" : request.getParameter("concurrentusers") %>" min="1" />
      </p>
      <p>
        <label for="totalrequests">Total requests:</label><br />
        <input type="number" name="totalrequests" placeholder="Total requests" value="<%= request.getParameter("totalrequests") == null ? "100" : request.getParameter("totalrequests") %>" min="1" />
      </p>
      <p>
        <label for="user">User (if needed):</label><br />
        <input autocomplete="new-password" type="text" name="user" placeholder="User" value="<%= request.getParameter("user") == null ? "" : request.getParameter("user") %>" />
      </p>
      <p>
        <label for="password">Password (if needed):</label><br />
        <input autocomplete="new-password" type="password" name="password" placeholder="Password" value="<%= request.getParameter("password") == null ? "" : request.getParameter("password") %>" />
      </p>
      <input type="submit" value="Start" />
    </form>
  </body>
</html>
