package org.openqa.selenium.safari;

/**
 * Created by IntelliJ IDEA.
 * User: kurniady
 * Date: 1/12/11
 * Time: 5:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class Experimenter {

  public static void main(String[] args) throws Exception {
    SafariDriver driver = new SafariDriver();

    driver.get("http://www/~kurniady/script_test.html");

    /*
    for (int i=0;i<10;i++) {
      driver.get("http://www.google.com");
      Thread.sleep(1000);
      System.err.println("Current URL is : " + driver.getCurrentUrl());
      System.err.println("Current title is : " + driver.getTitle());
      Thread.sleep(1000);
      driver.get("http://code.google.com");
      Thread.sleep(1000);
      System.err.println("Current URL is : " + driver.getCurrentUrl());
      System.err.println("Current title is : " + driver.getTitle());
      Thread.sleep(1000);
    }
*/

    // TODO(kurniady): fix this
    Thread.sleep(3000);

    driver.executeScript("window.console.log('hello!');");

    System.err.println("Done, idle sleep...");

    Thread.sleep(600000);

    driver.quit();
  }
}
