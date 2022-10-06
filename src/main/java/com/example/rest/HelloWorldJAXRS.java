/*
 * Copyright 2022, 2022 IBM Corp. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.example.rest;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("helloworld")
public class HelloWorldJAXRS {

	private static final String CLASS_NAME = HelloWorldJAXRS.class.getCanonicalName();
	private static final Logger LOG = Logger.getLogger(CLASS_NAME);

	@Path("execute")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> execute() throws Throwable {

		if (LOG.isLoggable(Level.FINER))
			LOG.entering(CLASS_NAME, "execute");

		Map<String, Object> json = new HashMap<>();
		json.put("message", "Hello World @ " + Instant.now());

		if (LOG.isLoggable(Level.FINER))
			LOG.exiting(CLASS_NAME, "execute", json);

		return json;
	}
}
