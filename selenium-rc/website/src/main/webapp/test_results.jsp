<%!
    private String renderResult(Browser browser, OS os, String name) {
        Map<String, Map<TestConfig, Boolean>> map = TestResults.getResults();
        Map<TestConfig, Boolean> results = map.get(name);

        Boolean pass = null;
        if (os == null) {
            // OS not provided, so just grab the first non-null result (if one can be found)
            for (OS selection : OS.values()) {
                pass = results.get(new TestConfig(browser, selection));
                if (pass != null) {
                    break;
                }
            }
        } else {
            pass = results.get(new TestConfig(browser, os));
        }

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
        <tr valign="bottom">
            <th></th>
            <th>XP<br/>IE6</th>
            <th>Vista<br/>IE7</th>
            <th>OSX10.5<br/>SAF3</th>
            <th>FF2</th>
            <th>FF2C</th>
            <th>OP92</th>
        </tr>
    </thead>
    <tbody>
        <%
            for (String name : map.keySet()) {
        %>
        <tr>
            <td><%= name %></td>

            <%= renderResult(Browser.IE6, OS.XP, name)%>
            <%= renderResult(Browser.IE7, OS.VISTA, name)%>
            <%= renderResult(Browser.SAFARI3, OS.LEOPARD, name)%>
            <%= renderResult(Browser.FIREFOX2, null, name)%>
            <%= renderResult(Browser.FIREFOX2CHROME, null, name)%>
            <%= renderResult(Browser.OPERA92, OS.XP, name)%>
        </tr>
        <%
            }
        %>
    </tbody>
</table>

</body>

</html>