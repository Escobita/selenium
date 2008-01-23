<%!
    private String renderResult(Browser browser, OS os, String name) {
        Map<String, Map<TestConfig, Boolean>> map = TestResults.getResults();
        Map<TestConfig, Boolean> results = map.get(name);
        Boolean pass = results.get(new TestConfig(browser, os));
        if (pass == null) {
            return "<td class='not_run'></td>";
        } else if (pass) {
            return "<td class='pass'>PASS</td>";
        } else {
            return "<td class='fail'>FAIL</td>";
        }
    }
%>
<%@ page import="org.openqa.selenium.Browser" %>
<%@ page import="org.openqa.selenium.OS" %>
<%@ page import="org.openqa.selenium.TestConfig" %>
<%@ page import="org.openqa.selenium.TestResults" %>
<%@ page import="java.util.Map" %>
<html>
<head>
    <title>Automatic Test Results</title>
    <style type="text/css">
        .not_run {
            background-color: gray;
        }

        .pass {
            background-color: green;
            color: white;
        }

        .fail {
            background-color: red;
        }
    </style>
</head>

<body>


<%
    Map<String, Map<TestConfig, Boolean>> map = TestResults.getResults();
%>

<table>
    <thead>
        <tr>
            <th></th>
            <th colspan="3">WinXP</th>
            <th colspan="3">Vista</th>
            <th colspan="2">Leopard</th>
        </tr>
        <tr>
            <th></th>
            <th>IE6</th>
            <th>Safari 3</th>
            <th>Firefox 2</th>
            <th>IE7</th>
            <th>Safari 3</th>
            <th>Firefox 2</th>
            <th>Safari 3</th>
            <th>Firefox 2</th>
        </tr>
    </thead>
    <tbody>
        <%
            for (String name : map.keySet()) {
        %>
        <tr>
            <td><%= name %></td>
            <%= renderResult(Browser.IE6, OS.XP, name)%>
            <%= renderResult(Browser.SAFARI3, OS.XP, name)%>
            <%= renderResult(Browser.FIREFOX2, OS.XP, name)%>
            <%= renderResult(Browser.IE7, OS.VISTA, name)%>
            <%= renderResult(Browser.FIREFOX2, OS.VISTA, name)%>
            <%= renderResult(Browser.SAFARI3, OS.VISTA, name)%>
            <%= renderResult(Browser.SAFARI3, OS.LEOPARD, name)%>
            <%= renderResult(Browser.FIREFOX2, OS.LEOPARD, name)%>
        </tr>
        <%
            }
        %>
    </tbody>
</table>

</body>

</html>