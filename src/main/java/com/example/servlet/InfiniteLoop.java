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

@WebServlet(Constants.CONTEXT_SERVLET + "InfiniteLoop")
public class InfiniteLoop extends BaseServlet {
	private static final long serialVersionUID = 1L;

	private long threshold;

	@Override
	protected void doWork(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
			throws ServletException, IOException {
		println(out, "Looping...");

		threshold = requestLong(request, "threshold", 0);
		final long timeout = requestLong(request, "timeout", 0);

		if (timeout > 0) {
			Thread timeoutThread = new Thread() {
				@Override
				public void run() {
					try {
						Thread.sleep(timeout);

						threshold = 1;

					} catch (InterruptedException e) {
						// Canceled for some reason
					}
				}
			};
			timeoutThread.setName("InfiniteLoop timeout thread");
			timeoutThread.setPriority(Thread.MAX_PRIORITY);
			timeoutThread.start();
		}

		long i = 0;
		while (true) {
			i += 2;

			if (threshold == 0) {
				i -= 2;
			} else {
				if (i > threshold) {
					break;
				}
			}
		}
	}
}
