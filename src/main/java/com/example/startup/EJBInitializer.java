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

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;

@Singleton
@Startup
public class EJBInitializer {
	private static final String CLASS_NAME = EJBInitializer.class.getCanonicalName();
	private static final Logger LOG = Logger.getLogger(CLASS_NAME);

	@PostConstruct
	private void onStartup() {
		if (LOG.isLoggable(Level.FINER))
			LOG.entering(CLASS_NAME, "onStartup");

		if (LOG.isLoggable(Level.FINE))
			LOG.fine("libertydiag started ; " + this);

		if (LOG.isLoggable(Level.FINER))
			LOG.exiting(CLASS_NAME, "onStartup");
	}
}
