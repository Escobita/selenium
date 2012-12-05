/*
Copyright 2011 Selenium committers

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package org.openqa.selenium.internal;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * Reads information about how the current application was built from the Build-Info section of the
 * manifest in the jar file, which contains this class.
 */
public class BuildInfo {

  private static final Properties BUILD_PROPERTIES = loadBuildProperties();

  private static Properties loadBuildProperties() {
    Properties properties = new Properties();

    Manifest manifest = null;
    JarFile jar = null;
    try {
      URL url = BuildInfo.class.getProtectionDomain().getCodeSource().getLocation();
      File file = new File(url.toURI());
      jar = new JarFile(file);
      manifest = jar.getManifest();
    } catch (NullPointerException ignored) {
    } catch (URISyntaxException ignored) {
    } catch (IOException ignored) {
    } catch (IllegalArgumentException ignored) {
    } finally {
      if (jar != null) {
        try {
          jar.close();
        } catch (IOException e) {
          // ignore
        }
      }
    }

    if (manifest == null) {
      return properties;
    }

    try {
      Attributes attributes = manifest.getAttributes("Build-Info");
      Set<Entry<Object, Object>> entries = attributes.entrySet();
      for (Entry<Object, Object> e : entries) {
        properties.put(String.valueOf(e.getKey()), String.valueOf(e.getValue()));
      }
    } catch (NullPointerException e) {
      // Fall through
    }

    return properties;
  }

  /** @return The embedded release label or "unknown". */
  public String getReleaseLabel() {
    return BUILD_PROPERTIES.getProperty("Selenium-Version", "unknown");
  }

  /** @return The embedded build revision or "unknown". */
  public String getBuildRevision() {
    return BUILD_PROPERTIES.getProperty("Selenium-Revision", "unknown");
  }

  /** @return The embedded build time or "unknown". */
  public String getBuildTime() {
    return BUILD_PROPERTIES.getProperty("Selenium-Build-Time", "unknown");
  }

  @Override
  public String toString() {
    return String.format("Build info: version: '%s', revision: '%s', time: '%s'",
        getReleaseLabel(), getBuildRevision(), getBuildTime());
  }
}
