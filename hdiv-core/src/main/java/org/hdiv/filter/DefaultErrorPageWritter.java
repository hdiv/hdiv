/**
 * Copyright 2005-2015 hdiv.org
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
	public void writetErrorPage(PrintWriter out, List<ValidatorError> editableErrors) {

		out.println("<!DOCTYPE html>");
		out.println("<html>");
		out.println("<head>");
		out.println("<title>HDIV. Unauthorized access</title>");
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
		out.println("	background-color: rgb(51, 51, 51);");
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
		out.println("	background-color: #97d167;");
		out.println("	background: linear-gradient(to bottom, #97d167 0%,#64a231 100%);");
		out.println("}");
		out.println("</style>");
		out.println("</head>");

		out.println("<body>");
		out.println("	<div id=\"errorWrapper\">");
		out.println("		<div id=\"errorHeader\">");
		String logo = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAGIAAAAnCAYAAAD0MJ3RAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZS"
				+ "BJbWFnZVJlYWR5ccllPAAAAyFpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME"
				+ "1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIF"
				+ "hNUCBDb3JlIDUuNS1jMDE0IDc5LjE1MTQ4MSwgMjAxMy8wMy8xMy0xMjowOToxNSAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cm"
				+ "RmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdX"
				+ "Q9IiIgeG1sbnM6eG1wPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvIiB4bWxuczp4bXBNTT0iaHR0cDovL25zLmFkb2JlLm"
				+ "NvbS94YXAvMS4wL21tLyIgeG1sbnM6c3RSZWY9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9zVHlwZS9SZXNvdXJjZVJlZi"
				+ "MiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENDIChXaW5kb3dzKSIgeG1wTU06SW5zdGFuY2VJRD0ieG1wLmlpZD"
				+ "owNTY5RjE2RDUwMjQxMUUzQjgzMkU2M0JGQjRCNzNFMCIgeG1wTU06RG9jdW1lbnRJRD0ieG1wLmRpZDowNTY5RjE2RTUwMjQxMU"
				+ "UzQjgzMkU2M0JGQjRCNzNFMCI+IDx4bXBNTTpEZXJpdmVkRnJvbSBzdFJlZjppbnN0YW5jZUlEPSJ4bXAuaWlkOjA1NjlGMTZCNT"
				+ "AyNDExRTNCODMyRTYzQkZCNEI3M0UwIiBzdFJlZjpkb2N1bWVudElEPSJ4bXAuZGlkOjA1NjlGMTZDNTAyNDExRTNCODMyRTYzQk"
				+ "ZCNEI3M0UwIi8+IDwvcmRmOkRlc2NyaXB0aW9uPiA8L3JkZjpSREY+IDwveDp4bXBtZXRhPiA8P3hwYWNrZXQgZW5kPSJyIj8+qy"
				+ "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";
		out.println("			<img src=\"" + logo + "\" />");
		out.println("		</div>");
		out.println("		<div id=\"errorBody\">");
		out.println("			<p>Unauthorized access</p>");

		if (editableErrors != null) {
			out.println("			<ul>");

			for (ValidatorError error : editableErrors) {
				out.print("				<li>Values for field '" + error.getParameterName() + "' are not correct: ");
				String values = error.getParameterValue();
				// Escape HTML characters
				values = HtmlUtils.htmlEscape(values);
				out.print(values);
				out.println("				</li>");
			}
			out.println("			</ul>");
		}

		out.println("		</div>");
		out.println("		<div id=\"errorFooter\">");
		out.println("			<div id=\"errorFooterColor\"></div>");
		out.println("			<p>&copy; 2005-2015 hdiv.org</p>");
		out.println("		</div>");
		out.println("	</div>");
		out.println("</body>");
		out.println("</html>");
	}

}