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

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.example.util.AnotherException;
import com.example.util.BaseServlet;
import com.example.util.Constants;
import com.example.util.SkipCatchException;
import com.example.util.TestException;

@WebServlet(Constants.CONTEXT_SERVLET + "Exceptions")
public class Exceptions extends BaseServlet {
	private static final long serialVersionUID = -4350781868385725191L;

	@Override
	protected void doWork(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
			throws ServletException, IOException {
		String throwconstructor = requestString(request, "throwconstructor", "blank");
		String throwmessage = requestString(request, "throwmessage", "An exception message.");
		boolean throwAllTheWay = requestBoolean(request, "throwAllTheWay", false);
		if ("blank".equals(throwconstructor)) {
			if (throwAllTheWay) {
				throw new SkipCatchException();
			} else {
				throw new TestException();
			}
		} else if ("message".equals(throwconstructor)) {
			if (throwAllTheWay) {
				throw new SkipCatchException(throwmessage);
			} else {
				throw new TestException(throwmessage);
			}
		} else if ("inner".equals(throwconstructor)) {
			if (throwAllTheWay) {
				throw new SkipCatchException(new AnotherException(throwmessage));
			} else {
				throw new TestException(new AnotherException(throwmessage));
			}
		} else if ("bothinner".equals(throwconstructor)) {
			if (throwAllTheWay) {
				throw new SkipCatchException(
						new TestException(throwmessage + " (outer)", new AnotherException(throwmessage + " (inner)")));
			} else {
				throw new TestException(throwmessage + " (outer)", new AnotherException(throwmessage + " (inner)"));
			}
		}
	}
}
