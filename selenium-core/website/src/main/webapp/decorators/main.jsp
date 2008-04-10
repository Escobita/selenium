<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>
<html>
<head>
    <title>Selenium Core: <decorator:title/></title>
    <style type="text/css">
		@import url(main.css);
		@import url(wiki.css);
    </style>
    <script src="http://www.google-analytics.com/urchin.js" type="text/javascript">
    </script>
    <script type="text/javascript">
        if (urchinTracker) {
            _uacct = "UA-131794-2";
            urchinTracker();
        }
    </script>
    <script type="text/javascript">
        function onLoadFunc() {
            document.getElementById("content").style.minHeight = document.getElementById("menu").clientHeight + 'px';
        }
    </script>
    <decorator:head/>
</head>

<body onload="onLoadFunc()">

<script type="text/javascript" src="http://www.openqa.org/shared/projects/header-start.jsp?name=selenium-core"></script>
<script type="text/javascript"
  src="http://pagead2.googlesyndication.com/pagead/show_ads.js">
</script>
<script type="text/javascript" src="http://www.openqa.org/shared/projects/header-end.jsp?name=selenium-core"></script>

<h1 class="first">Selenium-Core: <decorator:title/></h1>

<decorator:body/>
</div>
<div id="menu">
	<div class="menuGroup">
        <h1>Essential</h1>
        <ul>
            <li><a href="/">About</a></li>
            <li><a href="/news.jsp">News</a></li>
            <li><a href="/changelog.jsp">Changelog</a></li>
            <li><a href="/tutorial.html">Tutorial</a></li>
            <li><a href="http://wiki.openqa.org/display/SEL/Selenium+core+FAQ">FAQ</a></li>
        </ul>
    </div>
    <div class="menuGroup">
        <h1>Evaluating</h1>
        <ul>
            <li><a href="/license.jsp">License</a></li>
            <li><a href="/download.jsp">Download</a></li>
            <li><a href="/release-notes.html">Release Notes</a></li>
            <li><a href="/compatibility.html">Compatibility</a></li>
        </ul>
    </div>
    <div class="menuGroup">
        <h1>Using It</h1>
        <ul>
            <li><a href="/installing.html">Installing</a></li>
            <li><a href="/documentation.jsp">Documentation</a></li>
            <li><a href="/usage.html">Usage</a></li>
            <li><a href="/reference.html">Reference</a></li>
            <li><a href="/demos.html">Demos</a></li>
			<li><a href="http://wiki.openqa.org/display/SEL/Home">Wiki</a></li>
            <li><a href="http://clearspace.openqa.org/community/selenium/selenium_users">User Forums</a></li>
            <li><a href="/xpath-help.html">Help with XPath</a></li>
            <li><a href="/reporting.jsp">Reporting Issues</a></li>
            <li><a href="/rss.jsp">RSS Feeds</a></li>
        </ul>
    </div>
    <div class="menuGroup">
        <h1>Improving It</h1>
        <ul>
            <li><a href="/how-it-works.html">How it Works</a></li>
            <li><a href="/contribute.jsp">How to Contribute</a></li>
            <li><a href="http://clearspace.openqa.org/community/selenium/selenium_developers">Developer Forums</a></li>
            <li><a href="/members.jsp">Development Team</a></li>
            <li><a href="/source.jsp">Source Repository</a></li>
        </ul>
    </div>
</div>
<script type="text/javascript">
google_ad_client = "pub-6291771388053870";
google_ad_width = 120;
google_ad_height = 240;
google_ad_format = "120x240_as";
google_ad_type = "text_image";
google_ad_channel ="2417238009";
google_color_border = "336699";
google_color_bg = "FFFFFF";
google_color_link = "0000FF";
google_color_url = "008000";
google_color_text = "000
</script>
<script type="text/javascript"
  src="http://pagead2.googlesyndication.com/pagead/show_ads.js">
</script>
<script type="text/javascript" src="http://www.openqa.org/shared/projects/footer.jsp?name=selenium-core"></script>
</body>
</html>
