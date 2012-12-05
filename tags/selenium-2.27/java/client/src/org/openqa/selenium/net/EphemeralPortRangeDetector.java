package org.openqa.selenium.net;

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

/**
 * Identifies the ephemeral port range for a given environment.
 *
 * When trying to locate a "random" free port, it is important
 * to not allocate within the ephemeral range, since these
 * can be allocated at any time, and the probability of
 * race conditions increases as the number of recently used ports
 * increases, something which is quite common when running the
 * webdriver tests.
 *
 */
public interface EphemeralPortRangeDetector
{

  /**
   * Returns the first port in the ephemeral range
   * @return The first ephemeral port
   */
  public int getLowestEphemeralPort();
  /**
   * Returns the last port that could be searched for free ports
   * @return The first port that may be free
   */
  public int getHighestEphemeralPort();

}
