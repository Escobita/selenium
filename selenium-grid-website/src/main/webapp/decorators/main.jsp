<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>
<html>
<head>
  <title><decorator:title/></title>
  <style type="text/css">
      @import "http://www.openqa.org/shared/css/wiki.css";
  </style>
  <script src="http://www.google-analytics.com/urchin.js" type="text/javascript">
  </script>
  <script type="text/javascript">
      if (urchinTracker) {
          _uacct = "UA-131794-2";
          urchinTracker();
      }
  </script>
  
  <decorator:head/>
  
  <style type="text/css">
      @import "http://selenium-grid.openqa.org/stylesheets/openqa.css";
  </style>
</head>

<body>

  <div id="content">
    <decorator:body/>
  </div>

  <div id="footer">
    <div id="bottom-ads">
      <script type="text/javascript">
        google_ad_client = "pub-6291771388053870";
        google_ad_width = 728;
        google_ad_height = 90;
        google_ad_format = "728x90_as";
        google_ad_type = "text";
        google_ad_channel ="";
        google_color_border = "336699";
        google_color_bg = "FFFFFF";
        google_color_link = "0000FF";
        google_color_url = "008000";
        google_color_text = "000000";
      </script>
      <script type="text/javascript" src="http://pagead2.googlesyndication.com/pagead/show_ads.js"></script>
    </div>
  
    <p>Copyright 2006-2008 - OpenQA</p>
  </div>
  
  <div id="top-menu">
    <ul>
      <li><a href="http://clearspace.openqa.org/community/selenium">Forums</a></li>
      <li><a href="http://wiki.openqa.org/display/GRID/Welcome">Wiki</a></li>
      <li><a href="http://jira.openqa.org/browse/GRID">Bug Tracker</a></li>
      <li><a href="http://ph7spot.com/about/contact_me">Contacting</a></li>
      <li><a href="http://www.openqa.org/sass">OpenQA Account Management</a></li>
      <li class="last"><a href="http://www.openqa.org/sponsors.action">Sponsors</a></li>
    </ul>
  </div>

</body>
</html>
