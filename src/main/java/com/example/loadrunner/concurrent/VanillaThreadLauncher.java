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
package com.example.loadrunner.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VanillaThreadLauncher implements ThreadLauncher {
	private ExecutorService executorService;
	private static VanillaThreadLauncher singleton;

	private static final String CLASS = VanillaThreadLauncher.class.getCanonicalName();
	private static final Logger LOG = Logger.getLogger(CLASS);

	public static synchronized VanillaThreadLauncher getSingleton(int maxSize) {
		if (singleton == null) {
			singleton = new VanillaThreadLauncher(maxSize);
			if (LOG.isLoggable(Level.INFO))
				LOG.info("Creating singleton thread pool with max size = " + maxSize);
		}
		return singleton;
	}

	public VanillaThreadLauncher(int maxSize) {
		executorService = Executors.newFixedThreadPool(maxSize);
	}

	@Override
	public <T> Future<T> launch(Callable<T> task) {
		return executorService.submit(task);
	}
}
