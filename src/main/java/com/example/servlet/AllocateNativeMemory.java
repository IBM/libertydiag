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
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.example.util.BaseServlet;
import com.example.util.Constants;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(Constants.CONTEXT_SERVLET + "AllocateNativeMemory")
public class AllocateNativeMemory extends BaseServlet {
	private static final long serialVersionUID = 1L;

	private static List<ByteBuffer> list = new ArrayList<ByteBuffer>();
	private static long total;

	@Override
	protected void doWork(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
			throws ServletException, IOException {
		int capacity = requestInt(request, "capacity", 1024);
		boolean save = requestBoolean(request, "save", true);
		boolean clear = requestBoolean(request, "clear", false);
		if (!clear) {
			println(out, "Allocating " + capacity + " byte DirectByteBuffer. Save=" + save);
			ByteBuffer buf = ByteBuffer.allocateDirect(capacity);
			if (save) {
				list.add(buf);
				total += capacity;
			}
		} else {
			println(out, "Trashing " + list.size() + " buffers of " + total + " bytes");
			list.clear();
			System.gc();
		}
	}
}
