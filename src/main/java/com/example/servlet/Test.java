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
import com.example.util.LRU;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(Constants.CONTEXT_SERVLET + "Test")
public class Test extends BaseServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doWork(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
			throws ServletException, IOException {

		String play = "PI is about " + Math.PI;
		println(out, play);
	}

	@Override
	protected void manipulateResponse(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.getSession(true);
		response.setHeader("Set-Cookie",
				"lqe_test1a=6169c5d6f7a04402851d1c8a19e1a439; Version=1; path=/lqe; HttpOnly; secure lqe_test1b=6169c5d6f7a04402851d1c8a19e1a439; Version=1; path=/lqe; HttpOnly; secure lqe_test1c=6169c5d6f7a04402851d1c8a19e1a439; Version=1; Path=/lqe; Secure; HttpOnly");
	}

	public static void main(String[] args) {
		LRU<String> lru = new LRU<String>(10);
		for (int i = 0; i < 20; i++) {
			lru.add("value" + i);
		}
		System.out.println(lru.size());
	}
}
