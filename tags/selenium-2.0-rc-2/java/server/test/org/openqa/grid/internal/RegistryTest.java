package org.openqa.grid.internal;

import static org.openqa.grid.common.RegistrationRequest.APP;
import static org.openqa.grid.common.RegistrationRequest.MAX_SESSION;
import static org.openqa.grid.common.RegistrationRequest.REMOTE_URL;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.grid.common.RegistrationRequest;
import org.openqa.grid.internal.exception.CapabilityNotPresentOnTheGridException;
import org.openqa.grid.internal.listeners.RegistrationListener;
import org.openqa.grid.internal.mock.MockedNewSessionRequestHandler;
import org.openqa.grid.internal.mock.MockedRequestHandler;

public class RegistryTest {

	private static final int TOTAL_THREADS = 100;

	static RemoteProxy p1 = null;
	static RemoteProxy p2 = null;
	static RemoteProxy p3 = null;
	static RemoteProxy p4 = null;

	@BeforeClass
	public static void setup() {
		p1 = RemoteProxyFactory.getNewBasicRemoteProxy("app1", "http://machine1:4444/");
		p2 = RemoteProxyFactory.getNewBasicRemoteProxy("app1", "http://machine2:4444/");
		p3 = RemoteProxyFactory.getNewBasicRemoteProxy("app1", "http://machine3:4444/");
		p4 = RemoteProxyFactory.getNewBasicRemoteProxy("app1", "http://machine4:4444/");

	}

	@Test
	public void addProxy() {
		Registry registry = Registry.getNewInstanceForTestOnly();
		try {
			registry.add(p1);
			registry.add(p2);
			registry.add(p3);
			registry.add(p4);
			Assert.assertTrue(registry.getAllProxies().size() == 4);
		} finally {
			registry.stop();
		}
	}

	@Test
	public void addDuppedProxy() {
		Registry registry = Registry.getNewInstanceForTestOnly();
		try {
			registry.add(p1);
			registry.add(p2);
			registry.add(p3);
			registry.add(p4);
			registry.add(p4);
			Assert.assertTrue(registry.getAllProxies().size() == 4);
		} finally {
			registry.stop();
		}
	}

	static RegistrationRequest req = null;
	static Map<String, Object> app1 = new HashMap<String, Object>();
	static Map<String, Object> app2 = new HashMap<String, Object>();

	@BeforeClass
	public static void prepareReqRequest() {
		Map<String, Object> config = new HashMap<String, Object>();
		app1.put(APP, "app1");
		app2.put(APP, "app2");
		config.put(REMOTE_URL, "http://machine1:4444");
		config.put(MAX_SESSION, 5);
		req = new RegistrationRequest();
		req.addDesiredCapabilitiy(app1);
		req.setConfiguration(config);
	}

	@Test(expected = GridException.class)
	public void emptyRegistry() {
		Registry registry = Registry.getNewInstanceForTestOnly();
		try {

			MockedRequestHandler newSessionRequest = new MockedNewSessionRequestHandler(registry, app2);
			newSessionRequest.process();
		} finally {
			registry.stop();
		}

	}

	@Test(expected = CapabilityNotPresentOnTheGridException.class)
	public void CapabilityNotPresentRegistry() {
		Registry registry = Registry.getNewInstanceForTestOnly();
		try {
			registry.add(new RemoteProxy(req));

			MockedRequestHandler newSessionRequest = new MockedNewSessionRequestHandler(registry, app2);
			newSessionRequest.process();
		} finally {
			registry.stop();
		}
	}

	private int invoc = 0;

	private synchronized void increment() {
		invoc++;
	}

	@Test(timeout = 1000)
	public void registerAtTheSameTime() throws InterruptedException {
		final Registry registry = Registry.getNewInstanceForTestOnly();
		try {
			for (int i = 0; i < TOTAL_THREADS; i++) {
				new Thread(new Runnable() {

					public void run() {
						registry.add(new RemoteProxy(req));
						increment();
					}
				}).start();
			}
			while (invoc != TOTAL_THREADS) {
				Thread.sleep(250);
			}
			Assert.assertEquals(invoc, TOTAL_THREADS);
			Assert.assertEquals(registry.getAllProxies().size(), 1);
		} finally {
			registry.stop();
		}
	}

	private Random randomGenerator = new Random();

	/**
	 * try to simulate a real proxy. The proxy registration takes up to 1 sec to
	 * register, and crashes in 10% of the case.
	 * 
	 * @author Fran�ois Reynaud
	 * 
	 */
	class MyRemoteProxy extends RemoteProxy implements RegistrationListener {
		public MyRemoteProxy(RegistrationRequest request) {
			super(request);

		}

		public void beforeRegistration() {
			int registrationTime = randomGenerator.nextInt(1000);
			if (registrationTime > 900) {
				throw new NullPointerException();
			}
			try {
				Thread.sleep(registrationTime);
			} catch (InterruptedException e) {
			}
		}
	}

	private int invoc2 = 0;

	private synchronized void increment2() {
		invoc2++;
	}

	@Test(timeout = 2000)
	public void registerAtTheSameTimeWithListener() throws InterruptedException {
		final Registry registry = Registry.getNewInstanceForTestOnly();

		try {
			for (int i = 0; i < TOTAL_THREADS; i++) {
				new Thread(new Runnable() {

					public void run() {
						registry.add(new MyRemoteProxy(req));
						increment2();
					}
				}).start();
			}
			while (invoc2 != TOTAL_THREADS) {
				Thread.sleep(250);
			}
			Assert.assertEquals(invoc2, TOTAL_THREADS);
			Assert.assertEquals(registry.getAllProxies().size(), 1);
		} finally {
			registry.stop();
		}
	}

}
