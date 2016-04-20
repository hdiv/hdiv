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
package org.hdiv.logs;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.LogManager;
import org.apache.log4j.spi.LoggingEvent;
import org.hdiv.AbstractHDIVTestCase;
import org.hdiv.filter.ValidatorError;

public class LoggerTest extends AbstractHDIVTestCase {

	private Logger logger;

	private final MockAppender mockAppender = new MockAppender();

	@Override
	protected void onSetUp() throws Exception {
		this.logger = super.getApplicationContext().getBean(Logger.class);

		// Add MockAppender as root Appender
		LogManager.getRootLogger().addAppender(mockAppender);
	}

	public void testLogSimple() {

		ValidatorError error = new ValidatorError("type", "target", "parameterName", "parameterValue");
		logger.log(error);

		String msg = mockAppender.getMessage();
		assertEquals("type;target;parameterName;parameterValue;;;;;", msg);
	}

	public void testLog() {

		ValidatorError error = new ValidatorError("type", "target", "parameterName", "parameterValue", "originalValue", "127.0.0.1",
				"127.0.0.1", "anonymous", "ruleName");
		logger.log(error);

		String msg = mockAppender.getMessage();
		assertEquals("type;target;parameterName;parameterValue;originalValue;127.0.0.1;127.0.0.1;anonymous;ruleName", msg);
	}

	/**
	 * Mock Appender that stores the logged message in a property
	 */
	public class MockAppender extends AppenderSkeleton {

		private String message;

		public boolean requiresLayout() {
			return false;
		}

		public void close() {
		}

		@Override
		protected void append(final LoggingEvent event) {
			this.message = event.getMessage().toString();
		}

		public String getMessage() {
			return message;
		}
	}

}