package org.openqa.grid.e2e.misc;

import org.openqa.selenium.net.PortProber;

import org.openqa.grid.common.GridRole;
import org.openqa.grid.common.RegistrationRequest;
import org.openqa.grid.e2e.utils.GridTestHelper;
import org.openqa.grid.e2e.utils.RegistryTestHelper;
import org.openqa.grid.internal.Registry;
import org.openqa.grid.internal.utils.GridHubConfiguration;
import org.openqa.grid.internal.utils.SelfRegisteringRemote;
import org.openqa.grid.web.Hub;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * A node will try to contact the hub it's registered to every RegistrationRequest.REGISTER_CYCLE
 * millisec. If the hub crash, and is restarted, the node will register themselves again.
 * 
 * @author freynaud
 * 
 */
public class HubRestart {

  private Hub hub;
  private Registry registry;
  private SelfRegisteringRemote remote;
  private GridHubConfiguration config = new GridHubConfiguration();

  @BeforeClass(alwaysRun = false)
  public void prepare() throws Exception {

    config.setPort(PortProber.findFreePort());
    hub = GridTestHelper.getHub(config);
    registry = hub.getRegistry();

    remote = GridTestHelper.getRemoteWithoutCapabilities(hub.getUrl(), GridRole.NODE);

    remote.getConfiguration().put(RegistrationRequest.REGISTER_CYCLE, 250);

    remote.startRemoteServer();

  }

  @Test(timeOut = 5000)
  public void nodeRegisterAgain() throws Exception {

    // every 5 sec, the node register themselves again.
    Assert.assertEquals(remote.getConfiguration().get(RegistrationRequest.REGISTER_CYCLE), 250);
    remote.startRegistrationProcess();

    // should be up
    RegistryTestHelper.waitForNode(hub.getRegistry(), 1);

    // crashing the hub.
    hub.stop();

    // check that the remote do not crash if there is no hub to reply.
    Thread.sleep(1000);

    // and starting a new hub
    hub = new Hub(config);
    registry = hub.getRegistry();
    // should be empty
    Assert.assertEquals(registry.getAllProxies().size(), 0);
    hub.start();

    // the node will appear again after 250 ms.
    RegistryTestHelper.waitForNode(hub.getRegistry(), 1);

  }

  @AfterClass(alwaysRun = false)
  public void stop() throws Exception {
    hub.stop();
    remote.stopRemoteServer();

  }
}
