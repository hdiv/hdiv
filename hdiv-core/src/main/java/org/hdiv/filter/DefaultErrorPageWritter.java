/**
 * Copyright 2005-2016 hdiv.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hdiv.filter;

import java.io.PrintWriter;
import java.util.List;

import org.springframework.web.util.HtmlUtils;

/**
 * Utility class to creates default HDIV error page.
 * 
 * @since 2.1.7
 */
public class DefaultErrorPageWritter {

	/**
	 * Create default HDIV error page and write to the output.
	 * 
	 * @param out output to the response
	 * @param editableErrors existing editable errors to show in error page.
	 */
	public void writetErrorPage(final PrintWriter out, final List<ValidatorError> editableErrors) {

		out.println("<!DOCTYPE html>");
		out.println("<html>");
		out.println("<head>");
		out.println("<title>Hdiv | Unauthorized access</title>");
		out.println("<style type=\"text/css\">");
		out.println("body {");
		out.println("	margin: 0;");
		out.println("	font-family: verdana, Arial, Helvetica, sans-serif;");
		out.println("	font-size: 11px;");
		out.println("}");

		out.println("#errorWrapper {");
		out.println("	text-align: left;");
		out.println("	margin: 0 auto;");
		out.println("	width: 500px;");
		out.println("	height: 400px;");
		out.println("	color: #333333;");
		out.println("}");

		out.println("#errorHeader img {");
		out.println("	margin: 70px 0 38px 30px;");
		out.println("}");

		out.println("#errorHeader {");
		out.println("	background-color: rgb(60, 60, 60);");
		out.println("}");

		out.println("#errorBody {");
		out.println("	margin: 0 0 50px 0;");
		out.println("	padding: 5px 0 0 16px;");
		out.println("}");

		out.println("#errorBody p {");
		out.println("	font-size: 22px;");
		out.println("}");

		out.println("#errorBody li {");
		out.println("	font-size: 14px;");
		out.println("}");

		out.println("#errorFooterColor {");
		out.println("	height: 75px;");
		out.println("	background-color: #00cc77;");
		out.println("}");
		out.println("</style>");
		out.println("</head>");

		out.println("<body>");
		out.println("	<div id=\"errorWrapper\">");
		out.println("		<div id=\"errorHeader\">");
		String logo = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAP8AAAByCAYAAACcJgfMAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAA/dpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuNS1jMDE0IDc5LjE1MTQ4MSwgMjAxMy8wMy8xMy0xMjowOToxNSAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wTU09Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9tbS8iIHhtbG5zOnN0UmVmPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvc1R5cGUvUmVzb3VyY2VSZWYjIiB4bWxuczp4bXA9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC8iIHhtbG5zOmRjPSJodHRwOi8vcHVybC5vcmcvZGMvZWxlbWVudHMvMS4xLyIgeG1wTU06T3JpZ2luYWxEb2N1bWVudElEPSJ1dWlkOkFGMDQzNkVBOTAzQjExREI5MERGRDVDQ0RCRjczNDIzIiB4bXBNTTpEb2N1bWVudElEPSJ4bXAuZGlkOkI5ODg2RDA0NDc4RjExRTZBODI1RDBENUU0RDVEN0EwIiB4bXBNTTpJbnN0YW5jZUlEPSJ4bXAuaWlkOkI5ODg2RDAzNDc4RjExRTZBODI1RDBENUU0RDVEN0EwIiB4bXA6Q3JlYXRvclRvb2w9IkFkb2JlIElsbHVzdHJhdG9yIENDIDIwMTUgKE1hY2ludG9zaCkiPiA8eG1wTU06RGVyaXZlZEZyb20gc3RSZWY6aW5zdGFuY2VJRD0ieG1wLmlpZDpGMTdBQTQwQUQzRjIxMUU1Qjg2MkMwNzY0MEY1RTEyQSIgc3RSZWY6ZG9jdW1lbnRJRD0ieG1wLmRpZDpG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";
		out.println("			<img src=\"" + logo + "\" height=\"75\" width=\"168\" />");
		out.println("		</div>");
		out.println("		<div id=\"errorBody\">");
		out.println("			<p>Unauthorized access</p>");

		if (editableErrors != null) {
			out.println("			<ul>");

			for (ValidatorError error : editableErrors) {
				out.println("				<li>Values for field '" + error.getParameterName() + "' are not correct: ");
				// Escape HTML characters
				out.println(HtmlUtils.htmlEscape(error.getParameterValue()));
				out.println("				</li>");
			}
			out.println("			</ul>");
		}

		out.println("		</div>");
		out.println("		<div id=\"errorFooter\">");
		out.println("			<div id=\"errorFooterColor\"></div>");
		out.println("			<p>&copy; 2008-2016 hdivsecurity.com</p>");
		out.println("		</div>");
		out.println("	</div>");
		out.println("</body>");
		out.println("</html>");
	}

}