<%@ page import="org.openqa.selenium.OS" %>
<%@ page import="org.openqa.selenium.Browser" %>
<%@ page import="org.openqa.selenium.TestResults" %>
<%@ page import="org.openqa.selenium.TestConfig" %><%
    String name = request.getParameter("name");
    OS os = OS.valueOf(request.getParameter("os"));
    Browser browser = Browser.valueOf(request.getParameter("browser"));
    boolean pass = Boolean.parseBoolean(request.getParameter("pass"));

    TestResults.putResult(name, pass, new TestConfig(browser, os));;
%>
OK