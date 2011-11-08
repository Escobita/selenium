/*
Copyright 2007-2010 WebDriver committers
Copyright 2007-2010 Google Inc.

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

package org.openqa.selenium;

import org.openqa.selenium.environment.webserver.AppServer;

public class Pages {
  public String alertsPage;
  public String clickJacker;
  public String clicksPage;
  public String simpleTestPage;
  public String simpleXmlDocument;
  public String xhtmlTestPage;
  public String formPage;
  public String metaRedirectPage;
  public String redirectPage;
  public String javascriptEnhancedForm;
  public String javascriptPage;
  public String macbethPage;
  public String framesetPage;
  public String iframePage;
  public String dragAndDropPage;
  public String chinesePage;
  public String nestedPage;
  public String richTextPage;
  public String rectanglesPage;
  public String childPage;
  public String grandchildPage;
  public String uploadPage;
  public String svgPage;
  public String documentWrite;
  public String sleepingPage;
  public String errorsPage;
  public String dynamicPage;
  public String slowIframes;
  public String html5Page;
  public String tables;
  public String deletingFrame;
  public String draggableLists;
  public String droppableItems;
  public String bodyTypingPage;
  public String formSelectionPage;
  public String selectableItemsPage;
  public String underscorePage;
  public String ajaxyPage;
  public String mapVisibilityPage;
  public String mouseTrackerPage;
  public String dynamicallyModifiedPage;
  public String linkedImage;
  public String selectPage;
  public String touchLongContentPage;
  public String veryLargeCanvas;
  public String readOnlyPage;
  public String booleanAttributes;

  public Pages(AppServer appServer) {
    ajaxyPage = appServer.whereIs("ajaxy_page.html");
    alertsPage = appServer.whereIs("alerts.html");
    bodyTypingPage = appServer.whereIs("bodyTypingTest.html");
    childPage = appServer.whereIs("child/childPage.html");
    chinesePage = appServer.whereIs("cn-test.html");
    clickJacker = appServer.whereIs("click_jacker.html");
    clicksPage = appServer.whereIs("clicks.html");
    draggableLists = appServer.whereIs("draggableLists.html");
    dragAndDropPage = appServer.whereIs("dragAndDropTest.html");
    droppableItems = appServer.whereIs("droppableItems.html");
    deletingFrame = appServer.whereIs("deletingFrame.htm");
    documentWrite = appServer.whereIs("document_write_in_onload.html");
    dynamicPage = appServer.whereIs("dynamic.html");
    errorsPage = appServer.whereIs("errors.html");
    formPage = appServer.whereIs("formPage.html");
    formSelectionPage = appServer.whereIs("formSelectionPage.html");
    framesetPage = appServer.whereIs("frameset.html");
    grandchildPage = appServer.whereIs("child/grandchild/grandchildPage.html");
    html5Page = appServer.whereIs("html5Page.html");
    iframePage = appServer.whereIs("iframes.html");
    javascriptEnhancedForm = appServer.whereIs("javascriptEnhancedForm.html");
    javascriptPage = appServer.whereIs("javascriptPage.html");
    macbethPage = appServer.whereIs("macbeth.html");
    mapVisibilityPage = appServer.whereIs("map_visibility.html");
    metaRedirectPage = appServer.whereIs("meta-redirect.html");
    mouseTrackerPage = appServer.whereIs("mousePositionTracker.html");
    nestedPage = appServer.whereIs("nestedElements.html");
    rectanglesPage = appServer.whereIs("rectangles.html");
    redirectPage = appServer.whereIs("redirect");
    richTextPage = appServer.whereIs("rich_text.html");
    selectableItemsPage = appServer.whereIs("selectableItems.html");
    simpleTestPage = appServer.whereIs("simpleTest.html");
    simpleXmlDocument = appServer.whereIs("simple.xml");
    sleepingPage = appServer.whereIs("sleep");
    slowIframes = appServer.whereIs("slow_loading_iframes.html");
    svgPage = appServer.whereIs("svgPiechart.xhtml");
    tables = appServer.whereIs("tables.html");
    underscorePage = appServer.whereIs("underscore.html");
    uploadPage = appServer.whereIs("upload.html");
    xhtmlTestPage = appServer.whereIs("xhtmlTest.html");
    dynamicallyModifiedPage = appServer.whereIs("dynamicallyModifiedPage.html");
    linkedImage = appServer.whereIs("linked_image.html");
    selectPage = appServer.whereIs("selectPage.html");
    touchLongContentPage = appServer.whereIs("longContentPage.html");
    veryLargeCanvas = appServer.whereIs("veryLargeCanvas.html");
    readOnlyPage = appServer.whereIs("readOnlyPage.html");
    booleanAttributes = appServer.whereIs("booleanAttributes.html");
  }
}
