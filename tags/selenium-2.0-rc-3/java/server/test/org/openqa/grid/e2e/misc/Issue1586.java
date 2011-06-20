package org.openqa.grid.e2e.misc;

import java.net.MalformedURLException;
import java.net.URL;

import org.openqa.grid.e2e.utils.GridConfigurationMock;
import org.openqa.grid.e2e.utils.RegistryTestHelper;
import org.openqa.grid.internal.Registry;
import org.openqa.grid.selenium.SelfRegisteringRemote;
import org.openqa.grid.web.Hub;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.net.PortProber;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

// see http://code.google.com/p/selenium/issues/detail?id=1586
public class Issue1586 {

	private Hub hub = Hub.getNewInstanceForTest(PortProber.findFreePort(), Registry.getNewInstanceForTestOnly());
	private URL hubURL = hub.getUrl();

	@BeforeClass(alwaysRun = true)
	public void prepare() throws Exception {

		hub.start();
		hubURL = new URL("http://" + hub.getHost() + ":" + hub.getPort());
		SelfRegisteringRemote remote = SelfRegisteringRemote.create(GridConfigurationMock.webdriverConfig(hub.getRegistrationURL()));
		remote.addFirefoxSupport();
		remote.launchRemoteServer();
		remote.registerToHub();
		RegistryTestHelper.waitForNode(hub.getRegistry(), 1);
	}

	@Test
	public void test() throws MalformedURLException, InterruptedException {
		DesiredCapabilities ff = DesiredCapabilities.firefox();
		WebDriver driver = null;
		try {
			driver = new RemoteWebDriver(new URL(hubURL + "/grid/driver"), ff);
			for (int i = 0; i < 20; i++) {
				driver.get("http://code.google.com/p/selenium/");
				WebElement keywordInput = driver.findElement(By.name("q"));
				keywordInput.clear();
				keywordInput.sendKeys("test");
				WebElement submitButton = driver.findElement(By.name("projectsearch"));
				submitButton.click();
				driver.getCurrentUrl(); // fails here
			}
		}finally {
			if (driver !=null){
				driver.quit();
			}
		}
	}

	@AfterClass(alwaysRun = true)
	public void stop() throws Exception {
		hub.stop();
	}
}
