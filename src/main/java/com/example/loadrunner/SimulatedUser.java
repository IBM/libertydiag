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
import java.util.Base64;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.example.util.ApplicationCache;

import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

public class SimulatedUser implements Callable<SimulatedUserResult> {

	private static final String CLASS = SimulatedUser.class.getCanonicalName();
	private static final Logger LOG = Logger.getLogger(CLASS);

	private Client client;
	private URL target;
	private int totalRequests;
	private String userName;
	private String password;
	private String method;
	private String entity;
	private boolean infiniteRequests;
	private String loadIdentifier;
	private SimulatedUserResult result;

	@Override
	public SimulatedUserResult call() throws Exception {
		if (LOG.isLoggable(Level.FINE))
			LOG.fine(this + " started " + totalRequests + " requests");

		String encoding = null;
		if (userName != null && userName.length() > 0) {
			encoding = Base64.getEncoder().encodeToString((userName + ":" + password).getBytes());
		}
		
		result = new SimulatedUserResult();

		try {
			final long overflowPoint = Long.MAX_VALUE - 1;
			long i = 0;
			while (LoadRunner.RUNNERS.containsKey(loadIdentifier) && (infiniteRequests || (!infiniteRequests && i < totalRequests))) {
				final long startTime = System.currentTimeMillis();
				
				WebTarget webTarget = client.target(target.toURI());
				Invocation.Builder requestBuilder = webTarget.request();
				
				if (encoding != null) {
					requestBuilder.header(HttpHeaders.AUTHORIZATION, "Basic " + encoding);
				}
				
				Response response = null;
				
				try {
					switch (method.toUpperCase()) {
					case HttpMethod.GET:
						response = requestBuilder.get();
						break;
					case HttpMethod.POST:
						response = requestBuilder.post(Entity.xml(entity));
						break;
					case HttpMethod.PUT:
						response = requestBuilder.put(Entity.xml(entity));
						break;
					case HttpMethod.DELETE:
						response = requestBuilder.delete();
						break;
					case HttpMethod.HEAD:
						response = requestBuilder.head();
						break;
					case HttpMethod.PATCH:
						response = requestBuilder.method(HttpMethod.PATCH, Entity.xml(entity));
						break;
					default:
						throw new UnsupportedOperationException("Method not supported: " + method);
					}
					
					Status responseCode = response.getStatusInfo().toEnum();
					if (responseCode != Status.OK) {
						if (LOG.isLoggable(Level.FINE))
							LOG.fine(this + " unexpected HTTP code:" + response.getStatusInfo());
						
						result.errors++;
					}
					
					// Read the whole response. Usually (unless trace is enabled), we won't do
					// anything with the response; however, we still always want to read the whole
					// response because otherwise we may not get the true response time.
					String output = response.readEntity(String.class);
					if (LOG.isLoggable(Level.FINE))
						LOG.fine(this + " result body:" + output);

					result.count++;
					final long executionTime = System.currentTimeMillis() - startTime;
					result.totalExecutionTime += executionTime;
					if (executionTime > result.maxExecutionTime) {
						result.maxExecutionTime = executionTime;
					}
					if (result.minExecutionTime == 0 || executionTime < result.minExecutionTime) {
						result.minExecutionTime = executionTime;
					}
				} finally {
					if (response != null) {
						response.close();
					}
					ApplicationCache.returnClient(client);
				}
				i++;
				if (i == overflowPoint) {
					i = 0;
				}
			}
		} catch (Throwable t) {
			if (LOG.isLoggable(Level.SEVERE)) {
				LOG.log(Level.SEVERE, this + " error: " + t, t);
			}
		}

		if (LOG.isLoggable(Level.FINE))
			LOG.fine(this + " finished " + totalRequests + " requests");

		return result;
	}

	public URL getTarget() {
		return target;
	}

	public void setTarget(URL target) {
		this.target = target;
	}

	public int getTotalRequests() {
		return totalRequests;
	}

	public void setTotalRequests(int totalRequests) {
		this.totalRequests = totalRequests;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
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

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public boolean isInfiniteRequests() {
		return infiniteRequests;
	}

	public void setInfiniteRequests(boolean infiniteRequests) {
		this.infiniteRequests = infiniteRequests;
	}

	public String getLoadIdentifier() {
		return loadIdentifier;
	}

	public void setLoadIdentifier(String loadIdentifier) {
		this.loadIdentifier = loadIdentifier;
	}

	public SimulatedUserResult getResult() {
		return result;
	}
}
