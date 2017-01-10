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
package org.hdiv.urlProcessor;

import org.hdiv.util.Method;
import org.junit.Assert;
import org.junit.Test;

public class UrlDataTest {

	@Test
	public void testShortURLIsJS() {
		UrlDataImpl data = new UrlDataImpl("short", Method.GET);
		Assert.assertFalse(data.isJS());
		data = new UrlDataImpl("javascript:", Method.GET);
		Assert.assertTrue(data.isJS());
	}

	@Test
	public void testURITemplates() {
		testPath("/mymethod/test/{?value}", "/mymethod/test/");
		testPath("/mymethod/test/filter/{?value}", "/mymethod/test/filter/");
		testPath("/mymethod/test/{myFilter}/MyPage.html", "/mymethod/test/{myFilter}/MyPage.html");
		testPath("/mymethod/test/MyPage.html", "/mymethod/test/MyPage.html");
		testPath("/mymethod/test/MyPage.html?value=3{&id}", "/mymethod/test/MyPage.html?value=3");
		testPath("/mymethod/test/MyPage.html?value=3{&id,other}", "/mymethod/test/MyPage.html?value=3");
		testPath("/mymethod/test/{id}", "/mymethod/test/{id}");
	}

	private void testPath(final String initial, final String expected) {
		UrlDataImpl data = new UrlDataImpl(initial, Method.GET);
		Assert.assertEquals(expected, data.getUrlWithOutUriTemplate());
	}

}
