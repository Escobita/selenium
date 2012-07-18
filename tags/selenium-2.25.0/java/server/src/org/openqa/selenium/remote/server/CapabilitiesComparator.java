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

package org.openqa.selenium.remote.server;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Platform;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

import java.util.Collection;
import java.util.Comparator;

/**
 * Compares two sets of {@link Capabilities} against a desired standard. Capabilities are compared
 * by...
 * <ol>
 *   <li>{@link Capabilities#getBrowserName() browser name},
 *   <li>{@link Capabilities#getVersion() browser version},
 *   <li>{@link Capabilities#isJavascriptEnabled() whether JavaScript is enabled},
 *   <li>and {@link Capabilities#getPlatform() platform}
 * </ol>
 * For all comparisons, if the capability is missing, that particular criteria shall not factor
 * into the comparison. When comparing platforms, preference will be given to an exact platform
 * match over a fuzzy match (e.g. Platform.WINDOWS will match Platform.WINDOWS before it matches
 * Platform.XP).
 */
class CapabilitiesComparator implements Comparator<Capabilities> {

  private final Comparator<Capabilities> compareWith;

  public CapabilitiesComparator(final Capabilities desiredCapabilities) {
    final CapabilityScorer<String> browserNameScorer = CapabilityScorer.scoreAgainst(
        desiredCapabilities.getBrowserName());
    Comparator<Capabilities> byBrowserName = new Comparator<Capabilities>() {
      public int compare(Capabilities c1, Capabilities c2) {
        return browserNameScorer.score(c1.getBrowserName())
            - browserNameScorer.score(c2.getBrowserName());
      }
    };

    final CapabilityScorer<String> versionScorer = CapabilityScorer.scoreAgainst(
        desiredCapabilities.getVersion());
    Comparator<Capabilities> byVersion = new Comparator<Capabilities>() {
      public int compare(Capabilities c1, Capabilities c2) {
        return versionScorer.score(c1.getVersion())
            - versionScorer.score(c2.getVersion());
      }
    };

    final CapabilityScorer<Boolean> jsScorer = CapabilityScorer.scoreAgainst(
        desiredCapabilities.isJavascriptEnabled());
    Comparator<Capabilities> byJavaScript = new Comparator<Capabilities>() {
      public int compare(Capabilities c1, Capabilities c2) {
        return jsScorer.score(c1.isJavascriptEnabled())
            - jsScorer.score(c2.isJavascriptEnabled());
      }
    };

    Platform desiredPlatform = desiredCapabilities.getPlatform();
    if (desiredPlatform == null) {
      desiredPlatform = Platform.ANY;
    }

    final CapabilityScorer<Platform> strictPlatformScorer = CapabilityScorer.scoreAgainst(
        desiredPlatform);
    Comparator<Capabilities> byStrictPlatform = new Comparator<Capabilities>() {
      public int compare(Capabilities c1, Capabilities c2) {
        return strictPlatformScorer.score(c1.getPlatform())
            - strictPlatformScorer.score(c2.getPlatform());
      }
    };

    final CapabilityScorer<Platform> fuzzyPlatformScorer = new FuzzyPlatformScorer(
        desiredPlatform);
    Comparator<Capabilities> byFuzzyPlatform = new Comparator<Capabilities>() {
      public int compare(Capabilities c1, Capabilities c2) {
        return fuzzyPlatformScorer.score(c1.getPlatform())
            - fuzzyPlatformScorer.score(c2.getPlatform());
      }
    };

    compareWith = Ordering.compound(Lists.newArrayList(
        byBrowserName,
        byVersion,
        byJavaScript,
        byStrictPlatform,
        byFuzzyPlatform));
  }

  public static <T extends Capabilities> T getBestMatch(
      Capabilities against, Collection<T> toCompare) {
    return Ordering.from(new CapabilitiesComparator(against)).max(toCompare);
  }

  public int compare(final Capabilities a, final Capabilities b) {
    return compareWith.compare(a, b);
  }

  private static class CapabilityScorer<T> {
    final T scoreAgainst;

    public CapabilityScorer(T scoreAgainst) {
      this.scoreAgainst = scoreAgainst;
    }

    public int score(T value) {
      if (value == null) {
        return 0;
      } else if (value.equals(scoreAgainst)) {
        return 1;
      }
      return -1;
    }

    static <T> CapabilityScorer<T> scoreAgainst(T value) {
      return new CapabilityScorer<T>(value);
    }
  }

  private static class FuzzyPlatformScorer extends CapabilityScorer<Platform> {

    public FuzzyPlatformScorer(Platform scoreAgainst) {
      super(scoreAgainst);
    }

    @Override
    public int score(Platform value) {
      if (value == null) {
        value = Platform.ANY;
      }

      if (value.equals(Platform.ANY)) {
        return 0;
      }

      return value.is(scoreAgainst) || scoreAgainst.is(value) ? 1 : -1;
    }
  }
}
