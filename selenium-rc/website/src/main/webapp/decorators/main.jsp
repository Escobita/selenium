<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>
<html>
<head>
    <title>Selenium RC: <decorator:title/></title>
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

<script type="text/javascript" src="http://www.openqa.org/shared/projects/header-start.jsp?name=selenium-rc"></script>
<script type="text/javascript"
  src="http://pagead2.googlesyndication.com/pagead/show_ads.js">
</script>
<script type="text/javascript" src="http://www.openqa.org/shared/projects/header-end.jsp?name=selenium-rc"></script>

<h1 class="first">Selenium RC: <decorator:title/></h1>

<decorator:body/>
</div>
<div id="menu">
	<div class="menuGroup">
        <h1>Selenium Remote Control</h1>
        <ul>
            <li><a href="/">About</a></li>
            <li><a href="/news.jsp">News</a></li>
            <li><a href="/changelog.jsp">Changelog</a></li>
        </ul>
    </div>
    <div class="menuGroup">
        <h1>Evaluating Selenium Remote Control</h1>
        <ul>
            <li><a href="/license.jsp">License</a></li>
            <li><a href="/download.jsp">Download</a></li>
            <li><a href="/release-notes.html">Release Notes</a></li>
        </ul>
    </div>
    <div class="menuGroup">
        <h1>Using Selenium Remote Control</h1>
        <ul>
            <li><a href="/documentation.jsp">Documentation</a></li>
            <li><a href="/tutorial.html">Tutorial</a></li>
            <li><a href="http://wiki.openqa.org/display/SRC/Selenium+RC+FAQ">FAQ</a></li>
            <li><a href="/experimental.html">Experimental</a></li>
            <li><a href="/java.html">- Java</a></li>
            <li><a href="/dotnet.html">- .NET</a></li>
            <li><a href="/perl.html">- Perl</a></li>
            <li><a href="/php.html">- PHP</a></li>
            <li><a href="/python.html">- Python</a></li>
            <li><a href="/ruby.html">- ruby</a></li>
            <li><a href="/selenese.html">- Selenese</a></li>
            <li><a href="/js.html">- JavaScript</a></li>
            <li><a href="/options.html">Server Command Line Options</a></li>
            <li><a href="http://wiki.openqa.org/display/SRC/Developer%27s+Guide">Developers Guide</a></li>
			<li><a href="http://wiki.openqa.org/display/SRC/Home">Wiki</a></li>
            <li><a href="/user-forums.html">User Forums</a></li>
            <li><a href="/xpath-help.html">Help with XPath</a></li>
            <li><a href="/self-help.html">Self Help</a></li>
            <li><a href="/reporting.jsp">Reporting Issues</a></li>
            <li><a href="/rss.jsp">RSS Feeds</a></li>
        </ul>
    </div>
    <div class="menuGroup">
        <h1>Developing Selenium Remote Control</h1>
        <ul>
            <li><a href="/how-it-works.html">How it Works</a></li>
            <li><a href="/contribute.jsp">How to Contribute</a></li>
            <li><a href="/dev-forums.html">Developer Forums</a></li>
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
<script type="text/javascript" src="http://www.openqa.org/shared/projects/footer.jsp?name=selenium-rc"></script>

</body>
</html>
