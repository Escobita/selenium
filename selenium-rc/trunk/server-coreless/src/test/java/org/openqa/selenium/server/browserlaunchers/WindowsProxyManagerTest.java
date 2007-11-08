package org.openqa.selenium.server.browserlaunchers;

import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;

public class WindowsProxyManagerTest extends TestCase {
  
  public void deleteFlatDirContentsWithNoPrefix() throws IOException {
    File srcDir = makeSourceDirAndCookie("testcookie");
    WindowsProxyManager.deleteFlatDirContents(srcDir, null);
    assertTrue(srcDir.exists());
    File[] files = srcDir.listFiles();
    assertEquals(0, files.length);
    srcDir.delete();
  }
  
  public void deleteFlatDirContentsWithPrefix() throws IOException {
    File srcDir = makeSourceDirAndCookie("testcookie");
    WindowsProxyManager.deleteFlatDirContents(srcDir, "nomatch");
    assertTrue(srcDir.exists());
    File[] files = srcDir.listFiles();
    assertEquals(1, files.length);
    LauncherUtils.deleteTryTryAgain(srcDir, 1);
    assertFalse(srcDir.exists());
  }
  
  public void deleteFlatDirContentsWithNoSuchDir() {
    File tempDir = new File(System.getProperty("java.io.tmpdir"));
    File srcDir = new File(tempDir, "rc-wpmt-src");
    assertFalse(srcDir.exists());
    WindowsProxyManager.deleteFlatDirContents(srcDir, null);
    assertFalse(srcDir.exists());
  }
  
  public void testHidePreexistingCookiesNoDestDirNoPrefix() throws IOException {
    File srcDir = makeSourceDirAndCookie("testcookie");
    File destDir = getNonexistentDir();
    WindowsProxyManager.hideCookies(srcDir, null, destDir);
    assertTrue(srcDir.exists());
    assertTrue(destDir.exists());
    assertEquals(1, destDir.listFiles().length);
    assertEquals(0, srcDir.listFiles().length);
    LauncherUtils.deleteTryTryAgain(srcDir, 1);
    assertFalse(srcDir.exists());
    LauncherUtils.deleteTryTryAgain(destDir, 1);
    assertFalse(destDir.exists());
  }
  
  public void testHidePreexistingCookiesWithDestDirNoPrefix() throws IOException {
    File srcDir = makeSourceDirAndCookie("testcookie");
    File destDir = getNonexistentDir();

    assertTrue(destDir.mkdirs());
    File lostCookieFile = File.createTempFile("lostcookie", "tmp", destDir);
    lostCookieFile.deleteOnExit();
    assertTrue(lostCookieFile.exists());
    
    WindowsProxyManager.hideCookies(srcDir, null, destDir);
    
    assertTrue(srcDir.exists());
    assertTrue(destDir.exists());
    assertEquals(1, destDir.listFiles().length);
    assertEquals(0, srcDir.listFiles().length);
    
    LauncherUtils.deleteTryTryAgain(srcDir, 1);
    assertFalse(srcDir.exists());
    LauncherUtils.deleteTryTryAgain(destDir, 1);
    assertFalse(destDir.exists());
  }
  
  public void testRestorePreexistingCookiesNoPrefix() throws IOException {
    File hiddenDir = makeSourceDirAndCookie("hiddencookie");
    File cookieDir = getNonexistentDir();
    
    WindowsProxyManager.restoreCookies(cookieDir, null, hiddenDir);
    
    assertFalse(hiddenDir.exists());
    assertTrue(cookieDir.exists());
    assertEquals(1, cookieDir.listFiles().length);
    
    LauncherUtils.deleteTryTryAgain(cookieDir, 1);
    assertFalse(cookieDir.exists());
    LauncherUtils.deleteTryTryAgain(hiddenDir, 1);
    assertFalse(hiddenDir.exists());
  }
  
  public void testHidePreexistingCookiesNoDestDirWithPrefix() throws IOException {
    File srcDir = makeSourceDirAndCookie(WindowsProxyManager.VISTA_COOKIE_PREFIX);
    File destDir = getNonexistentDir();
    
    WindowsProxyManager.hideCookies(srcDir, 
        WindowsProxyManager.VISTA_COOKIE_PREFIX, destDir);
    
    assertTrue(srcDir.exists());
    assertTrue(destDir.exists());
    assertEquals(1, destDir.listFiles().length);
    
    LauncherUtils.deleteTryTryAgain(srcDir, 1);
    assertFalse(srcDir.exists());
    LauncherUtils.deleteTryTryAgain(destDir, 1);
    assertFalse(destDir.exists());
  }
  
  private File makeSourceDirAndCookie(String cookiePrefix) throws IOException {
    File tempDir = new File(System.getProperty("java.io.tmpdir"));
    File srcDir = new File(tempDir, "rc-wpmt-src");
    srcDir.deleteOnExit();
    assertTrue(srcDir.mkdir());
    File cookieFile = File.createTempFile(cookiePrefix, "tmp", srcDir);
    cookieFile.deleteOnExit();
    assertTrue(cookieFile.exists());
    return srcDir;
  }
  
  private File getNonexistentDir() {
    File tempDir = new File(System.getProperty("java.io.tmpdir"));
    File destDir = new File(tempDir, "rc-wpmt-dest");
    destDir.deleteOnExit();
    assertFalse(destDir.exists());
    return destDir;
  }
  
}
