<%@ page import="java.util.Map, com.example.servlet.LoadRunnerServlet, com.example.loadrunner.*" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%
if (request.getParameter("clear") != null) {
  LoadRunner.clear();
  response.sendRedirect("/loadrunner.jsp");
}
%>

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

      hr {
        margin-top: 30px;
        margin-bottom: 30px;
      }
      
      fieldset {
        margin-bottom: 20px;
      }
      
      .disabled {
        color: gray;
      }
      
      .errors {
        color: red;
      }
    </style>
    <script type="text/javascript">
      function changeInfinite() {
    	if (document.getElementById('infinite').checked) {
          document.getElementById('totalrequests').disabled = true;
          document.getElementById('totalrequestslabel').className = 'disabled';
    	} else {
          document.getElementById('totalrequests').disabled = false;
          document.getElementById('totalrequestslabel').className = '';
    	}
      }
    </script>
  </head>
  <body>
    <h1>Load Runner</h1>
    <%
    if (LoadRunner.RUNNERS.size() > 0) {
    %>
      <h2>Running</h2>
      <p>Refresh this page for updated status (refreshing does not start a new load)</p>
      <ol>
      <%
      for (Map.Entry<String, LoadRunner> runner : LoadRunner.RUNNERS.entrySet()) {
        LoadRunnerResult pendingResult = runner.getValue().getPendingResult();
  		final long now = System.currentTimeMillis();
  		final long time = now - runner.getValue().getStarted();
      %>
        <li><%= runner.getKey() %><%= pendingResult.totalResults.errors == 0 ? "" : " (some errors!)" %>: <span<%= pendingResult.totalResults.errors == 0 ? "" : " class=\"errors\"" %>><%= pendingResult.getStatus(time) %></span> | <a href="<%= LoadRunnerServlet.URL %>?stop=<%= runner.getKey() %>">Stop Run</a></li>
      <%
      }
      %>
      </ol>
      <hr />
    <%
    }
    %>
    <%
    if (LoadRunner.COMPLETED_RUNNERS.size() > 0) {
    %>
      <h2>Completed</h2>
      <details>
        <summary>Expand to see all completed runs</summary>
        <ol>
        <%
        for (LoadRunnerResult runner : LoadRunner.COMPLETED_RUNNERS) {
        %>
          <li><%= runner.loadIdentifier %><%= runner.totalResults.errors == 0 ? "" : " (some errors!)" %>: <span<%= runner.totalResults.errors == 0 ? "" : " class=\"errors\"" %>><%= runner.status %></span></li>
        <%
        }
        %>
        </ol>
      </details>
      <hr />
    <%
    }
    %>
    <h2>Create New Load</h2>
    <form action="<%= LoadRunnerServlet.URL %>" method="get">
      <fieldset>
        <legend>Destination</legend>
        <p>
          <label for="url">Target URL:</label><br />
          <input type="text" id="url" name="url" placeholder="Target URL" class="fullwidth" value="<%= request.getParameter("url") == null ? "" : request.getParameter("url") %>" required />
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
      </fieldset>
      <fieldset>
        <legend>Optional request security</legend>
        <p>
          <label for="user">User (if needed):</label><br />
          <input autocomplete="new-password" type="text" id="user" name="user" placeholder="User" value="<%= request.getParameter("user") == null ? "" : request.getParameter("user") %>" />
        </p>
        <p>
          <label for="password">Password (if needed):</label><br />
          <input autocomplete="new-password" type="password" id="password" name="password" placeholder="Password" value="<%= request.getParameter("password") == null ? "" : request.getParameter("password") %>" />
        </p>
      </fieldset>
      <fieldset>
        <legend>Request volume</legend>
        <p>
          <label for="totalrequests" id="totalrequestslabel"<%= request.getParameter("infinite") == null ? "" : " class=\"disabled\"" %>>Total requests:</label><br />
          <input type="number" id="totalrequests" name="totalrequests" placeholder="Total requests" value="<%= request.getParameter("totalrequests") == null ? "100" : request.getParameter("totalrequests") %>" min="1"<%= request.getParameter("infinite") == null ? "" : " disabled" %> />
        </p>
        <p>
          <input type="checkbox" id="infinite" name="infinite" onchange="changeInfinite()"<%= request.getParameter("infinite") == null ? "" : " checked" %>>
          <label for="infinite">Infinite (until manually stopped)</label>
        </p>
      </fieldset>
      <fieldset>
        <legend>Request concurrency</legend>
        <p>
          <label for="concurrentusers">Concurrent users:</label><br />
          <input type="number" name="concurrentusers" placeholder="Concurrent users" value="<%= request.getParameter("concurrentusers") == null ? "5" : request.getParameter("concurrentusers") %>" min="1" />
        </p>
      </fieldset>
      <p>
        <label for="entity">Optional request body (for POST/PUT/PATCH):</label><br />
        <textarea name="entity" rows="5" class="fullwidth"><%= request.getParameter("entity") == null ? "" : request.getParameter("entity") %></textarea>
      </p>
      <input type="submit" value="Start" />
    </form>
    <hr />
    <p><a href="/">Back to the home page</a></p>
  </body>
</html>
