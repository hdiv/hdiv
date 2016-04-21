package org.hdiv.urlProcessor;

import org.hdiv.util.Method;
import org.junit.Assert;
import org.junit.Test;

public class UrlDataTest {

	@Test
	public void testShortURLIsJS() {
		UrlData data = new UrlData("short", Method.GET);
		Assert.assertFalse(data.isJS());
		data = new UrlData("javascript:", Method.GET);
		Assert.assertTrue(data.isJS());
	}

}
