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

@WebServlet(Constants.CONTEXT_SERVLET + "LargeResponse")
public class LargeResponse extends BaseServlet {

	private static final long serialVersionUID = -2572166286865676993L;

	@Override
	protected void doWork(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
			throws ServletException, IOException {
		int count = requestInt(request, "count", 1024 * 1024);
		String incomingPattern = requestString(request, "pattern", "x");
		int patternMultiply = requestInt(request, "patternMultiply", 1);
		boolean flush = requestBoolean(request, "flush", false);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < patternMultiply; i++) {
			sb.append(incomingPattern);
		}

		char[] pattern = sb.toString().toCharArray();

		for (int i = 0; i < count; i++) {
			out.write(pattern);
			if (flush) {
				out.flush();
			}
		}
	}
}
