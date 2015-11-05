/**
 * Copyright 2005-2013 hdiv.org
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
package org.hdiv.config.validations;

import junit.framework.TestCase;

import org.hdiv.config.HDIVConfig;
import org.hdiv.validator.EditableDataValidationResult;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class DefaultEditableValidationsTest extends TestCase {

	protected HDIVConfig config;

	@Override
	protected void setUp() throws Exception {

		ApplicationContext context = new ClassPathXmlApplicationContext(
				"org/hdiv/config/xml/hdiv-config-test-schema-default-validations.xml");
		this.config = context.getBean(HDIVConfig.class);
	}

	protected boolean validateValue(String value) {

		EditableDataValidationResult result = config.getEditableDataValidationProvider().validate("/editableTest.html", "paramName", new String[] { value }, "text");
		return result.isValid();
	}

	public void testSqlInjection() {

		// SQL Comment Sequences
		assertFalse(validateValue("OR 1#"));
		assertFalse(validateValue("DROP sampletable;--"));
		assertFalse(validateValue("admin'--"));
		assertFalse(validateValue("DROP/*comment*/sampletable"));
		assertFalse(validateValue("DR/**/OP/*bypass blacklisting*/sampletable"));
		assertFalse(validateValue("SELECT/*avoid-spaces*/password/**/FROM/**/Members"));
		assertFalse(validateValue("SELECT /*!32302 1/0, */ 1 FROM tablename"));
		assertFalse(validateValue("‘ or 1=1#"));
		assertFalse(validateValue("‘ or 1=1-- -"));
		assertFalse(validateValue("‘ or 1=1/*"));
		assertFalse(validateValue("1='1' or-- -"));
		assertFalse(validateValue("' /*!50000or*/1='1"));
		assertFalse(validateValue("' /*!or*/1='1"));
		assertFalse(validateValue("0/**/union/*!50000select*/table_name`foo`/**/"));

		// Payloads
		assertFalse(validateValue("' or 1=1#"));
		assertFalse(validateValue("') or ('1'='1--"));
		assertFalse(validateValue("1 OR \'1\'!=0"));
		assertFalse(validateValue("aaa\' or (1)=(1) #!asd"));
		assertFalse(validateValue("aaa\' OR (1) IS NOT NULL #!asd"));
		assertFalse(validateValue("' =+ '"));
		assertFalse(validateValue("asd' =- (-'asd') -- -a"));
		assertFalse(validateValue("aa\" =+ - \"0"));
		assertFalse(validateValue("aa' LIKE 0 -- -a"));
		assertFalse(validateValue("aa' LIKE md5(1) or '1"));
		assertFalse(validateValue("asd\"or-1=\"-1"));
		assertFalse(validateValue("asd\"or!1=\"!1"));
		assertFalse(validateValue("asd\"or!(1)=\"1"));
		assertFalse(validateValue("asd\" or ascii(1)=\"49"));
		assertFalse(validateValue("asd' or md5(5)^'1"));
		assertFalse(validateValue("\"asd\" or 1=\"1"));
		assertFalse(validateValue("' or id= 1 having 1 #1 !"));
		assertFalse(validateValue("' or id= 2-1 having 1 #1 !"));
		assertFalse(validateValue("aa'or BINARY 1= '1"));
		assertFalse(validateValue("aa'like-'aa"));

		assertFalse(validateValue("IF (SELECT * FROM login) BENCHMARK(1000000,MD5(1))"));
		assertFalse(validateValue("SELECT pg_sleep(10);"));
		assertFalse(validateValue("IF(SUBSTRING(Password,1,1)='2',BENCHMARK(100000,SHA1(1)),0) User,Password FROM mysql.user WHERE User = ‘root’;"));
		assertFalse(validateValue("select if( user() like 'root@%', benchmark(100000,sha1('test')), 'false' );"));
	}

	public void testXSS() {

		// HTML tags
		assertFalse(validateValue("<a href=javascript:..."));
		assertFalse(validateValue("<applet src=\"...\" type=text/html>"));
		assertFalse(validateValue("<applet src=\"data:text/html;base64,PHNjcmlwdD5hbGVydCgvWFNTLyk8L3NjcmlwdD4\" type=text/html>"));
		assertFalse(validateValue("<base href=javascript:..."));
		assertFalse(validateValue("<base href=... // change base URL to something else to exploit relative filename inclusion"));
		assertFalse(validateValue("<bgsound src=javascript:..."));
		assertFalse(validateValue("<body background=javascript:..."));
		assertFalse(validateValue("<body onload=..."));
		assertFalse(validateValue("<embed src=http://www.example.com/flash.swf allowScriptAccess=always"));
		assertFalse(validateValue("<embed src=\"data:image/svg+xml;"));
		assertFalse(validateValue("<frameset><frame src=\"javascript:...\"></frameset>"));
		assertFalse(validateValue("<iframe src=javascript:..."));
		assertFalse(validateValue("<img src=x onerror=..."));
		assertFalse(validateValue("<input type=image src=javascript:..."));
		assertFalse(validateValue("<layer src=..."));
		assertFalse(validateValue("<link href=\"javascript:...\" rel=\"stylesheet\" type=\"text/css\""));
		assertFalse(validateValue("<link href=\"http://www.example.com/xss.css\" rel=\"stylesheet\" type=\"text/css\""));
		assertFalse(validateValue("<meta http-equiv=\"refresh\" content=\"0;url=javascript:...\""));
		assertFalse(validateValue("<meta http-equiv=\"refresh\" content=\"0;url=http://;javascript:...\" // evasion"));
		assertFalse(validateValue("<meta http-equiv=\"link\" rel=stylesheet content=\"http://www.example.com/xss.css\">"));
		assertFalse(validateValue("<meta http-equiv=\"Set-Cookie\" content=\"NEW_COOKIE_VALUE\">"));
		assertFalse(validateValue("<object data=http://www.example.com"));
		assertFalse(validateValue("<object type=text/x-scriptlet data=..."));
		assertFalse(validateValue("<object type=application/x-shockwave-flash data=xss.swf>"));
		assertFalse(validateValue("<object classid=clsid:ae24fdae-03c6-11d1-8b76-0080c744f389><param name=url value=javascript:...></object> // not verified"));
		assertFalse(validateValue("<script>...</script>"));
		assertFalse(validateValue("<script src=http://www.example.com/xss.js></script> - TODO add another rule for this"));
		assertFalse(validateValue("<script src=\"data:text/javascript,alert(1)\"></script>"));
		assertFalse(validateValue("<script src=\"data:text/javascript;base64,PHNjcmlwdD5hbGVydChkb2N1bWVudC5jb29raWUpOzwvc2NyaXB0Pg==\"></script>"));
		assertFalse(validateValue("<style>STYLE</style>"));
		assertFalse(validateValue("<style type=text/css>STYLE</style>"));
		assertFalse(validateValue("<style type=text/javascript>alert('xss')</style>"));
		assertFalse(validateValue("<table background=javascript:..."));
		assertFalse(validateValue("<td background=javascript:"));

		// Detect event handler names
		assertFalse(validateValue("<body onload=...>"));
		assertFalse(validateValue("<img src=x onerror=...>"));

		// Detect URI attributes
		assertFalse(validateValue("<a href=\"javascript:...\">Link</a>"));
		assertFalse(validateValue("<base href=\"javascript:...\">"));
		assertFalse(validateValue("<bgsound src=\"javascript:...\">"));
		assertFalse(validateValue("<body background=\"javascript:...\">"));
		assertFalse(validateValue("<frameset><frame src=\"javascript:...\"></frameset>"));
		assertFalse(validateValue("<iframe src=javascript:...>"));
		assertFalse(validateValue("<img dynsrc=javascript:...>"));
		assertFalse(validateValue("<img lowsrc=javascript:...>"));
		assertFalse(validateValue("<img src=javascript:...>"));
		assertFalse(validateValue("<input type=image src=javascript:...>"));

		assertFalse(validateValue("<meta http-equiv=\"refresh\" content=\"0;url=data:text/html;base64,PHNjcmlwdD5hbGVydCgnWFNTJyk8L3NjcmlwdD4K\">"));
		assertFalse(validateValue("<img src=jaVaScrIpt:...>"));
		assertFalse(validateValue("<img src=&#6a;avascript:...> (not evasion)"));
		assertFalse(validateValue("<img src=\"jav	ascript:...\"> (embedded tab; null byte, other whitespace characters work too)"));
		assertFalse(validateValue("<img src=\"jaa&#09;ascript:...\"> (the combination of the above two)"));

		assertFalse(validateValue("<div style=\"background-image: url(javascript:...)\">"));

		// JavaScript fragments
		assertFalse(validateValue("alert(String.fromCharCode(88,83,83)"));
		assertFalse(validateValue("document.cookie"));
		assertFalse(validateValue("document.styleSheets[0].addImport('yourstylesheet.css', 2);"));
		assertFalse(validateValue("window.execScript(\"alert('test');\", \"JavaScript\");"));
		assertFalse(validateValue("document.body.innerHTML = ''"));
		assertFalse(validateValue("newObj = new ActiveXObject(servername.typename[, location])"));
		assertFalse(validateValue("setTimeout(\"alert('xss')\", 1000)"));
		assertFalse(validateValue("xmlHttp.onreadystatechange=function() {}"));
		assertFalse(validateValue("eval(location.hash.substr(1)) // used to execute JavaScript in fragment identifier"));

		// CSS attack fragments
		assertFalse(validateValue("<div style=\"background-image: url(javascript:...)\">"));
		assertFalse(validateValue("<div style=\"background-image: url(&#1;javascript:alert('XSS'))\"> // not used"));
		assertFalse(validateValue("<div style=\"width: expression(...);\">"));
		assertFalse(validateValue("<img style=\"x:expression(document.write(1))\">"));
		assertFalse(validateValue("<xss style=\"behavior: url(http://ha.ckers.org/xss.htc);\">"));
		assertFalse(validateValue("<style>li {list-style-image: url(\"javascript:alert('XSS')\");}</style><ul><li>xss"));
		assertFalse(validateValue("<style>@import url(...);</style>"));
		assertFalse(validateValue("-moz-binding:url(...)"));
		assertFalse(validateValue("background:url(\"javascript:...\")"));
		assertFalse(validateValue("</xss/*-*/style=xss:e/**/xpression(alert(1337))> (comment evasion) // TODO Verify"));
		assertFalse(validateValue("<style type=\"text/css\">@i\\m\\p\\o\\rt url(...);</style> (css escaping evasion)"));
		assertFalse(validateValue("<li style=\"behavior:url(hilite.htc)\">xss"));

	}

	public void testCaseInsensitive() {

		assertFalse(validateValue("<A "));
		assertFalse(validateValue("select/*avoid-spaces*/password/**/FROM/**/Members"));
		assertFalse(validateValue("<FRAMESET><frame src=\"javascript:...\"></FRAMESET>"));
	}

}
