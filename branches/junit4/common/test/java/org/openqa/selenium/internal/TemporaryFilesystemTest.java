package org.openqa.selenium.internal;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.openqa.selenium.WebDriverException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TemporaryFilesystemTest {

  @Test public void CanCreateTempFiles() {
    File tmp = TemporaryFilesystem.createTempDir("TemporaryFilesystem", "canCreate");
    try {
      assertTrue(tmp.exists());
    } catch (WebDriverException e) {
      tmp.delete();
      throw e;
    }
  }

  @Test public void FilesystemCleanupDeletesDirs() {
    if (!TemporaryFilesystem.shouldReap()) {
      System.out.println("Reaping of files disabled - " +
                         "ignoring testFilesystemCleanupDeletesDirs");
      return;
    }
    File tmp = TemporaryFilesystem.createTempDir("TemporaryFilesystem", "fcdd");
    assertTrue(tmp.exists());

    TemporaryFilesystem.deleteTemporaryFiles();
    assertFalse(tmp.exists());
  }

  @Test public void FilesystemCleanupDeletesRecursive() throws IOException {
    if (!TemporaryFilesystem.shouldReap()) {
      System.out.println("Reaping of files disabled - " +
                         "ignoring testFilesystemCleanupDeletesRecursive");
      return;
    }
    File tmp = TemporaryFilesystem.createTempDir("TemporaryFilesystem", "fcdr");
    createDummyFilesystemContent(tmp);

    TemporaryFilesystem.deleteTemporaryFiles();
    assertFalse(tmp.exists());
  }

  @Test public void SpecificDeleteRequestHonored() throws IOException {
    if (!TemporaryFilesystem.shouldReap()) {
      System.out.println("Reaping of files disabled - " +
                         "ignoring testSpecificDeleteRequestHonored");
      return;
    }
    File tmp = TemporaryFilesystem.createTempDir("TemporaryFilesystem", "sdrh");
    createDummyFilesystemContent(tmp);

    TemporaryFilesystem.deleteTempDir(tmp);

    assertFalse(tmp.exists());
  }

  @Test public void DoesNotDeleteArbitraryFiles() throws IOException {
    File tempFile = File.createTempFile("TemporaryFilesystem", "dndaf");
    assertTrue(tempFile.exists());
    try {
      TemporaryFilesystem.deleteTempDir(tempFile);
      assertTrue(tempFile.exists());
    } finally {
      tempFile.delete();
    }
  }

  @Test public void shouldReapDefaultsTrue() {
    if (!TemporaryFilesystem.shouldReap()) {
      System.out.println("Reaping of files disabled - " +
                         "ignoring testShouldReapDefaultsTrue");
      return;
    }
    assertTrue(TemporaryFilesystem.shouldReap());
  }

  private void createDummyFilesystemContent(File dir) throws IOException {
    assertTrue(dir.isDirectory());
    File.createTempFile("cleanup", "file", dir);
    File childDir = new File(dir, "child");
    childDir.mkdir();
    File.createTempFile("cleanup", "childFile", childDir);
  }
}
