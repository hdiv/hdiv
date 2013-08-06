package org.hdiv.application;

import org.hdiv.AbstractHDIVTestCase;

public class IApplicationTest extends AbstractHDIVTestCase {

	private IApplication application;

	protected void onSetUp() throws Exception {

		this.application = this.getApplicationContext().getBean(IApplication.class);
	}

	public void testGetBean() throws Exception {
		String value = (String) this.application.getBean("hdivParameter");

		assertNotNull(value);
	}

}
