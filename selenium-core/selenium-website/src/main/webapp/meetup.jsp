<%@ page import="java.io.*" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%!
    private static final String file = "/home/selenium-j2ee/meetup.txt";
    //private static final String file = "/tmp/meetup.txt";

    private List<Map<String, String>> readConference() throws IOException {
        ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();

        FileInputStream fis = new FileInputStream(file);
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));
        String line;
        while ((line = br.readLine()) != null) {
            String[] parts = line.split("\t");
            Map<String, String> map = new HashMap<String, String>();
            if (parts.length > 0) {
                map.put("name", parts[0]);
                if (parts.length > 1) {
                    map.put("email", parts[1]);
                    if (parts.length > 2) {
                        map.put("interests", parts[2]);
                    } else {
                        map.put("interests", "");
                    }
                } else {
                    map.put("email", "");
                    map.put("interests", "");
                }
            } else {
                continue;
            }
            list.add(map);
        }
        br.close();
        fis.close();

        return list;
    }

    private void addEntry(String name, String email, String interests) throws IOException {
        FileOutputStream fos = new FileOutputStream(file, true);
        StringBuilder sb = new StringBuilder();
        sb.append(name).append("\t");
        sb.append(email).append("\t");
        sb.append(interests).append("\n");
        fos.write(sb.toString().getBytes());
        fos.close();
    }
%>
<%
    String name = request.getParameter("name");
    String email = request.getParameter("email");
    String interests = request.getParameter("interests");

    if (name != null) {
        if (email == null) {
            email = "N/A";
        }

        if (interests == null) {
            interests = "N/A";
        }

        name = name.replaceAll("\\s+", " ");
        email = email.replaceAll("\\s+", " ");
        interests = interests.replaceAll("\\s+", " ");

        addEntry(name, email, interests);
    }
%>
<html>
<head>
    <title>Selenium Users Open Evening: February 25, 2008</title>
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
    <style type="text/css">
        .blah {
            padding: 5px;
            font-size: 14px;
            line-height: 18px;
            margin-left: 25px;
            margin-right: 25px
        }

        #attendees {
            border-color: #600;
            border-style: solid;
            border-width: 0 0 1px 1px;
            border-spacing: 0;
            border-collapse: collapse;
        }

        #attendees th {
            border-color: #600;
            border-style: solid;
            margin: 0;
            padding: 4px;
            border-width: 1px 1px 0 0;
            background-color: #FFC;
            text-align: left;
        }

        #attendees td {
            border-color: #600;
            border-style: solid;
            margin: 0;
            padding: 4px 15px 4px 4px;
            border-width: 1px 1px 0 0;
            background-color: #FFC;
            font-size: 16px;
        }

        .formLabel {
            text-align: right;
            padding-right: 15px;
            font-size: 16px;
        }

        .textinput {
            width: 200px;
        }

        textarea {
            width: 200px;
            height: 50px;
        }
    </style>
</head>
<body onload="onLoadFunc()">

<script type="text/javascript" src="http://www.openqa.org/shared/projects/header-start.jsp?name=selenium"></script>
<script type="text/javascript"
        src="http://pagead2.googlesyndication.com/pagead/show_ads.js">
</script>
<script type="text/javascript" src="http://www.openqa.org/shared/projects/header-end.jsp?name=selenium"></script>

<h1>Selenium Users Open Evening: February 25, 2008</h1>

<div class="blah" style="border: 1px solid black;">
    With representatives from all the major Selenium projects on hand to
    present ideas, discuss the future of Selenium and answer audience
    questions, the Selenium Open Evening is an opportunity to get involved
    in the future of the project. With Selenium developers from as far
    apart as London, Tokyo and the US and Lightning Talks on related subjects,
    this is a great way to meet Selenium users and meet some of the other
    brightest minds in web testing and Agile development!
</div>

<p/>

<h2>Time &amp; Location</h2>

<div class="blah">
    <b>Monday, February 25, 2008<br/>
        6:30PM PST to 9:00PM PST</b>

    <p/>

    <a href="http://maps.google.com/maps?f=q&hl=en&geocode=&q=1600+Amphitheatre+Pky,+Mountain+View,+CA&sll=37.38948,-122.08171&sspn=0.085789,0.108147&ie=UTF8&z=16&iwloc=addr">Google
        Campus</a><br/>
    1600 Amphitheatre Pky<br/>
    Mountain View, CA<br/>

    <p/>

    Upon arriving, please proceed to reception in building 41 and ask for directions to the <u>Selenium Users Open
    Evening</u>.
</div>

<h2>What to Bring</h2>

<div class="blah">
    <u>Small bites of food and drinks will be provided</u>, so all that we ask is that you please bring your curiosity
    and passion about Selenium, open source, automated QA, or agile development!
</div>

<h2>Attendees</h2>

<div class="blah">
    Interested in attending? Please fill out the following form with your <b>first name and family name</b>. This will help us make sure the experience optimized
    for you and the other attendees. Please tell us what interests you have or why you're attending. You can also
    optionally provide your email address in case we need to get in contact with you after the meetup.  Please also note we are limited to 100, and will be closing the list when that number is reached. You'll have badges waiting for you at Google - so bring some proof of ID.

    <p/>
	This list is now closed. The room in question at Google is at full capacity now.  People not on the list will be turned away at the front desk - sorry!	
	<p/>

    <p/>

    <table id="attendees">
        <thead>
            <tr>
                <th>&nbsp;</th>
                <th>Name</th>
                <th>Interests</th>
            </tr>
        </thead>
        <tbody>
            <%
                List<Map<String, String>> list = readConference();
                int count = 1;
                for (Map<String, String> entry : list) {
            %>
            <tr>
                <td class="countCol"><%= count++ %>
                </td>
                <td class="nameCol"><%= entry.get("name") %>
                </td>
                <td class="interestsCol"><%= entry.get("interests") %>
                </td>
            </tr>
            <%
                }
            %>
        </tbody>
    </table>
</div>


<script type="text/javascript" src="http://www.openqa.org/shared/projects/footer.jsp?name=selenium"></script>

</body>
</html>