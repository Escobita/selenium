package org.openqa.grid.web.servlet.beta;

import java.util.Map;

import org.openqa.grid.common.SeleniumProtocol;
import org.openqa.grid.common.exception.GridException;
import org.openqa.grid.internal.RemoteProxy;
import org.openqa.grid.internal.TestSession;
import org.openqa.grid.internal.TestSlot;
import org.openqa.grid.internal.utils.HtmlRenderer;
import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.CapabilityType;

public class WebProxyHtmlRendererBeta implements HtmlRenderer {

  private RemoteProxy proxy;

  @SuppressWarnings("unused")
  private WebProxyHtmlRendererBeta() {}

  public WebProxyHtmlRendererBeta(RemoteProxy proxy) {
    this.proxy = proxy;
  }



  public String renderSummary() {
    StringBuilder builder = new StringBuilder();
    builder.append("<div class='proxy'>");
    builder.append("<p class='proxyname'>");
    builder.append(proxy.getClass().getSimpleName());

    // TODO freynaud
    builder.append(" //TODO:node version");

    String platform = getPlatform(proxy);

    builder.append("<p class='proxyid'>id : ");
    builder.append(proxy.getId());
    builder.append(", OS : " + platform + "</p>");

    builder.append(nodeTabs());

    builder.append("<div class='content'>");

    builder.append(tabBrowsers());
    builder.append(tabConfig());

    builder.append("</div>");
    builder.append("</div>");

    return builder.toString();

  }

  // content of the config tab.
  private String tabConfig() {
    StringBuilder builder = new StringBuilder();
    builder.append("<div type='config' class='content_detail'>");
    Map<String, Object> config = proxy.getConfig();

    for (String key : config.keySet()) {
      builder.append("<p>");
      builder.append(key);
      builder.append(":");
      builder.append(config.get(key));
      builder.append("</p>");
    }

    builder.append("</div>");
    return builder.toString();
  }


  // content of the browsers tab
  private String tabBrowsers() {
    StringBuilder builder = new StringBuilder();
    builder.append("<div type='browsers' class='content_detail'>");

    SlotsLines rcLines = new SlotsLines();
    SlotsLines wdLines = new SlotsLines();

    for (TestSlot slot : proxy.getTestSlots()) {
      if (slot.getProtocol() == SeleniumProtocol.Selenium) {
        rcLines.add(slot);
      } else {
        wdLines.add(slot);
      }
    }

    if (rcLines.getLinesType().size() != 0) {
      builder.append("<p class='protocol' >Remote Control (legacy)</p>");
      builder.append(getLines(rcLines));
    }
    if (wdLines.getLinesType().size() != 0) {
      builder.append("<p class='protocol' >WebDriver</p>");
      builder.append(getLines(wdLines));
    }
    builder.append("</div>");
    return builder.toString();
  }

  // the lines of icon representing the possible slots
  private String getLines(SlotsLines lines) {
    StringBuilder builder = new StringBuilder();
    for (MiniCapability cap : lines.getLinesType()) {
      String icon = cap.getIcon();
      String version = cap.getVersion();
      builder.append("<p>");
      if (version != null) {
        builder.append("v:" + version);
      }
      for (TestSlot s : lines.getLine(cap)) {
        getSingleSlotHtml(s, icon);
      }
      builder.append("</p>");
    }
    return builder.toString();
  }

  // icon ( or generic html if icon not available )
  private String getSingleSlotHtml(TestSlot s, String icon) {
    StringBuilder builder = new StringBuilder();
    TestSession session = s.getSession();
    if (icon != null) {
      builder.append("<img ");
      builder.append("src='").append(icon).append("' ");
    } else {
      builder.append("<a href='#' ");
    }

    if (session != null) {
      builder.append(" class='busy' ");
      builder.append(" title='").append(session.get("lastCommand")).append("' ");
    } else {
      builder.append(" title='").append(s.getCapabilities()).append("'");
    }

    if (icon != null) {
      builder.append(" />\n");
    } else {
      builder.append(">");
      builder.append(s.getCapabilities().get(CapabilityType.BROWSER_NAME));
      builder.append("</a>");
    }
    return builder.toString();
  }

  // the tabs header.
  private String nodeTabs() {
    StringBuilder builder = new StringBuilder();
    builder.append("<div class='tabs'>");
    builder.append("<ul>");
    builder
        .append("<li class='tab' type='browsers'><a title='test slots' href='#'>Browsers</a></li>");
    builder
        .append("<li class='tab' type='config'><a title='node configuration' href='#'>Configuration</a></li>");
    builder.append("</ul>");
    builder.append("</div>");
    return builder.toString();
  }


  /**
   * return the platform for the proxy. It should be the same for all slots of the proxy, so checking that.
   * @return
   */
  public static String getPlatform(RemoteProxy proxy) {
    Platform res = null;
    if (proxy.getTestSlots().size() == 0) {
      return "Unknown";
    } else {
      res = getPlatform(proxy.getTestSlots().get(0));

    }

    for (TestSlot slot : proxy.getTestSlots()) {
      Platform tmp = getPlatform(slot);
      if (tmp != res) {
        return "mixed OS";
      } else {
        res = tmp;
      }
    }
    if (res == null) {
      return "not specified";
    } else {
      return res.toString();
    }
  }

  private static Platform getPlatform(TestSlot slot) {
    Object o = slot.getCapabilities().get(CapabilityType.PLATFORM);
    if (o == null) {
      return Platform.ANY;
    } else {
      if (o instanceof String) {
        return Platform.valueOf((String) o);
      } else if (o instanceof Platform) {
        return (Platform) o;
      } else {
        throw new GridException("Cannot cast " + o + " to Paltform");
      }
    }
  }


}
