package org.openqa.selenium.ie;

import java.util.List;

import junit.framework.TestCase;

import org.openqa.selenium.By;
import org.openqa.selenium.RenderedWebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class InternetExplorerTest extends TestCase {
	public void testCrasher() {
//		WebDriver driver = new InternetExplorerDriver();
//		driver.get("http://www.nzz.ch/");
//		System.out.println(driver.getTitle());
//		System.out.println(driver.findElement(By.id("header")).getText());
//		driver.quit();
	}
	
	public void testGoogleExample() {
		WebDriver driver = new InternetExplorerDriver();
		driver.get("http://www.google.com");
		WebElement element = driver.findElement(By.name("q"));
		element.sendKeys("Cheese");
		element.submit();
		
		assertTrue(driver.getTitle().contains("Cheese"));
		
		List<WebElement> allLinks = driver.findElements(By.xpath("//a"));
		WebElement clicky = null;
		for (WebElement link : allLinks) {
			if (clicky == null && ((RenderedWebElement) link).isDisplayed())
				clicky = link;
		}
		
		clicky.click();
	}
}
