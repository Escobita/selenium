package org.openqa.selenium.server;


import java.io.File;

import org.junit.Before;
import org.openqa.jetty.http.HttpContext;
import org.openqa.jetty.util.Resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class FsResourceLocatorUnitTest {
	private File tempFile;

	private FsResourceLocator resourceLocator;

	private HttpContext context;

  @Before
	public void setUp() throws Exception {
		tempFile = File.createTempFile("selenium-test-", "");
		tempFile.deleteOnExit();
		resourceLocator = new FsResourceLocator(tempFile.getParentFile());
		context = new HttpContext();
	}

	public void testShouldGetResourceFromRootDir() throws Exception {
		Resource resource = resourceLocator.getResource(context, tempFile.getName());
		assertTrue(resource.exists());
		assertNotNull(resource.getInputStream());
		assertEquals(tempFile.getAbsolutePath(), resource.getFile().getAbsolutePath());
	}

	public void testShouldReturnMissingResourceIfResourceNotFound()
			throws Exception {
		assertFalse(resourceLocator.getResource(context, "not_exists").exists());
	}

	public void testShouldReturnFilePathFromToString() throws Exception {
		Resource resource = resourceLocator.getResource(context, tempFile.getName());
		assertTrue("toString() must end with filename, because Jetty used this method to determine file type",
				resource.toString().endsWith(tempFile.getName()));
	}

	public void testHackForJsUserExtensionsLocating() throws Exception {
		File extension = new File("user-extensions.js").getAbsoluteFile();
		extension.createNewFile();
		extension.deleteOnExit();
		FsResourceLocator extensionLocator = new FsResourceLocator(extension.getParentFile());
		Resource resource = extensionLocator.getResource(context, "some/path/user-extensions.js");
		assertTrue(resource.exists());
		assertEquals(extension.getAbsolutePath(), resource.getFile().getAbsolutePath());
	}
}
