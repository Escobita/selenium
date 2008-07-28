﻿<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" >
<head>
    <title><decorator:title/></title>
    <link href="openqa.css" rel="stylesheet" type="text/css" />
    <link rel="shortcut icon" href="openqa-favicon.ico" type="image/vnd.microsoft.icon" />
    <link rel="icon" href="openqa-favicon.ico" type="image/vnd.microsoft.icon" /> 
    <script src="page-tools.js" type="text/javascript"></script>
    <decorator:head/>
</head>
<body class="homepage">
<div id="container">
    <div id="header">
        <h1><a href="../selenium.openqa.org/index.html" title="Return to Selenium home page">Selenium</a></h1>
        <ul>
            <li><a href="index.html#">About</a></li>
            <li><a href="support.html#">Support</a></li>
            <li><a href="index.html#">Documentation</a></li>
            <li><a href="downloads.html#">Download</a></li>
            <li><a href="projects.html">Projects</a></li>
        </ul>
        <form id="searchbox_016909259827549404702:hzru01fldsm" action="http://www.google.com/cse" title="openqa.org Selenium Search">
            <div>
                <label for="q" title="Search openqa.org's sites for selenium content">search selenium:</label>
                <input type="hidden" name="cx" value="016909259827549404702:hzru01fldsm">
                <input type="hidden" name="cof" value="FORID:">
                <input type="text" id="q" name="q" accesskey="s" size="30">
                <input type="submit" id="submit" value="Go">
            </div>
        </form>

    </div>
    <div id="userStatus">Welcome, <span id="username">Guest</span>. <a id="login" href="http://www.openqa.org/sass/index.action">Login</a> or <a id="create_account" href="http://www.openqa.org/sass/index.action">Create an account</a>.</div>
    <div id="mBody">
        <div id="sidebar">
            <img alt="Selenium Logo" src="images/big-logo.png" />
            <p>
                <strong>Selenium is a suite of tools</strong> to automate web app testing across many platforms.
            </p>
            <p>Selenium...</p>
            <ul>
                <li>runs in <a href="many-browsers.html">many browsers</a> and <a href="operating-systems.html">operating systems</a></li>
                <li>can be controlled by many <a href="programming-languages.html#">programming languages</a> and <a href="testing-frameworks.html">testing frameworks</a>.</li>
            </ul>
            <div class="downloadBox"><a href="downloads.html">Download Selenium</a>1.0 beta</div>
            <div class="ads">
				<script type="text/javascript"><!--
				google_ad_client = "pub-6291771388053870";
				google_ad_width = 190;
				google_ad_height = 370;
				google_ad_format = "190x370_as";
				google_ad_type = "text_image";
				google_ad_channel ="";
				google_color_border = "E9E9E9";
				google_color_bg = "E9E9E9";
				google_color_link = "000000";
				google_color_url = "333333";
				google_color_text = "000000";
				//--></script>
				<script type="text/javascript"
				  src="http://pagead2.googlesyndication.com/pagead/show_ads.js">
				</script>
	        </div>
        </div>
        <div id="mainContent">
	      <decorator:body/>
        </div>
    </div>
    <div id="footer">
        <ul id="sitemap">
            <li><a href="index.html#">Selenium Projects</a>
                <ul><li><a href="http://selenium-core.openqa.org">Selenium Core</a></li>
                    <li><a href="http://selenium-ide.openqa.org">Selenium IDE</a></li>
                    <li><a href="http://selenium-rc.openqa.org">Selenium Remote Control</a></li>
                    <li><a href="http://selenium-grid.openqa.org">Selenium Grid</a></li>
                    <li><a href="http://selenium-on-rails.openqa.org">Selenium on Rails</a></li>
                    <li><a href="http://cubictest.openqa.org">CubicTest (for Eclipse)</a></li>
                    <li class="different"><a href="more-related-projects.html#">Other related projects</a></li>
                </ul></li>
            <li><a href="index.html#">Documentation</a>
                <ul><li><a href="examples.html">Examples</a></li>
                    <li><a href="screencasts.html">Screencasts</a></li>
                    <li><a href="index.html#">Tutorials/Labs</a></li>
                    <li><a href="http://wiki.openqa.org/display/SEL/Home">Wiki</a></li>
                    <li><a href="http://selenium-core.openqa.org/reference.html">Selenium API</a></li>
                </ul></li>
            <li><a href="index.html#">Support</a>
                <ul><li><a href="http://clearspace.openqa.org/index.jspa">Forums</a></li>
                    <li><a href="http://jira.openqa.org/">Bug Tracker</a></li>
                    <li><a href="jobs-board.html">Jobs Board</a></li>
                    <li><a href="commercial-suppprt.html">Commercial Support</a></li>
                </ul></li>
            <li><a href="index.html#">About Selenium</a>
                <ul><li><a href="who-made-selenium.html">Who made Selenium</a>
	                <li><a href="index.html#">How it Works</a></li>
                    <li><a href="news.html">News/Blogs</a></li>
                    <li><a href="supported-platforms.html">Supported Platforms</a></li>
                    <li><a href="roadmap.html">Roadmap</a></li>
                    <li><a href="getting-involved.html">Getting Involved</a></li>
                </ul></li>
            <li><a href="../www.openqa.org/index.html">OpenQA.org</a>
                <ul><li><a href="http://www.openqa.org/sass/index.action">Create an account</a></li>
                    <li><a href="index.html#">Account management</a></li>
                    <li><a href="index.html#">Sponsors</a></li>
                </ul></li>
        </ul>
        <a href="../www.openqa.org/index.html"><img alt="openqa.org logo" id="footerLogo" src="images/openqa-logo.png" /></a>
    </div>
</div>
</body>
</html>
