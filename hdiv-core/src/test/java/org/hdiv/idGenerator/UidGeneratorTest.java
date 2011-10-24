package org.hdiv.idGenerator;

//import java.util.Random;
//import java.util.UUID;

import junit.framework.TestCase;

public class UidGeneratorTest extends TestCase{

	public void testUidGenerationPerformance() {

//Only in Java 1.5
//		{
//			UUID id = UUID.randomUUID();
//			String sId = id.toString();
//			
//			UidGenerator iudg = new RandomGuidUidGenerator();
//			String gid = iudg.generateUid().toString();
//			
//			Random random = new Random(System.nanoTime() + this.hashCode());
//			String rId = Long.valueOf(random.nextLong()).toString();
//		}
//		
//		long time1 = System.nanoTime();
//		
//		UUID id = UUID.randomUUID();
//		String sId = id.toString();
//		
//		long time2 = System.nanoTime();
//		
//		UidGenerator iudg = new RandomGuidUidGenerator();
//		String gid = iudg.generateUid().toString();
//		
//		long time3 = System.nanoTime();
//		
//		Random random = new Random(System.nanoTime() + this.hashCode());
//		String rId = Long.valueOf(random.nextLong()).toString();
//		
//		long time4 = System.nanoTime();
//		
//		System.out.println(sId);
//		System.out.println(gid);
//		System.out.println(rId);
//		
//		System.out.println(time2-time1);
//		System.out.println(time3-time2);
//		System.out.println(time4-time3);
	}
}
