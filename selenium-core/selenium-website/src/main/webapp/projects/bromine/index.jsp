<%@ page import="org.w3c.dom.Element" %>
<%@ page import="org.w3c.dom.NodeList" %>
<%@ page import="org.w3c.dom.Document" %>
<%@ page import="javax.xml.parsers.DocumentBuilderFactory" %>
<%@ page import="javax.xml.parsers.DocumentBuilder" %>
<%@ page import="java.net.URL" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.TimeZone" %>
<%@ page import="java.util.Date" %>
<html>    
    <head>        
        <title>Bromine</title>
        <link rel="stylesheet" type="text/css" href="css/lightwindow.css" />
    	<script type="text/javascript" src="javascript/prototype.js"></script>
    	<script type="text/javascript" src="javascript/effects.js"></script>
    	<script type="text/javascript" src="javascript/lightwindow.js"></script>    
    </head>    
    <body class="sectionProjects">        
        <fieldset>               
            <legend>    <b>News</b>               
            </legend>               
            <dl>                     
                <%
                    try {
                        // date format: Mon, 12 Jan 2009 22:06:48 GMT
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                        URL url = new URL("http://clearspace.openqa.org/blogs/feeds/users/Bromine");
                        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                        Document doc = db.parse(url.openStream());
                        NodeList items = doc.getElementsByTagName("item");
                        for (int i = 0; i < items.getLength() && i<3; i++) {
                            Element e = (Element) items.item(i);
                            String link = e.getElementsByTagName("link").item(0).getFirstChild().getNodeValue();
                            String title = e.getElementsByTagName("title").item(0).getFirstChild().getNodeValue();
                            String desc = e.getElementsByTagName("description").item(0).getFirstChild().getNodeValue();
                            String date = e.getElementsByTagName("pubDate").item(0).getFirstChild().getNodeValue();
            
                %>
                    <li>
                        <a href="<%= link %>"><%= title %></a> - <i><%= date%></i>
                    </li>
                <%
                        }
                    } catch (Exception e) {}
                %>              
            </dl>        
        </fieldset>        
        <br>        
        <table border="0">            
            <tr>                
                <td valign="top">
                    <a href="../../download"><img src="download.gif" style='float: left; margin-right: 6px;'></a>	
                    <p>
                        <b>Bromine</b> is a web-based QA tool for selenium, that enables you to easily run selenium RC tests and view the results. It scales beatifully from the single tester that just wants to run some tests without all the hassle to the corporate solution with multiple user-groups and houndreds of testcases. 
                    </p>
                    <p>
                        We created Bromine for 2 main reasons:
                        <ol>
                            <li>We needed an easy way to run selenium tests and store the results for analysis and backlog</li>
                            <li>We needed to present the results in a way our less technical minded management would understand</li>
                        </ol>
                    </p>
                 	<h3>Features:</h3>	                     
                    <ul>
                        <li>Supports tests in Java and PHP</li>
                        <li>Record tests with provided IDE formats</li>		                         
                        <li>Upload tests</li>		                         
                        <li>Run multiple tests against multiple OS/browser combinations with a single click</li>
                        <li>Setup OS/browser testing needs with checkboxes</li>		                         
                        <li>Uses completely unmodified RC servers</li>		                         
                        <li>No RC server hassle, just plug in the IP and you're set</li>		                         
                        <li>Seemless loadbalancing. If you have multiple RC servers defined, they will be used (not GRID, our own solution)</li>		  
                        <li>Full log of results. All commands executed and their status are stored.</li>
                        <!--li>Create custom reports with drag 'n drop elements (pie charts, tables, graphs)</li-->                       
                        <li>Define your own user groups and their rights</li>
                        <li>Coded in CakePHP, MVC design, accessible API, easily extendable.</li>
                        <li>Add plugins easily</li>		                         
                    </ul>
                    <h3>Links:</h3>	                     
                    <ul>
                        <li><a href="http://bromine.seleniumhq.org/brscreencast/brscreencast.html">Screencast</a></li>
                        <li><a href="http://wiki.openqa.org/display/BR/Home">Documentation</a></li>
                        <li><a href="http://clearspace.openqa.org/community/selenium/bromine">Forums</a></li>		                         
                        <li><a href="http://jira.openqa.org/browse/BR">Bug tracker</a></li>		                         		                         
                    </ul>
                </td>                
                <td valign="top"> 	                     
                    <img src="br3.png" style='border: 1px solid black; margin-left: 6px;'/></td>            
            </tr>        
        </table>    
    </body>
</html>