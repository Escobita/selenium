package com.thoughtworks.selenium;

public class TestSomeStuff extends SeleneseTestCase {
    public void setUp() throws Exception {
        super.setUp("http://process-dev-vm2:8080");
    }
    public void testSlider_SliderStyles()
    throws InterruptedException
{
    String idPrefix1 = "form1_";
    selenium.open("/selenium-server/tests/html/Slider.faces.htm");
    //basic vertical
    selenium.dragdrop(idPrefix1  + "slider7e_Overlay", "+0,+80");
String      valStr = selenium.getValue(idPrefix1 + "slider7e");
    assertNotNull(valStr);
    assertTrue(Double.parseDouble(valStr) > 0.0);
    Thread.sleep(1000);

    //scrollbar vertical
    selenium.dragdrop(idPrefix1 + "slider7f_Overlay", "+0,+80");
    valStr = selenium.getValue(idPrefix1 + "slider7f");
    assertNotNull(valStr);
    assertTrue(Double.parseDouble(valStr) > 0.0);
    Thread.sleep(1000);

    //volume vertical
    selenium.dragdrop(idPrefix1 + "slider7g_Overlay", "+0,+80");
    valStr = selenium.getValue(idPrefix1 + "slider7g");
    assertNotNull(valStr);
    assertTrue(Double.parseDouble(valStr) > 0.0);
    Thread.sleep(1000);

    //thermometer vertical
    selenium.dragdrop(idPrefix1 + "slider7h_Overlay", "+0,+80");
    valStr = selenium.getValue(idPrefix1 + "slider7h");
    assertNotNull(valStr);
    assertTrue(Double.parseDouble(valStr) > 0.0);
    Thread.sleep(1000);
}

    public void xtestIdeaCreate() throws Throwable {
        selenium.setContext("Test idea create", "error");
        selenium.open("http://process-dev-vm2:8080/portal/server.pt");
        selenium.type("in_tx_username", "hw_user");
        selenium.click("in_bu_Login");
        selenium.waitForPageToLoad("30000");
        selenium.select("xpath=//select[@id='in_se_commnav']", "Process Community");  
//        selenium.open("http://process-dev-vm2:8080/portal/server.pt?space=CommunityPage&cached=true&parentname=MyPage&parentid=1&in_hi_userid=251&control=SetCommunity&CommunityID=205&PageID=0");
        selenium.click("link=Create Idea");
        selenium.waitForPopUp("main", "30000");
        selenium.type("field0", "here is an idea");
        selenium.click("Ok");
        selenium.waitForPageToLoad("30000");
        System.out.println("ok?");
    }
    public void xtestTextArea() throws Throwable {
        selenium.setContext("Test text area", "error");
        selenium.open("/selenium-server/tests/html/test_verifications.html");
        String s = selenium.getText("theTextarea");
        System.out.println("selenium.getElementHeight=" + selenium.getElementHeight("theTextarea"));
        System.out.println("selenium.getElementWidth=" + selenium.getElementWidth("theTextarea"));
        System.out.println("selenium.getElementPositionLeft=" + selenium.getElementPositionLeft("theTextarea"));
        System.out.println("selenium.getElementPositionTop=" + selenium.getElementPositionTop("theTextarea"));
        boolean b = "Line 1\nLine 2".equals(s)  // IE
        || "Line 1 Line 2".equals(s);           // firefox
        assertTrue("text area", b);
        System.out.println(s);
    }
    public void xtestTypeHang() throws Throwable{
        selenium.open("http://www.google.co.uk");
        selenium.waitForPageToLoad("50000");
        
        selenium.type("q", "rabbits");
        selenium.click("btnG");
        selenium.waitForPageToLoad("50000");
        
        selenium.type("q", "selenium2");
        selenium.click("btnG");
        selenium.waitForPageToLoad("50000");
        
        selenium.type("q", "selenium");
        selenium.click("btnG");
        selenium.waitForPageToLoad("50000");
    }    
    public void xtestCallxtestTypeHang() throws Throwable {
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        /*
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();
        xtestTypeHang();*/
    }
}
