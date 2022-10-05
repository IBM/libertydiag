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

import com.example.util.BaseServlet;
import com.example.util.Constants;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(Constants.CONTEXT_SERVLET + "NativeLibraries")
public class NativeLibraries extends BaseServlet {

	private static final long serialVersionUID = -8808645408696530510L;

	@Override
	protected void doWork(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
			throws ServletException, IOException {
		String name = requestString(request, "name", "");
		if (name.length() == 0) {
			println(out, "No library name name specified.");
		} else {
			boolean system = requestBoolean(request, "system", false);
			if (system) {
				println(out, "Calling System.loadLibrary()");
				System.load(name);
			} else {
				println(out, "Calling System.load()");
				System.loadLibrary(name);
			}
		}
	}
}
