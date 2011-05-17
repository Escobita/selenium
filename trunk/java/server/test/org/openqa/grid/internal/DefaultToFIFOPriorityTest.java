package org.openqa.grid.internal;

import static org.openqa.grid.common.RegistrationRequest.APP;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.grid.internal.listeners.Prioritizer;
import org.openqa.grid.internal.mock.MockedNewSessionRequestHandler;
import org.openqa.grid.internal.mock.MockedRequestHandler;
import org.openqa.grid.web.servlet.handler.RequestType;

// TODO freynaud copy paste from PriorityTestLoad ....

public class DefaultToFIFOPriorityTest {

	private final static int MAX = 50;

	private static Registry registry = Registry.getNewInstanceForTestOnly();

	// priority rule : nothing defined = FIFO
	private static Prioritizer fifo = null;

	private static Map<String, Object> ff = new HashMap<String, Object>();
	private static RemoteProxy p1;
	private static List<MockedRequestHandler> requests = new ArrayList<MockedRequestHandler>();
	private static TestSession session = null;

	/**
	 * create a hub with 1 FF
	 * 
	 * @throws InterruptedException
	 */
	@BeforeClass
	public static void setup() throws InterruptedException {

		registry.setPrioritizer(fifo);
		ff.put(APP, "FF");
		p1 = RemoteProxyFactory.getNewBasicRemoteProxy(ff, "http://machine1:4444");
		registry.add(p1);

		for (int i = 1; i <= MAX; i++) {
			Map<String, Object> cap = new HashMap<String, Object>();
			cap.put(APP, "FF");
			cap.put("_priority", i);
			MockedNewSessionRequestHandler req = new MockedNewSessionRequestHandler(registry, cap);
			requests.add(req);
		}

		// use all the spots ( so 1 ) of the grid so that a queue buils up
		MockedRequestHandler newSessionRequest = new MockedRequestHandler(registry);
		newSessionRequest.setRequestType(RequestType.START_SESSION);
		newSessionRequest.setDesiredCapabilities(ff);
		newSessionRequest.process();
		session = newSessionRequest.getTestSession();

		// fill the queue with MAX requests.
		for (MockedRequestHandler h : requests) {
			final MockedRequestHandler req = h;
			new Thread(new Runnable() {
				public void run() {
					req.process();
				}
			}).start();
		}
		
		System.out.println(">" + requests);

		// free the grid : the queue is consumed, and the test with the highest
		// priority should be processed.
		while (requests.size() != MAX) {
			Thread.sleep(250);
		}
		session.terminateSyncronousFOR_TEST_ONLY();
	}

	// validate that the one with priority 5 has been assigned a proxy
	@Test
	public void validateRequestAreHandledFIFO() throws InterruptedException {
		System.out.println(">" + requests);
		Thread.sleep(1000);
		Assert.assertNotNull(requests.get(0).getTestSession());
		Assert.assertEquals(1, requests.get(0).getDesiredCapabilities().get("_priority"));
	}

	@AfterClass
	public static void teardown() {
		registry.stop();
	}

}
