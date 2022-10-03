package com.example.rest;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("helloworld")
public class HelloWorldJAXRS {

	private static final String CLASS_NAME = HelloWorldJAXRS.class.getCanonicalName();
	private static final Logger LOG = Logger.getLogger(CLASS_NAME);
	private static final String METHOD_EXECUTE = "execute";

	@Path("execute")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> execute() throws Throwable {

		if (LOG.isLoggable(Level.FINER))
			LOG.entering(CLASS_NAME, METHOD_EXECUTE);

		Map<String, Object> json = new HashMap<>();
		json.put("message", "Hello World @ " + Instant.now());

		if (LOG.isLoggable(Level.FINER))
			LOG.exiting(CLASS_NAME, METHOD_EXECUTE, json);

		return json;
	}
}
