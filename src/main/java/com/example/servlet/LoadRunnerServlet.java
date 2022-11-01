/*******************************************************************************
 * (c) Copyright IBM Corporation 2022.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.example.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.example.loadrunner.LoadRunner;
import com.example.util.ApplicationCache;
import com.example.util.Constants;

import jakarta.annotation.Resource;
import jakarta.enterprise.concurrent.ManagedExecutorService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;

/**
 * Load runner servlet. Example usage:
 * 
 * <pre>
 * curl -u user1:password "http://localhost:9080/servlet/LoadRunner?url=https%3A%2F%2Flocalhost%3A9443%2Fexercisedb&user=user1&password=password&activity=insertselectdelete&concurrentusers=1&totalrequests=1"
 * </pre>
 */
@WebServlet(LoadRunnerServlet.URL)
//@ServletSecurity(@HttpConstraint(rolesAllowed = "users"))
public class LoadRunnerServlet extends HttpServlet {

	public static final String URL = Constants.CONTEXT_SERVLET + "LoadRunner";

	private static final String CLASS = LoadRunnerServlet.class.getCanonicalName();
	private static final Logger LOG = Logger.getLogger(CLASS);

	private static final long serialVersionUID = 1L;

	@Resource(lookup = "concurrent/executorService1")
	private ManagedExecutorService executorService;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Date started = new Date();
		PrintWriter writer = null;
		try {

			String stopIdentifier = request.getParameter("stop");
			if (stopIdentifier != null) {
				LoadRunner.stop(stopIdentifier);
				response.sendRedirect("/loadrunner.jsp");
			} else if (request.getParameter("clear") != null) {
				LoadRunner.clear();
				response.sendRedirect("/loadrunner.jsp");
			} else {
				String urlString = request.getParameter("url");
				if (urlString == null) {
					throw new IllegalArgumentException("URL not provided");
				}

				URL target = new URL(urlString);

				String method = request.getParameter("method");
				if (method == null || method.length() == 0) {
					throw new IllegalArgumentException("Invalid HTTP method");
				}

				String entity = request.getParameter("entity");

				String concurrentusersString = request.getParameter("concurrentusers");
				if (concurrentusersString == null || concurrentusersString.length() == 0) {
					throw new IllegalArgumentException("Invalid concurrent users");
				}
				int concurrentusers = Integer.parseInt(concurrentusersString);
				if (concurrentusers <= 0) {
					throw new IllegalArgumentException("Concurrent users must be greater than 0");
				}

				String user = request.getParameter("user");
				String password = request.getParameter("password");

				boolean infiniteRequests = request.getParameter("infinite") != null;

				int totalrequests = 0;

				if (!infiniteRequests) {
					String totalrequestsString = request.getParameter("totalrequests");
					if (totalrequestsString == null || totalrequestsString.length() == 0) {
						throw new IllegalArgumentException("Invalid total requests");
					}
					totalrequests = Integer.parseInt(totalrequestsString);
					if (totalrequests <= 0) {
						throw new IllegalArgumentException("Total requests must be greater than 0");
					}
				}

				LoadRunner loadRunner = new LoadRunner();
				String info = "Starting load runner (" + loadRunner + ") to " + target + " with " + concurrentusers
						+ " concurrent users and " + (infiniteRequests ? "infinite" : totalrequests)
						+ " total requests";

				if (user != null && user.length() > 0) {
					info += " using user " + user;
				}

				if (LOG.isLoggable(Level.INFO))
					LOG.info(info);

				// Create all the clients
				ArrayList<Client> clients = new ArrayList<>();
				ApplicationCache.fillClients(clients, concurrentusers);
				while (clients.size() < concurrentusers) {
					clients.add(ClientBuilder.newClient());
				}

				// Now start the actual threads
				loadRunner.setTarget(target);
				loadRunner.setConcurrentUsers(concurrentusers);
				loadRunner.setTotalRequests(totalrequests);
				loadRunner.setInfiniteRequests(infiniteRequests);
				loadRunner.setExecutorService(executorService);
				loadRunner.setUserName(user);
				loadRunner.setPassword(password);
				loadRunner.setMethod(method);
				loadRunner.setEntity(entity);
				loadRunner.setClients(clients);

				executorService.submit(loadRunner);

				// We don't actually wait for the result, instead redirect back to the form
				// with a notification that the load runner started

				String redirectUrl = "/loadrunner.jsp?url="
						+ URLEncoder.encode(urlString, StandardCharsets.UTF_8.toString()) + "&method="
						+ URLEncoder.encode(method, StandardCharsets.UTF_8.toString()) + "&entity="
						+ URLEncoder.encode(entity, StandardCharsets.UTF_8.toString()) + "&concurrentusers="
						+ URLEncoder.encode(concurrentusersString, StandardCharsets.UTF_8.toString())
						+ "&totalrequests=" + totalrequests + "&user="
						+ URLEncoder.encode(user, StandardCharsets.UTF_8.toString()) + "&password="
						+ URLEncoder.encode(password, StandardCharsets.UTF_8.toString());

				if (infiniteRequests) {
					redirectUrl += "&infinite=on";
				}

				response.sendRedirect(redirectUrl);
			}

			// We redirected above, so no need to write any response

			// writer = startResponse(request, response, started,
			// HttpServletResponse.SC_OK);
			// writer.println(info);
			// finishResponse(writer, started);

		} catch (Throwable e) {
			if (writer == null) {
				writer = startResponse(request, response, started, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
			e.printStackTrace(writer);
			finishResponse(writer, started);
		}

		if (LOG.isLoggable(Level.FINER))
			LOG.exiting(CLASS, "service");
	}

	private PrintWriter startResponse(HttpServletRequest request, HttpServletResponse response, Date started,
			int status) throws IOException {
		response.setStatus(status);
		response.setContentType("text/plain");
		PrintWriter writer = response.getWriter();
		writer.println("Request started at " + started);
		writer.flush();
		return writer;
	}

	private void finishResponse(PrintWriter writer, Date started) {
		Date finished = new Date();
		long diff = finished.getTime() - started.getTime();
		writer.println("Request finished at " + finished + ". Processing took " + diff + " ms");
	}
}
