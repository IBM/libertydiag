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
package com.example.loadrunner;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.enterprise.concurrent.ManagedExecutorService;
import jakarta.ws.rs.client.Client;

public class LoadRunner implements Callable<LoadRunnerResult> {

	private static final String CLASS = LoadRunner.class.getCanonicalName();
	private static final Logger LOG = Logger.getLogger(CLASS);

	private URL target;
	private int concurrentUsers;
	private int totalRequests;
	private String userName;
	private String password;
	private String method;
	private String entity;
	private ManagedExecutorService executorService;
	private ArrayList<Client> clients;

	@Override
	public LoadRunnerResult call() throws Exception {
		if (LOG.isLoggable(Level.FINER))
			LOG.entering(CLASS, "call", this + " called");

		final long started = System.currentTimeMillis();
		LoadRunnerResult loadResult = new LoadRunnerResult();

		try {
			List<Future<SimulatedUserResult>> futures = new ArrayList<>();
			for (int i = 0; i < concurrentUsers; i++) {
				SimulatedUser task = createTask();
				futures.add(executorService.submit(task));
			}

			while (!futures.isEmpty()) {
				Future<SimulatedUserResult> future = futures.remove(0);
				SimulatedUserResult result = future.get();
				loadResult.totalResults.add(result);

				if (LOG.isLoggable(Level.FINE))
					LOG.fine(this + " finished " + result);
			}

		} catch (Throwable t) {
			if (LOG.isLoggable(Level.SEVERE))
				LOG.log(Level.SEVERE, "Error: " + t, t);
		}

		final long finished = System.currentTimeMillis();
		final long wallclockTime = finished - started;

		if (LOG.isLoggable(Level.INFO))
			LOG.info("Load Runner finished in " + (finished - started) + " ms; requests: "
					+ loadResult.totalResults.count + ", concurrency: " + concurrentUsers + ", average execution: "
					+ String.format("%.2f", loadResult.totalResults.getAverageExecutionTime()) + " ms, max execution: "
					+ loadResult.totalResults.maxExecutionTime + " ms, min execution: "
					+ loadResult.totalResults.minExecutionTime + " ms, throughput: "
					+ String.format("%.2f", loadResult.totalResults.getThroughput(wallclockTime)) + " tps");

		if (LOG.isLoggable(Level.FINER))
			LOG.exiting(CLASS, "call", this + " finished in " + wallclockTime + " ms: " + loadResult);

		return loadResult;
	}

	private SimulatedUser createTask() {
		SimulatedUser user = new SimulatedUser();
		user.setTarget(target);
		user.setTotalRequests(totalRequests / concurrentUsers);
		user.setUserName(userName);
		user.setPassword(password);
		user.setMethod(method);
		user.setEntity(entity);
		user.setClient(clients.remove(0));
		return user;
	}

	public URL getTarget() {
		return target;
	}

	public void setTarget(URL target) {
		this.target = target;
	}

	public int getConcurrentUsers() {
		return concurrentUsers;
	}

	public void setConcurrentUsers(int concurrentUsers) {
		this.concurrentUsers = concurrentUsers;
	}

	public int getTotalRequests() {
		return totalRequests;
	}

	public void setTotalRequests(int totalRequests) {
		this.totalRequests = totalRequests;
	}

	public ManagedExecutorService getExecutorService() {
		return executorService;
	}

	public void setExecutorService(ManagedExecutorService executorService) {
		this.executorService = executorService;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String user) {
		this.userName = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public ArrayList<Client> getClients() {
		return clients;
	}

	public void setClients(ArrayList<Client> clients) {
		this.clients = clients;
	}
}
