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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.enterprise.concurrent.ManagedExecutorService;
import jakarta.ws.rs.client.Client;

public class LoadRunner implements Callable<LoadRunnerResult> {

	public static final Map<String, LoadRunner> RUNNERS = new ConcurrentHashMap<>();
	public static final List<LoadRunnerResult> COMPLETED_RUNNERS = Collections
			.synchronizedList(new ArrayList<LoadRunnerResult>());

	private static AtomicLong RUNS = new AtomicLong();
	private static final String CLASS = LoadRunner.class.getCanonicalName();
	private static final Logger LOG = Logger.getLogger(CLASS);

	private URL target;
	private int concurrentUsers;
	private int totalRequests;
	private boolean infiniteRequests;
	private String userName;
	private String password;
	private String method;
	private String entity;
	private ManagedExecutorService executorService;
	private ArrayList<Client> clients;
	private String loadIdentifier;
	private LoadRunnerResult loadResult;
	private List<SimulatedUser> users;
	private long started;

	@Override
	public LoadRunnerResult call() throws Exception {
		if (LOG.isLoggable(Level.FINER))
			LOG.entering(CLASS, "call", this + " called");

		started = System.currentTimeMillis();

		loadResult = new LoadRunnerResult();
		users = new ArrayList<>();

		loadIdentifier = "Run" + RUNS.incrementAndGet();
		loadResult.loadIdentifier = loadIdentifier;
		RUNNERS.put(loadIdentifier, this);

		try {
			List<Future<SimulatedUserResult>> futures = new ArrayList<>();
			for (int i = 0; i < concurrentUsers; i++) {
				SimulatedUser task = createTask();
				users.add(task);
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

		if (RUNNERS.containsKey(loadIdentifier)) {
			final long time = finishRunner(loadIdentifier, this, loadResult);

			if (LOG.isLoggable(Level.INFO))
				LOG.info(loadResult.status);

			if (LOG.isLoggable(Level.FINER))
				LOG.exiting(CLASS, "call", this + " finished in " + time + " ms: " + loadResult);
		} else {
			// Runner was stopped
		}
		return loadResult;
	}

	public static long stop(String loadIdentifier) {
		long result = -1;
		LoadRunner runner = RUNNERS.get(loadIdentifier);
		if (runner != null) {
			LoadRunnerResult stoppedResults = runner.getPendingResult();
			stoppedResults.loadIdentifier = loadIdentifier;
			runner.loadResult = stoppedResults;
			result = finishRunner(loadIdentifier, runner, stoppedResults);
			stoppedResults.status += " ; Manually stopped";
		}
		return result;
	}
	
	public static void clear() {
		if (LOG.isLoggable(Level.INFO))
			LOG.info("Clearing completed runners");
		COMPLETED_RUNNERS.clear();
	}

	private static synchronized long finishRunner(String loadIdentifier, LoadRunner runner,
			LoadRunnerResult accumulatedResults) {

		RUNNERS.remove(loadIdentifier);
		COMPLETED_RUNNERS.add(accumulatedResults);

		final long finished = System.currentTimeMillis();
		final long time = finished - runner.started;

		accumulatedResults.status = "Load Runner (" + loadIdentifier + ") finished to " + runner.target + " in "
				+ (finished - runner.started) + " ms; concurrency: " + runner.concurrentUsers + ", "
				+ accumulatedResults.getStatus(time);
		return time;
	}

	public LoadRunnerResult getPendingResult() {
		LoadRunnerResult result = new LoadRunnerResult();
		for (SimulatedUser user : users) {
			result.totalResults.add(user.getResult());
		}
		return result;
	}

	private SimulatedUser createTask() {
		SimulatedUser user = new SimulatedUser();
		user.setLoadIdentifier(loadIdentifier);
		user.setTarget(target);
		if (infiniteRequests) {
			user.setInfiniteRequests(true);
		} else {
			user.setTotalRequests(totalRequests / concurrentUsers);
		}
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

	public boolean isInfiniteRequests() {
		return infiniteRequests;
	}

	public void setInfiniteRequests(boolean infiniteRequests) {
		this.infiniteRequests = infiniteRequests;
	}

	public LoadRunnerResult getLoadResult() {
		return loadResult;
	}

	public long getStarted() {
		return started;
	}
}
