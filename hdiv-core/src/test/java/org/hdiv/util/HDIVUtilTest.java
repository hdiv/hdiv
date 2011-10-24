package org.hdiv.util;

import org.hdiv.AbstractHDIVTestCase;

public class HDIVUtilTest extends AbstractHDIVTestCase {

	protected void onSetUp() throws Exception {
		
	}
	
	public void testStripSession() {
		
		String url = "http://localhost:8080/hdiv-jsf/list.faces;jsessionid=AAAAAA?_HDIV_STATE_=14-2-8AB072360ABD8A2B2FBC484B0BC61BA4";
		
		String after = HDIVUtil.stripSession(url);
		
		System.out.println(after);
		
		assertTrue(after.indexOf("jsessionid")<0);
	}

}
