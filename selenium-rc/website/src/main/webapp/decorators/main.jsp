<%@ taglib uri="sitemesh-decorator" prefix="decorator" %>
<html>
<head>
    <title>Selenium RC: <decorator:title/></title>
    <style type="text/css">
        @import "http://www.openqa.org/shared/css/main.css";
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
    <script type="text/javascript">
        function onLoadFunc() {
            document.getElementById("content").style.minHeight = document.getElementById("menu").clientHeight + 'px';
        }
    </script>
    <decorator:head/>
</head>

<body onload="onLoadFunc()">

<script type="text/javascript" src="http://www.openqa.org/shared/projects/header-start.jsp"></script>
<script type="text/javascript"
  src="http://pagead2.googlesyndication.com/pagead/show_ads.js">
</script>
<script type="text/javascript" src="http://www.openqa.org/shared/projects/header-end.jsp"></script>

<h1 class="first">Selenium RC: <decorator:title/></h1>

<decorator:body/>

<script type="text/javascript" src="http://www.openqa.org/shared/projects/leftnav.jsp"></script>
<script type="text/javascript"
  src="http://pagead2.googlesyndication.com/pagead/show_ads.js">
</script>
<script type="text/javascript" src="http://www.openqa.org/shared/projects/footer.jsp"></script>

</body>
</html>
