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

		return config
				.areEditableParameterValuesValid("/editableTest.html", "paramName", new String[] { value }, "text");
	}

	public void testSqlInjection() {

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
	}

}
