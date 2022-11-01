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
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.example.servlet.Simple;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public abstract class BaseServlet extends HttpServlet {

	private static final long serialVersionUID = -2490637537644072450L;

	private static final String CLASS = BaseServlet.class.getCanonicalName();
	private static final Logger LOG = Logger.getLogger(CLASS);

	@SuppressWarnings("rawtypes")
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		manipulateResponse(request, response);

		response.setContentType("text/html");

		String name = getClass().getName();
		PrintWriter out = response.getWriter();
		out.println("<html>");
		out.println("<head>");
		out.println("<title>libertydiag: " + name + "</title>");
		out.println("</head>");
		out.println("<body>");
		out.println("<h1>" + name + "</h1>");

		String rparamsstr = "";
		Map rparams = request.getParameterMap();
		for (Object kvobj : rparams.entrySet()) {
			Map.Entry kv = (Map.Entry) kvobj;
			if (rparamsstr.length() > 0) {
				rparamsstr += "&";
			}
			Object val = kv.getValue();
			rparamsstr += kv.getKey() + "=";
			if (val == null) {
				rparamsstr += "null";
			} else {
				if (val instanceof String[]) {
					String[] strs = (String[]) val;
					for (int i = 0; i < strs.length; i++) {
						if (i > 0) {
							rparamsstr += ",";
						}
						rparamsstr += strs[i];
					}
				} else {
					rparamsstr += val.toString();
				}
			}
		}

		String intro = "Invoking " + name + " by " + getUser(request) + "... [" + rparamsstr + "]";

		// By default, we use INFO level because some of these diagnostic operations can be
		// dangerous so we'd want to record in case someone does something maliciously.
		// If the user doesn't want to see these messages, then set a trace including:
		// com.example.util.BaseServlet=warning
		if (LOG.isLoggable(Level.INFO))
			LOG.info(Helper.MESSAGE_PREFIX + intro);

		println(out, intro);

		try {
			doWork(request, response, out);
		} catch (Throwable t) {

			if (t instanceof SkipCatchException) {
				t.printStackTrace();
				throw (ServletException) t;
			}

			try {
				println(out, "Exception caught:");
				Helper.printThrowableToStream(out, t);
			} catch (Throwable t2) {
				t2.printStackTrace();
				throw new ServletException(t);
			}
		}

		out.flush();

		println(out, "Done");

		if (LOG.isLoggable(Level.INFO))
			LOG.info("libertydiag: Done " + name);

		out.println("</body>");
		out.println("</html>");
	}

	protected void manipulateResponse(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	}

	public static String getSessionId(HttpServletRequest request) {
		String result = "";
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : request.getCookies()) {
				String name = cookie.getName();
				if (name != null && name.contains(Simple.DEFAULT_SESSIONID)) {
					result = cookie.getValue();
				}
			}
		}
		return result;
	}

	public static String appendNumber(long l) {
		if (l == -1) {
			return "-1";
		}
		return "" + l + " (" + toHex(l) + ")";
	}

	public static int getPid() {
		int result = -1;
		String id = ManagementFactory.getRuntimeMXBean().getName();
		int i = id.indexOf('@');
		if (i != -1) {
			result = Integer.parseInt(id.substring(0, i));
		}
		return result;
	}

	private String getUser(HttpServletRequest request) {
		String result = request.getRemoteUser();
		if (result == null || result.length() == 0) {
			result = "anonymous";
		}
		String str = request.getRemoteAddr();
		if (str != null && str.length() > 0) {
			result += " (" + str + ")";
		}
		return result;
	}

	protected void println(PrintWriter out, String message) {
		println(out, message, true, true);
	}

	protected void println(PrintWriter out, String message, boolean flush) {
		println(out, message, flush, true);
	}

	protected void println(PrintWriter out, String message, boolean flush, boolean br) {
		out.println("[" + new Date() + "] " + message + (br ? "<br />" : ""));
		if (flush) {
			out.flush();
		}
	}

	protected abstract void doWork(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
			throws ServletException, IOException;

	public static int requestInt(HttpServletRequest request, String name, int defaultValue) {
		String str = request.getParameter(name);

		if (str != null && str.length() > 0) {
			try {
				return Integer.parseInt(str);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
		return defaultValue;
	}

	public static long requestLong(HttpServletRequest request, String name, long defaultValue) {
		String str = request.getParameter(name);

		if (str != null && str.length() > 0) {
			try {
				return Long.parseLong(str);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
		return defaultValue;
	}

	public static String requestString(HttpServletRequest request, String name, String defaultValue) {
		String str = request.getParameter(name);
		if (str == null || str.length() == 0) {
			str = defaultValue;
		}
		return str;
	}

	public static boolean requestBoolean(HttpServletRequest request, String name, boolean value) {
		String str = request.getParameter(name);

		if (str != null && str.length() > 0) {
			str = str.toLowerCase().trim();
			if (str.equals("1") || str.equals("true") || str.equals("on")) {
				value = true;
			}
		}
		return value;
	}

	public static String encode(Object obj) {
		String str = null;

		if (obj != null) {
			str = obj.toString();
			str = str.replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("&", "&amp;");
		}
		return str;
	}

	public static String toHex(long l) {
		return "0x" + Long.toString(l, 16).toUpperCase();
	}

	public static void checkNullOrBlank(String s, String error) {
		if (s == null || s.length() == 0) {
			throw new IllegalArgumentException("Argument not found: " + error);
		}
	}
}
