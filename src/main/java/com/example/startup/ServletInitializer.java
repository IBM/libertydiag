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
package com.example.startup;

import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class ServletInitializer implements ServletContextListener {
	private static final String CLASS_NAME = ServletInitializer.class.getCanonicalName();
	private static final Logger LOG = Logger.getLogger(CLASS_NAME);

	private static final String METHOD_CONTEXTINITIALIZED = "contextInitialized";
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		if (LOG.isLoggable(Level.FINER))
			LOG.entering(CLASS_NAME, METHOD_CONTEXTINITIALIZED);

		if (LOG.isLoggable(Level.FINE))
			LOG.fine("libertydiag started ; " + this);

		if (LOG.isLoggable(Level.FINER))
			LOG.exiting(CLASS_NAME, METHOD_CONTEXTINITIALIZED);
	}

	private static final String METHOD_CONTEXTDESTROYED = "contextDestroyed";
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		if (LOG.isLoggable(Level.FINER))
			LOG.entering(CLASS_NAME, METHOD_CONTEXTDESTROYED);

		if (LOG.isLoggable(Level.FINE))
			LOG.fine("libertydiag shut down ; " + this);

		if (LOG.isLoggable(Level.FINER))
			LOG.exiting(CLASS_NAME, METHOD_CONTEXTDESTROYED);
	}
}
