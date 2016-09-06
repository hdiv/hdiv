package org.hdiv.idGenerator;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class RandomGuidTest {

	@Test
	public void testRandom() {

		Set<String> users = new HashSet<String>();
		int retries = 3;
		long time = System.currentTimeMillis();
		for (long i = 0; i < 4294967296L; i++) {
			if (i % 1000000 == 0) {
				System.out.println(i + " " + (System.currentTimeMillis() - time));
			}
			String values = RandomGuid.getRandomGuid(true).substring(0, 8);
			if (users.contains(values)) {
				retries--;
				if (retries == 0) {
					System.out.println(i);
					break;
				}
			}
			else {
				retries = 3;
				users.add(values);
			}
		}
	}

	@Test
	public void testWithIntsRandom() {
		SecureRandom random = new SecureRandom();
		Set<Integer> users = new HashSet<Integer>();
		int retries = 3;
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
				retries = 3;
				users.add(values);
			}
		}
	}

}
