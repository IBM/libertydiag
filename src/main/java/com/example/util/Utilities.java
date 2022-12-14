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
package com.example.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Utilities {

	private static final String CLASS_NAME = Utilities.class.getCanonicalName();
	private static final Logger LOG = Logger.getLogger(CLASS_NAME);

	public static ConcurrentHashMap<String, Class<?>> LOADED_CLASSES = new ConcurrentHashMap<String, Class<?>>();

	public static void callStatic(String className, String methodName) {
		try {
			Class<?> c = findClass(className);
			c.getMethod(methodName).invoke(null);
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

	public static void callStatic1String(String className, String methodName, String arg1) {
		try {
			Class<?> c = findClass(className);
			c.getMethod(methodName, String.class).invoke(null, arg1);
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

	public static String getStaticFieldString(String className, String fieldName) {
		try {
			Class<?> c = findClass(className);
			return (String) c.getField(fieldName).get(null);
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

	public static Class<?> findClass(String className) throws ClassNotFoundException {
		Class<?> c = LOADED_CLASSES.get(className);
		if (c == null) {
			c = Class.forName(className);
			LOADED_CLASSES.put(className, c);
		}
		return c;
	}

	public static String getVersion() {

		if (LOG.isLoggable(Level.FINER))
			LOG.entering(CLASS_NAME, "getVersion");

		String result = "0.0.0";
		Properties properties = new Properties();
		try {
			try (InputStream inputStream = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream("/maven.properties")) {
				properties.load(inputStream);
			}

			result = properties.getProperty("appversion", result);
		} catch (IOException e) {

			// No need to stop processing if we can't read maven.properties for some reason
			if (LOG.isLoggable(Level.FINE))
				LOG.log(Level.FINE, "Error reading maven.properties", e);
		}

		if (LOG.isLoggable(Level.FINER))
			LOG.exiting(CLASS_NAME, "getVersion");

		return result;
	}
}
