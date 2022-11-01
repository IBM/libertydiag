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
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import com.example.util.BaseServlet;
import com.example.util.Constants;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(Constants.CONTEXT_SERVLET + "Sleep")
public class Sleep extends BaseServlet {
	private static final long serialVersionUID = 1L;
	public static final int DEFAULT_DURATION = 1000;
	private static final Random random = ThreadLocalRandom.current();

	@Override
	protected void doWork(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
			throws ServletException, IOException {
		int duration = doSleep(request, out);

		println(out, "Finished sleeping for " + duration + "ms");
	}

	public static int doSleep(HttpServletRequest req) {
		return doSleep(req, null);
	}

	public static int doSleep(HttpServletRequest req, PrintWriter out) {
		int duration = DEFAULT_DURATION;
		String durationStr = req.getParameter("duration");

		if (durationStr != null) {
			duration = Integer.parseInt(durationStr);
			if (duration <= 0) {
				duration = DEFAULT_DURATION;
			}
		} else {
			String meanStr = req.getParameter("mean");
			if (meanStr != null) {
				double mean = Double.parseDouble(meanStr);
				double stddev = 1;
				String stddevStr = req.getParameter("stddev");
				if (stddevStr != null) {
					stddev = Double.parseDouble(stddevStr);
				}
				duration = (int) Math.round((random.nextGaussian() * stddev) + mean);
				if (duration < 0) {
					duration = (int)mean;
				}
			}
		}

		if (out != null) {
			BaseServlet.print(out, "Starting to sleep for " + duration + "ms", true, true);
		}

		try {
			Thread.sleep(duration);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return duration;
	}
}
