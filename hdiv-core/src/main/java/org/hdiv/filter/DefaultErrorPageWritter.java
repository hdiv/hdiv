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

import org.hdiv.context.RequestContextHolder;
import org.hdiv.util.HDIVUtil;
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
	 * @param context request context
	 * @param out output to the response
	 * @param editableErrors existing editable errors to show in error page.
	 */
	@SuppressWarnings("deprecation")
	public void writeErrorPage(final RequestContextHolder context, final PrintWriter out, final List<ValidatorError> editableErrors) {
		// @formatter:off
		out.write("<!DOCTYPE html>");
		out.write("<html>");
		out.write("<head>");
		out.write("<title>Hdiv | Unauthorized access</title>");
		out.write("<style type=\"text/css\">");
		out.write("body {\n"+
        "    background: #efeded none repeat scroll 0 0;\n"+
        "    margin: 0;\n"+
        "    font-family: verdana, Arial, Helvetica, sans-serif;\n"+
        "    font-size: 14px;\n"+
        "    color: #333;\n"+
        "}\n");

        out.write(".unauthorized {\n"+
        "    color: inherit;\n"+
        "    font-family: Open Sans,Helvetica,Arial,sans-serif;\n"+
        "    font-weight: 500;\n"+
        "    font-size: 19px;\n"+
        "    line-height: 1.1;\n"+
        "}\n");

        out.write(".hdiv-header {\n"+
        "    background: #fff none repeat scroll 0 0;\n"+
        "    border-bottom: 1px solid #eaeaea;\n"+
        "    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);\n"+
        "    box-sizing: border-box;\n"+
        "    height: 78px;\n"+
        "    padding-top: 15px;\n"+
        "    width: 100%;\n"+
        "}\n");

        out.write(".hdiv-error-box{\n"+
        "    background: #f9f9f9 none repeat scroll 0 0;\n"+
        "    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);\n"+
        "    box-sizing: border - box;\n"+
        "    margin: 30px auto 0;\n"+
        "    padding-left: 190px;\n"+
        "    width: 350px;\n"+
        "    display: block;\n"+
        "}\n");

        out.write(".hdiv-icon-box {\n"+
        "    margin-left: -190px;\n"+
        "    position: absolute;\n"+
        "    width: 160px;\n"+
        "}\n");

        out.write(".hdiv-icon-box img {\n"+
        "    display: block;\n"+
        "    margin: 40px auto 0;\n"+
        "}\n"+
        "img {\n"+
        "    vertical-align: middle;\n"+
        "}\n"+
        "img {\n"+
        "    border: 0 none;\n"+
        "}\n");

        out.write(".hdiv-error-box h1 {\n"+
        "    display: block;\n"+
        "    font-size: 19px;\n"+
        "    font-weight: 600;\n"+
        "    line-height: 20px;\n"+
        "    margin: 0;\n"+
        "    padding: 35px 0 0;\n"+
        "    margin: 0px 0px 20px 0px;\n"+
        "}\n");

        out.write(".hdiv-message-box{\n"+
        "    background: white; \n"+
        "    padding: 0 0 15px 30px; \n"+
        "    margin-left: -32px; \n"+
        "    border-left: 1px solid #eaeaea;\n"+
        "}\n");

        out.write(".hdiv-btn {\n"+
        "    -moz-user-select: none;\n"+
        "    background-image: none;\n"+
        "    border: 1px solid transparent;\n"+
        "    border-radius: 4px;\n"+
        "    cursor: pointer;\n"+
        "    display: inline-block;\n"+
        "    font-size: 14px;\n"+
        "    font-weight: 400;\n"+
        "    line-height: 1.42857;\n"+
        "    margin-bottom: 0;\n"+
        "    padding: 6px 12px;\n"+
        "    text-align: center;\n"+
        "    vertical-align: middle;\n"+
        "    white-space: nowrap;\n"+
        "    background-color: #eee;\n"+
        "    background-image: linear-gradient(to bottom, #f2f2f2, #e8e8e8);\n"+
        "    background-repeat: repeat-x;\n"+
        "    border: 1px solid #c4c4c4;\n"+
        "    box-shadow: 0 1px 0 #fff inset;\n"+
        "    color: #444 !important;\n"+
        "    text-shadow: 0 1px 0 #fff;\n"+
        "}\n"+
        "a, button, input {\n"+
        "    outline: 0 none !important;\n"+
        "    text-decoration: none;\n"+
        "}\n");

        out.write(".hdiv-btn-primary {\n"+
        "    background-color: #3b94e5;\n"+
        "    background-image: linear-gradient(to bottom, #32d692, #00cc77);\n"+
        "    background-repeat: repeat-x;\n"+
        "    border-color: #00b76b;\n"+
        "    box-shadow: 0 1px 0 rgba(255, 255, 255, 0.3) inset;\n"+
        "    color: #fff !important;\n"+
        "    text-shadow: 0 -1px 0 rgba(0, 0, 0, 0.25);\n"+
        "    text-transform: capitalize;\n"+
        "}\n");

        out.write("</style>");
		out.write("</head>");

        String logoHeader = "data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz4KPCEtLSBHZW5lcmF0b3I6IEFkb2JlIElsbHVzdHJhdG9yIDE2LjAuMCwgU1ZHIEV4cG9ydCBQbHVnLUluIC4gU1ZHIFZlcnNpb246IDYuMDAgQnVpbGQgMCkgIC0tPgo8IURPQ1RZUEUgc3ZnIFBVQkxJQyAiLS8vVzNDLy9EVEQgU1ZHIDEuMS8vRU4iICJodHRwOi8vd3d3LnczLm9yZy9HcmFwaGljcy9TVkcvMS4xL0RURC9zdmcxMS5kdGQiPgo8c3ZnIHZlcnNpb249IjEuMSIgaWQ9IkxheWVyXzEiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIgeG1sbnM6eGxpbms9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkveGxpbmsiIHg9IjBweCIgeT0iMHB4IgoJIHdpZHRoPSIzNjAuNTgzcHgiIGhlaWdodD0iNzAuNDYzcHgiIHZpZXdCb3g9IjAgMCAzNjAuNTgzIDcwLjQ2MyIgZW5hYmxlLWJhY2tncm91bmQ9Im5ldyAwIDAgMzYwLjU4MyA3MC40NjMiCgkgeG1sOnNwYWNlPSJwcmVzZXJ2ZSI+CjxnPgoJPHBhdGggZmlsbD0iIzFEMUQxQiIgZD0iTTEwLjUwMiwxMzQuMjUydi0xMy4wMzRjMC0wLjM4MS0wLjEzNi0wLjcwNS0wLjQwNC0wLjk3NWMtMC4yNy0wLjI2OS0wLjkzNy0wLjQwMy0xLjMxNy0wLjQwMwoJCWMtMC4zOCwwLTEuMTA4LDAuMTM1LTEuMzc3LDAuNDAzQzcuMTMzLDEyMC41MTMsNywxMjAuODM3LDcsMTIxLjIxOHYyOS41NWMwLDAuMzgxLDAuMTMzLDAuNzA2LDAuNDAzLDAuOTc2CgkJYzAuMjY5LDAuMjY5LDAuOTk3LDAuNDAzLDEuMzc3LDAuNDAzYzAuMzgxLDAsMS4wNDgtMC4xMzUsMS4zMTctMC40MDNjMC4yNjktMC4yNywwLjQwNC0wLjU5NSwwLjQwNC0wLjk3NmwwLjA1LTEzLjE1NwoJCWw3LjQ3OCwwLjAwMWMwLjM4LDAsMC43MDUtMC4xMzQsMC45NzMtMC40MDRjMC4yNy0wLjI2OSwwLjM1NC0wLjgzNCwwLjM1NC0xLjIxNGMwLTAuMzgxLTAuMTM1LTEuMDY2LTAuNDA0LTEuMzM2CgkJYy0wLjI2OC0wLjI2OC0wLjU5My0wLjQwNC0wLjk3My0wLjQwNEwxMC41MDIsMTM0LjI1MnoiLz4KCTxwYXRoIGZpbGw9IiMwMENDNzciIGQ9Ik0yMy40NzksMTM3LjQ4MmMxLjAyMSwwLDEuODUtMC44MjksMS44NS0xLjg1YzAtMS4wMjEtMC44MjktMS44NDgtMS44NS0xLjg0OAoJCWMtMS4wMjMsMC0xLjg1MSwwLjgyOC0xLjg1MSwxLjg0OEMyMS42MjksMTM2LjY1MywyMi40NTYsMTM3LjQ4MiwyMy40NzksMTM3LjQ4MiIvPgoJPHBhdGggZmlsbD0iIzFEMUQxQiIgZD0iTTMxLjY2NywxMjEuMzc5YzAtMC4zODEtMC4xMzYtMC43MDUtMC40MDQtMC45NzVjLTAuMjctMC4yNjktMC45MzctMC40MDMtMS4zMTctMC40MDMKCQljLTAuMzgsMC0xLjEwOCwwLjEzNS0xLjM3NywwLjQwM2MtMC4yNzEsMC4yNy0wLjQwMywwLjU5NC0wLjQwMywwLjk3NXYyOS41NWMwLDAuMzgxLDAuMTMzLDAuNzA1LDAuNDAzLDAuOTc1CgkJYzAuMjY5LDAuMjcsMC45OTcsMC40MDQsMS4zNzcsMC40MDRjMC4zODEsMCwxLjA0OC0wLjEzNSwxLjMxNy0wLjQwNGMwLjI2OS0wLjI3LDAuNDA0LTAuNTk0LDAuNDA0LTAuOTc1VjEyMS4zNzl6Ii8+CjwvZz4KPHBhdGggZmlsbD0iIzAwQ0M3NyIgZD0iTTM4LjY2NywxNzkuMzExYzAsMS44NDItMS40OTMsMy4zMzQtMy4zMzQsMy4zMzRIMy4zMzRjLTEuODQxLDAtMy4zMzQtMS40OTItMy4zMzQtMy4zMzRsMCwwCgljMC0xLjg0MSwxLjQ5My0zLjMzMywzLjMzNC0zLjMzM2gzMS45OTlDMzcuMTc0LDE3NS45NzgsMzguNjY3LDE3Ny40NywzOC42NjcsMTc5LjMxMUwzOC42NjcsMTc5LjMxMXoiLz4KPGc+Cgk8cGF0aCBmaWxsPSIjMUQxRDFCIiBkPSJNNzguOTUsMTkuMTI4Yy0wLjM1LDAuMTU5LTAuNjAzLDAuNDEyLTAuNzYsMC43NmwtNy44MzksMTguMjkyYy0wLjE1OSwwLjM0OC0wLjE1OSwwLjcwNCwwLDEuMDY4CgkJYzAuMTU4LDAuMzYzLDAuNDExLDAuNjI2LDAuNzYsMC43ODRjMC4yNTIsMC4wNjQsMC40NDQsMC4wOTQsMC41NywwLjA5NGMwLjYwMiwwLDEuMDMtMC4yODMsMS4yODQtMC44NTVsNy43OTEtMTguMjkxCgkJYzAuMTU5LTAuMzQ4LDAuMTU5LTAuNzA0LDAtMS4wNjhjLTAuMTU5LTAuMzY0LTAuNDExLTAuNjI1LTAuNzYtMC43ODRTNzkuMjk3LDE4Ljk2OSw3OC45NSwxOS4xMjggTTY1LjEyNCwxOS44ODgKCQljLTAuMTU5LTAuMzQ5LTAuNDExLTAuNjAyLTAuNzYxLTAuNzZjLTAuMzQ3LTAuMTU5LTAuNjk2LTAuMTU5LTEuMDQzLDBjLTAuMzUsMC4xNTktMC42MDMsMC40Mi0wLjc2MSwwLjc4NAoJCWMtMC4xNTgsMC4zNjUtMC4xNTgsMC43MiwwLDEuMDY4bDcuNzcsMTguMTM4YzAuMjUzLDAuNTM4LDAuNjgxLDAuODA5LDEuMjgzLDAuODA5YzAuMjU0LDAsMC40MjktMC4wMzMsMC41MjMtMC4wOTYKCQljMC4zNDgtMC4xNTgsMC42MDItMC40MiwwLjc1OS0wLjc4M2MwLjE1OS0wLjM2NC0xLjA2My0zLjU3OC0xLjIyMS0zLjkyOEw2NS4xMjQsMTkuODg4eiBNNTcuMzQyLDE5LjM4OQoJCWMtMC4yNjUsMC4yNy0wLjM5NywwLjYxLTAuMzk3LDEuMDIxdjE4LjI5MmMwLDAuMzgsMC4xMzIsMC43MTMsMC4zOTcsMC45OTdjMC4yNjQsMC4yODUsMC42LDAuNDI3LDEuMDAzLDAuNDI3CgkJYzAuMzc0LDAsMC42OTMtMC4xNDIsMC45NTgtMC40MjdjMC4yNjQtMC4yODQsMC4zOTctMC42MTcsMC4zOTctMC45OTdWMjAuNDExYzAtMC40MTItMC4xMzMtMC43NTEtMC4zOTctMS4wMjEKCQljLTAuMjY1LTAuMjY5LTAuNTg0LTAuNDA0LTAuOTU4LTAuNDA0QzU3Ljk0MiwxOC45ODUsNTcuNjA2LDE5LjEyLDU3LjM0MiwxOS4zODkgTTkuNjMzLDIyLjU5NlY5LjE5OQoJCWMwLTAuMzgxLTAuMTM2LTAuNzA0LTAuNDA0LTAuOTc0QzguOTU4LDcuOTU2LDguNjM0LDcuODIsOC4yNTQsNy44MmMtMC4zOCwwLTAuNzA2LDAuMTM2LTAuOTc0LDAuNDA0CgkJYy0wLjI3LDAuMjctMC40MDQsMC41OTMtMC40MDQsMC45NzR2MjkuNTVjMCwwLjM4MSwwLjEzNCwwLjcwNiwwLjQwNCwwLjk3NmMwLjI2OCwwLjI2OSwwLjU5NCwwLjQwMiwwLjk3NCwwLjQwMgoJCWMwLjM4LDAsMC43MDQtMC4xMzQsMC45NzQtMC40MDJjMC4yNjktMC4yNywwLjQwNC0wLjU5NSwwLjQwNC0wLjk3NlYyNS4zNTJoMTEuODJjMC4zOCwwLDAuNzA1LTAuMTM0LDAuOTc0LTAuNDA0CgkJYzAuMjY5LTAuMjY5LDAuNDA0LTAuNTk0LDAuNDA0LTAuOTc0YzAtMC4zODEtMC4xMzUtMC43MDQtMC40MDQtMC45NzRjLTAuMjY5LTAuMjY4LTAuNTkzLTAuNDA0LTAuOTc0LTAuNDA0SDkuNjMzeiBNMjguMDU5LDguMjI1CgkJYy0wLjI4MiwwLjI3LTAuNDIsMC41OTMtMC40MiwwLjk3NHYyOS41NWMwLDAuMzgxLDAuMTM5LDAuNzA2LDAuNDIsMC45NzZjMC4yNzksMC4yNjksMC42MDUsMC40MDIsMC45NzksMC40MDIKCQljMC4zNzQsMCwwLjY5NC0wLjEzNCwwLjk1OC0wLjQwMmMwLjI2NC0wLjI3LDAuMzk3LTAuNTk1LDAuMzk3LTAuOTc2VjkuMTk5YzAtMC4zODEtMC4xMzMtMC43MDQtMC4zOTctMC45NzQKCQljLTAuMjY0LTAuMjY5LTAuNTg0LTAuNDA0LTAuOTU4LTAuNDA0QzI4LjY2NSw3LjgyLDI4LjMzOCw3Ljk1NiwyOC4wNTksOC4yMjUiLz4KCTxwYXRoIGZpbGw9IiMwMENDNzciIGQ9Ik0yNS4yNzEsMjUuMTg4YzAuNzYsMCwxLjM3Ny0wLjYxNiwxLjM3Ny0xLjM3NmMwLTAuNzYtMC42MTctMS4zNzYtMS4zNzctMS4zNzZzLTEuMzc2LDAuNjE2LTEuMzc2LDEuMzc2CgkJQzIzLjg5NSwyNC41NzEsMjQuNTExLDI1LjE4OCwyNS4yNzEsMjUuMTg4Ii8+Cgk8cGF0aCBmaWxsPSIjMUQxRDFCIiBkPSJNNTEuMDU1LDI5LjYyMmMwLDQuMjk3LTMuNTAyLDcuNzkzLTcuODA4LDcuNzkzYy00LjMwNSwwLTcuODA5LTMuNDk2LTcuODA5LTcuNzkzCgkJYzAtNC4yOTgsMy41MDMtNy43OTQsNy44MDktNy43OTRDNDcuNTUyLDIxLjgyOCw1MS4wNTUsMjUuMzI0LDUxLjA1NSwyOS42MjIgTTUzLjgyNywzOC43NDJWOC4xNjEKCQljMC0wLjM4Mi0wLjEzMy0wLjcwOC0wLjM5OS0wLjk3OWMtMC4yNjctMC4yNy0wLjU4Ny0wLjQwNi0wLjk2My0wLjQwNmMtMC40MDcsMC0wLjc0MywwLjEzNi0xLjAwOSwwLjQwNgoJCWMtMC4yNjcsMC4yNzEtMC40LDAuNTk4LTAuNCwwLjk3OVYyMi42Yy0xLjkyNS0yLjEzNy00LjcxMy0zLjQ4My03LjgxLTMuNDgzYy01Ljc5NSwwLTEwLjUxLDQuNzEyLTEwLjUxLDEwLjUwNgoJCWMwLDUuNzkzLDQuNzE1LDEwLjUwNSwxMC41MSwxMC41MDVjMy4wOTcsMCw1Ljg4NS0xLjM0Nyw3LjgxLTMuNDgzdjIuMDk5YzAsMC4zODIsMC4xMzMsMC43MDksMC40LDAuOTc5CgkJYzAuMjY2LDAuMjcxLDAuNjAyLDAuNDA1LDEuMDA5LDAuNDA1YzAuMzc2LDAsMC42OTctMC4xMzUsMC45NjMtMC40MDVDNTMuNjk0LDM5LjQ1MSw1My44MjcsMzkuMTI0LDUzLjgyNywzOC43NDIiLz4KPC9nPgo8cmVjdCB4PSIxMTAuOTA4IiBmaWxsPSIjRUZFRkVGIiB3aWR0aD0iMC45ODQiIGhlaWdodD0iNDYuOTAyIi8+CjxnPgoJPGc+CgkJPHBhdGggZmlsbD0iIzAwQ0M3NyIgZD0iTTE0MC4yODQsMjAuNWg0LjA5YzEuODY0LDAsMy4yMTcsMC4yNjUsNC4wNTksMC43OTVjMC44NDIsMC41MzEsMS4yNjMsMS4zNzQsMS4yNjMsMi41MzEKCQkJYzAsMC43ODUtMC4xODQsMS40MjktMC41NTMsMS45MzNzLTAuODU4LDAuODA2LTEuNDcsMC45MDh2MC4wOWMwLjgzMywwLjE4NiwxLjQzNCwwLjUzMywxLjgwMiwxLjA0MgoJCQljMC4zNjksMC41MSwwLjU1MywxLjE4NywwLjU1MywyLjAzMmMwLDEuMTk5LTAuNDMzLDIuMTM0LTEuMjk5LDIuODA1Yy0wLjg2NiwwLjY3MS0yLjA0MiwxLjAwNy0zLjUyOCwxLjAwN2gtNC45MTdWMjAuNXoKCQkJIE0xNDMuMDcsMjUuNzA2aDEuNjE4YzAuNzU1LDAsMS4zMDItMC4xMTcsMS42NDEtMC4zNTFjMC4zMzgtMC4yMzQsMC41MDgtMC42MjEsMC41MDgtMS4xNmMwLTAuNTAzLTAuMTg1LTAuODY1LTAuNTUzLTEuMDgzCgkJCWMtMC4zNjgtMC4yMTktMC45NTEtMC4zMjgtMS43NDgtMC4zMjhoLTEuNDY1VjI1LjcwNnogTTE0My4wNywyNy45MTd2My40MjVoMS44MTZjMC43NjcsMCwxLjMzMy0wLjE0NywxLjY5OS0wLjQ0CgkJCXMwLjU0OC0wLjc0MywwLjU0OC0xLjM0OWMwLTEuMDktMC43NzktMS42MzYtMi4zMzctMS42MzZIMTQzLjA3eiIvPgoJCTxwYXRoIGZpbGw9IiMwMENDNzciIGQ9Ik0xNzQuMjQ4LDI3LjA1NGMwLDIuMTc1LTAuNTM5LDMuODQ4LTEuNjE4LDUuMDE2Yy0xLjA3OSwxLjE2OS0yLjYyNSwxLjc1My00LjYzOSwxLjc1MwoJCQljLTIuMDEzLDAtMy41Ni0wLjU4NC00LjYzOC0xLjc1M2MtMS4wNzktMS4xNjgtMS42MTgtMi44NDctMS42MTgtNS4wMzRjMC0yLjE4NywwLjU0MS0zLjg1OCwxLjYyMy01LjAxMQoJCQljMS4wODItMS4xNTQsMi42MzItMS43Myw0LjY1Mi0xLjczczMuNTY0LDAuNTgxLDQuNjM0LDEuNzQ0UzE3NC4yNDgsMjQuODczLDE3NC4yNDgsMjcuMDU0eiBNMTY0LjY1NiwyNy4wNTQKCQkJYzAsMS40NjgsMC4yNzksMi41NzQsMC44MzYsMy4zMTdjMC41NTgsMC43NDMsMS4zOTEsMS4xMTUsMi40OTksMS4xMTVjMi4yMjQsMCwzLjMzNS0xLjQ3OCwzLjMzNS00LjQzMgoJCQljMC0yLjk2LTEuMTA2LTQuNDQxLTMuMzE3LTQuNDQxYy0xLjEwOSwwLTEuOTQ1LDAuMzczLTIuNTA4LDEuMTJDMTY0LjkzOCwyNC40NzksMTY0LjY1NiwyNS41ODUsMTY0LjY1NiwyNy4wNTR6Ii8+Cgk8L2c+Cgk8Zz4KCQk8cGF0aCBmaWxsPSIjMDBDQzc3IiBkPSJNMTg5LjQyNywyOC42djUuMDQzaC0yLjc4NlYyMC41aDMuODI5YzEuNzg2LDAsMy4xMDcsMC4zMjUsMy45NjUsMC45NzYKCQkJYzAuODU2LDAuNjUsMS4yODUsMS42MzcsMS4yODUsMi45NjJjMCwwLjc3My0wLjIxMywxLjQ2LTAuNjM5LDIuMDYzYy0wLjQyNSwwLjYwMi0xLjAyNywxLjA3NC0xLjgwNywxLjQxNgoJCQljMS45NzgsMi45NTUsMy4yNjcsNC44NjMsMy44NjUsNS43MjZoLTMuMDkyTDE5MC45MSwyOC42SDE4OS40Mjd6IE0xODkuNDI3LDI2LjMzNGgwLjg5OWMwLjg4MSwwLDEuNTMxLTAuMTQ2LDEuOTUtMC40NAoJCQljMC40Mi0wLjI5MywwLjYzLTAuNzU1LDAuNjMtMS4zODRjMC0wLjYyMy0wLjIxNS0xLjA2Ni0wLjY0My0xLjMzMWMtMC40MjktMC4yNjQtMS4wOTMtMC4zOTYtMS45OTEtMC4zOTZoLTAuODQ2VjI2LjMzNHoiLz4KCTwvZz4KCTxnPgoJCTxwYXRoIGZpbGw9IiMwMENDNzciIGQ9Ik0yMjAuMDk3LDMzLjY0M2gtMy41NDJsLTUuNzE3LTkuOTQyaC0wLjA4MWMwLjExMywxLjc1NiwwLjE3MSwzLjAwOSwwLjE3MSwzLjc1OHY2LjE4NWgtMi40OVYyMC41aDMuNTE1CgkJCWw1LjcwOCw5Ljg0M2gwLjA2M2MtMC4wOS0xLjcwOC0wLjEzNS0yLjkxNi0wLjEzNS0zLjYyM1YyMC41aDIuNTA4VjMzLjY0M3oiLz4KCTwvZz4KCTxnPgoJCTxwYXRoIGZpbGw9IiMwMENDNzciIGQ9Ik0yNTUuMTExLDMwLjE0NmMwLDEuMTU3LTAuNDIsMi4wNTktMS4yNTksMi43MDZjLTAuODM5LDAuNjQ3LTEuOTc4LDAuOTcxLTMuNDE2LDAuOTcxCgkJCWMtMS41NTksMC0yLjc1Ny0wLjIwMS0zLjU5Ni0wLjYwMnYtMS40NzVjMC41MzksMC4yMjgsMS4xMjYsMC40MDgsMS43NjIsMC41NGMwLjYzNSwwLjEzMiwxLjI2NSwwLjE5OCwxLjg4OCwwLjE5OAoJCQljMS4wMTksMCwxLjc4Ni0wLjE5MywyLjMwMi0wLjU4YzAuNTE1LTAuMzg3LDAuNzcyLTAuOTI1LDAuNzcyLTEuNjE0YzAtMC40NTYtMC4wOTEtMC44MjktMC4yNzQtMS4xMTkKCQkJYy0wLjE4My0wLjI5MS0wLjQ4OC0wLjU1OS0wLjkxNi0wLjgwNWMtMC40MjktMC4yNDYtMS4wODEtMC41MjQtMS45NTYtMC44MzZjLTEuMjIyLTAuNDM4LTIuMDk2LTAuOTU2LTIuNjItMS41NTUKCQkJcy0wLjc4Ni0xLjM4MS0wLjc4Ni0yLjM0NmMwLTEuMDEzLDAuMzgtMS44MTksMS4xNDItMi40MThjMC43NjEtMC41OTksMS43NjgtMC44OTksMy4wMjEtMC44OTljMS4zMDYsMCwyLjUwOCwwLjI0LDMuNjA0LDAuNzE5CgkJCWwtMC40NzcsMS4zMzFjLTEuMDg1LTAuNDU2LTIuMTQtMC42ODMtMy4xNjQtMC42ODNjLTAuODEsMC0xLjQ0MSwwLjE3NC0xLjg5NiwwLjUyMWMtMC40NTYsMC4zNDgtMC42ODQsMC44My0wLjY4NCwxLjQ0NwoJCQljMCwwLjQ1NiwwLjA4NCwwLjgyOSwwLjI1MiwxLjExOWMwLjE2NywwLjI5MSwwLjQ1LDAuNTU3LDAuODUsMC44YzAuMzk4LDAuMjQzLDEuMDA4LDAuNTExLDEuODI5LDAuODA1CgkJCWMxLjM3OCwwLjQ5MiwyLjMyNiwxLjAxOSwyLjg0NSwxLjU4MkMyNTQuODUyLDI4LjUxNiwyNTUuMTExLDI5LjI0NywyNTUuMTExLDMwLjE0NnoiLz4KCTwvZz4KCTxnPgoJCTxwYXRoIGZpbGw9IiMwMENDNzciIGQ9Ik0yNzQuNzk0LDMzLjY0M2gtNy4zMjZWMjAuNWg3LjMyNnYxLjM1N2gtNS43OTh2NC4yMzRoNS40NDd2MS4zNDloLTUuNDQ3djQuODM2aDUuNzk4VjMzLjY0M3oiLz4KCTwvZz4KCTxnPgoJCTxwYXRoIGZpbGw9IiMwMENDNzciIGQ9Ik0yOTIuOTc5LDIxLjY3OGMtMS40NDQsMC0yLjU4NSwwLjQ4MS0zLjQyMSwxLjQ0M3MtMS4yNTQsMi4yNzktMS4yNTQsMy45NTEKCQkJYzAsMS43MiwwLjQwMywzLjA0OSwxLjIwOSwzLjk4N3MxLjk1NSwxLjQwNywzLjQ0NywxLjQwN2MwLjkxNywwLDEuOTYzLTAuMTY1LDMuMTM4LTAuNDk0djEuMzM5CgkJCWMtMC45MTEsMC4zNDEtMi4wMzUsMC41MTItMy4zNzEsMC41MTJjLTEuOTM2LDAtMy40My0wLjU4Ny00LjQ4MS0xLjc2MmMtMS4wNTItMS4xNzUtMS41NzctMi44NDQtMS41NzctNS4wMDcKCQkJYzAtMS4zNTQsMC4yNTMtMi41NDEsMC43NTktMy41NmMwLjUwNy0xLjAxOSwxLjIzOC0xLjgwNCwyLjE5NC0yLjM1NWMwLjk1NS0wLjU1MSwyLjA4MS0wLjgyNywzLjM3NS0wLjgyNwoJCQljMS4zNzksMCwyLjU4MywwLjI1MiwzLjYxNCwwLjc1NWwtMC42NDcsMS4zMTNDMjk0Ljk2OCwyMS45MTIsMjkzLjk3MywyMS42NzgsMjkyLjk3OSwyMS42Nzh6Ii8+Cgk8L2c+Cgk8Zz4KCQk8cGF0aCBmaWxsPSIjMDBDQzc3IiBkPSJNMzE4LjU1OCwyMC41djguNTA0YzAsMS40OTktMC40NTMsMi42NzYtMS4zNTcsMy41MzNjLTAuOTA1LDAuODU3LTIuMTQ4LDEuMjg1LTMuNzMsMS4yODUKCQkJcy0yLjgwNy0wLjQzMS0zLjY3My0xLjI5NGMtMC44NjYtMC44NjMtMS4yOTktMi4wNDktMS4yOTktMy41NlYyMC41aDEuNTI4djguNTc2YzAsMS4wOTcsMC4zLDEuOTM4LDAuODk5LDIuNTI2CgkJCWMwLjU5OSwwLjU4NywxLjQ3OSwwLjg4MSwyLjY0MywwLjg4MWMxLjEwOCwwLDEuOTYzLTAuMjk1LDIuNTYyLTAuODg1YzAuNi0wLjU5LDAuODk5LTEuNDM3LDAuODk5LTIuNTRWMjAuNUgzMTguNTU4eiIvPgoJCTxwYXRoIGZpbGw9IiMwMENDNzciIGQ9Ik0zMzMuMTkzLDI4LjE3N3Y1LjQ2NmgtMS41MjhWMjAuNWgzLjYwNWMxLjYxMSwwLDIuODAzLDAuMzA5LDMuNTczLDAuOTI2CgkJCWMwLjc3LDAuNjE4LDEuMTU0LDEuNTQ2LDEuMTU0LDIuNzg3YzAsMS43MzgtMC44ODEsMi45MTMtMi42NDMsMy41MjRsMy41NjksNS45MDZoLTEuODA4bC0zLjE4Mi01LjQ2NkgzMzMuMTkzeiBNMzMzLjE5MywyNi44NjUKCQkJaDIuMDk1YzEuMDc5LDAsMS44Ny0wLjIxNCwyLjM3My0wLjY0M2MwLjUwNC0wLjQyOCwwLjc1NS0xLjA3MSwwLjc1NS0xLjkyOGMwLTAuODY5LTAuMjU2LTEuNDk1LTAuNzY5LTEuODc5CgkJCWMtMC41MTItMC4zODMtMS4zMzUtMC41NzUtMi40NjgtMC41NzVoLTEuOTg2VjI2Ljg2NXoiLz4KCTwvZz4KCTxnPgoJCTxwYXRoIGZpbGw9IiMwMENDNzciIGQ9Ik0zNjAuMDMzLDMzLjY0M2gtNy4zMjZWMjAuNWg3LjMyNnYxLjM1N2gtNS43OTh2NC4yMzRoNS40NDd2MS4zNDloLTUuNDQ3djQuODM2aDUuNzk4VjMzLjY0M3oiLz4KCTwvZz4KPC9nPgo8L3N2Zz4K"; 

		out.write("<body class=\"error-page\">");
        out.write("	<header class=\"hdiv-header\"><img style=\"padding-left: 50px\" src=\"" + logoHeader + "\" /></header>");
        out.write("        <section class=\"hdiv-error-box\">");
        out.write("			<div class=\"hdiv-icon-box\"><img src=\"" + HDIVUtil.getCustomImage(context.getRequest()) + "\" height=\"75\" width=\"75\" /></div>");
        out.write("            <div class=\"hdiv-message-box\">");
        out.write("            <h1 class=\"unauthorized\">Unauthorized access</h1>");

        out.write("            <div style=\"margin-top:58px;\">");
        out.write("                <a href=\"javascript: window.history.back()\" class=\"hdiv-btn hdiv-btn-primary btn-small\"><i class=\"icon-long-arrow-left\"></i> Go back</a>&nbsp;&nbsp;");
        out.write("                <a href=\"" + context.getContextPath() + "\" class=\"hdiv-btn btn-small\">Home</a>");
        out.write("            </div>");

        if (editableErrors != null)
        {
            out.write("			<ul>");

            for (ValidatorError error : editableErrors)
            {
                out.write("				<li>Values for field '" + error.getParameterName() + "' are not correct: ");
                String values = error.getParameterValue();
                // Escape HTML characters
                values = HtmlUtils.htmlEscape(values);
                out.write(values);
                out.write("				</li>");
            }
            out.write("			</ul>");
        }
        out.write("    </div>");
        out.write("</section>");	
        out.write("</body>");
		out.write("</html>");
		// @formatter:on
	}

}