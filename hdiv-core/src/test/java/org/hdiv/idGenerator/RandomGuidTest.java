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
package org.hdiv.idGenerator;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class RandomGuidTest {

	@Test
	public void testRandom() {
		Map<Character, Set<String>> users = new HashMap<Character, Set<String>>();

		int numRetries = 3;
		int retries = numRetries;
		long time = System.currentTimeMillis();
		for (long i = 0; i < 4294967296L; i++) {
			if (i % 1000000 == 0) {
				System.out.println(i + " " + (System.currentTimeMillis() - time));
			}
			String values = FastUUID.get().substring(0, 8);
			Character c = values.charAt(0);
			if (!users.containsKey(c)) {
				users.put(c, new HashSet<String>());
			}
			Set<String> current = users.get(c);
			if (current.contains(values)) {
				retries--;
				if (retries <= 0) {
					System.out.println(i);
					break;
				}
			}
			else {
				retries = numRetries;
				current.add(values);
			}
		}
	}

	@Test
	public void testPerformance() {
		long time = System.currentTimeMillis();
		int total = 10000000;

		for (int i = 0; i < total; i++) {
			FastUUID.get();
		}
		System.out.println(System.currentTimeMillis() - time);
		time = System.currentTimeMillis();
		for (int i = 0; i < total; i++) {
			RandomGuid.getRandomGuid(false);
		}
		System.out.println(System.currentTimeMillis() - time);
	}

	@Test
	public void testWithIntsRandom() {
		SecureRandom random = new SecureRandom();
		Set<Integer> users = new HashSet<Integer>();
		int retries = 1;
		long time = System.currentTimeMillis();
		for (long i = 0; i < 4294967296L; i++) {
			if (i % 1000000 == 0) {
				System.out.println(i + " " + (System.currentTimeMillis() - time));
			}
			Integer values = random.nextInt();
			if (users.contains(values)) {
				retries--;
				if (retries == 0) {
					System.out.println(i);
					break;
				}
			}
			else {
				retries = 1;
				users.add(values);
			}
		}
	}

	@Test
	public void testUUIDRandom() {
		UUID id = UUID.randomUUID();
		System.out.println(id);
	}

}
