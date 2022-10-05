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
package com.example.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.example.util.BaseServlet;
import com.example.util.Constants;
import com.example.util.Utilities;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(Constants.CONTEXT_SERVLET + "SystemDump")
public class SystemDump extends BaseServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doWork(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
			throws ServletException, IOException {
		println(out, "Calling com.ibm.jvm.Dump.SystemDump()...");
		try {
			Class<?> dump = Utilities.findClass("com.ibm.jvm.Dump");
			Method triggerDump = dump.getMethod("triggerDump", new Class<?>[] { String.class });
			String dumpFile = (String) triggerDump.invoke(null, new Object[] { "system:request=exclusive+prepwalk" });
			if (dumpFile != null && dumpFile.length() > 0) {
				println(out, "Requested dump " + dumpFile);
			}
		} catch (NoSuchMethodException | ClassNotFoundException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw new ServletException(e);
		}
	}
}
