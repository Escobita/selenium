.. _Getting Started:

The 5 Minute Getting Started Guide
==================================

.. _chapter09-reference:

WebDriver is a tool for automating testing web applications, and in particular 
to verify that they work as expected. It aims to provide a friendly API that's
easy to explore and understand, which will help make your tests easier to 
read and maintain. It's not tied to any particular test framework, so it can 
be used equally well with JUnit, TestNG or from a plain old "main" method. 
This "Getting Started" guide introduces you to WebDriver's Java API and helps 
get you started becoming familiar with it.

Start by `Downloading <http://code.google.com/p/selenium/downloads/list>`_ 
the latest binaries and unpack them into a directory. From now on, we'll 
refer to that as ``$WEBDRIVER_HOME``. Now, open your favourite IDE and:

 * Start a new Java project in your favourite IDE
 * Add all the JAR files under ``$WEBDRIVER_HOME`` to the ``CLASSPATH``

You can see that WebDriver acts just as a normal Java library does: it's 
entirely self-contained, and you don't need to remember to start any 
additional processes or run any installers before using it. 

You're now ready to write some code. An easy way to get started is this 
example, which searches for the term "Cheese" on Google and then outputs the 
result page's title to the console. You'll start by using the `HtmlUnitDriver`_. 
This is a pure Java driver that runs entirely in-memory. Because of this, you 
won't see a new browser window open. 

.. code-block:: java

    package org.openqa.selenium.example;

    import org.openqa.selenium.By;
    import org.openqa.selenium.WebDriver;
    import org.openqa.selenium.WebElement;
    import org.openqa.selenium.htmlunit.HtmlUnitDriver;

    public class Example  {
        public static void main(String[] args) {
            // Create a new instance of the html unit driver
            // Notice that the remainder of the code relies on the interface, 
            // not the implementation.
            WebDriver driver = new HtmlUnitDriver();

            // And now use this to visit Google
            driver.get("http://www.google.com");

            // Find the text input element by its name
            WebElement element = driver.findElement(By.name("q"));

            // Enter something to search for
            element.sendKeys("Cheese!");

            // Now submit the form. WebDriver will find the form for us from the element
            element.submit();

            // Check the title of the page
            System.out.println("Page title is: " + driver.getTitle());
        }
    }

Compile and run this. You should see a line with the title of the Google search 
results as output on the console. Congratulations, you've managed to get 
started with WebDriver!

In this next example, you shall use a page that requires Javascript to work 
properly, such as Google Suggest. You will also be using the `FirefoxDriver`_. 
Make sure that Firefox is installed on your machine and is in the normal 
location for your OS.

.. TODO: add default locations as a note (or footnote)

Once that's done, create a new class called GoogleSuggest, which looks like:

.. code-block:: java

    package org.openqa.selenium.example;

    import java.util.List;

    import org.openqa.selenium.By;
    import org.openqa.selenium.WebDriver;
    import org.openqa.selenium.RenderedWebElement;
    import org.openqa.selenium.WebElement;
    import org.openqa.selenium.firefox.FirefoxDriver;

    public class GoogleSuggest {
        public static void main(String[] args) throws Exception {
            // The Firefox driver supports javascript 
            WebDriver driver = new FirefoxDriver();
            
            // Go to the Google Suggest home page
            driver.get("http://www.google.com/webhp?complete=1&hl=en");
            
            // Enter the query string "Cheese"
            WebElement query = driver.findElement(By.name("q"));
            query.sendKeys("Cheese");

            // Sleep until the div we want is visible or 5 seconds is over
            long end = System.currentTimeMillis() + 5000;
            while (System.currentTimeMillis() < end) {
                // Browsers which render content (such as Firefox and IE) return "RenderedWebElements"
                RenderedWebElement resultsDiv = (RenderedWebElement) driver.findElement(By.className("gac_m"));

                // If results have been returned, the results are displayed in a drop down.
                if (resultsDiv.isDisplayed()) {
                  break;
                }
            }

            // And now list the suggestions
            List<WebElement> allSuggestions = driver.findElements(By.xpath("//td[@class='gac_c']"));
            
            for (WebElement suggestion : allSuggestions) {
                System.out.println(suggestion.getText());
            }
         }
    }

When you run this program, you'll see the list of suggestions being printed 
to the console. That's all there is to using WebDriver! 

Hopefully, this will have whet your appetite for more. In the following 
`Next Steps`_ section you will learn more about how to use WebDriver for things 
such as navigating forward and backward in your browser's history, and how to 
use frames and windows. It also provides a more complete discussion of the 
examples than has been done as you've been `Getting Started`_. If you're ready, 
let's take the `Next Steps`_!

.. _Next Steps:

Next Steps For Using WebDriver
==============================

Which Implementation of WebDriver Should I Use?
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

WebDriver is the name of the key interface against which tests should be 
written, but there are several implementations. These are:

==========================  ========================  =============================================
*Name of driver*            *Available on which OS?*  *Class to instantiate*
==========================  ========================  =============================================
`HtmlUnitDriver`_            All                       org.openqa.selenium.htmlunit.HtmlUnitDriver
`FirefoxDriver`_             All                       org.openqa.selenium.firefox.FirefoxDriver
`InternetExplorerDriver`_    Windows                   org.openqa.selenium.ie.InternetExplorerDriver
`ChromeDriver`_              All                       org.openqa.selenium.chrome.ChromeDriver
==========================  ========================  =============================================

You can find out more information about each of these by following the links in 
the table. Which you use depends on what you want to do. For sheer speed, the 
`HtmlUnitDriver`_ is great, but it's not graphical, which means that you can't 
watch what's happening. As a developer, you may be comfortable with this, but 
sometimes it's good to be able to test using a real browser, especially when 
you're showing a demo of your application (or running the tests) for an 
audience. Often, this idea is referred to as "safety", and it falls into two 
parts. Firstly, there's "actual safety", which refers to whether or not the 
tests works as they should. This can be measured and quantified. Secondly, 
there's "perceived safety", which refers to whether or not an observer believes 
the tests work as they should. This varies from person to person, and will 
depend on their familiarity with the application under test, WebDriver and your 
testing framework.

To support higher "perceived safety", you may wish to chose a driver such as 
the `FirefoxDriver`_. This has the added advantage that this driver actually 
renders content to a screen, and so can be used to detect information such 
as the position of an element on a page, or the CSS properties that apply to 
it. However, this additional flexibility comes at the cost of slower overall 
speed. By writing your tests against the WebDriver interface, it is possible to 
pick the most appropriate driver for a given test.

To keep things simple, let's start with the `HtmlUnitDriver`_:

.. code-block:: java
    
    WebDriver driver = new HtmlUnitDriver();

Navigating
~~~~~~~~~~

The first thing you'll want to do with WebDriver is navigate to a page. The 
normal way to do this is by calling "get":

.. code-block:: java

    driver.get("http://www.google.com");

WebDriver will wait until the page has fully loaded (that is, the "onload" 
event has fired) before returning control to your test or script. It's worth
noting that if your page uses a lot of AJAX on load then WebDriver may not
know when it has completely loaded. If you need to ensure such pages are 
fully loaded then you can use "waits".

.. TODO: link to a section on explicit waits in WebDriver

Interacting With the Page
~~~~~~~~~~~~~~~~~~~~~~~~~

Just being able to go to places isn't terribly useful. What we'd really like 
to do is to interact with the pages, or, more specifically, the HTML elements 
within a page. First of all, we need to find one. WebDriver offers a number of 
ways of finding elements. For example, given an element defined as:

.. code-block:: html

    <input type="text" name="passwd" id="passwd-id" />

we could find it using any of:

.. code-block:: java

    WebElement element;
    element = driver.findElement(By.id("passwd-id"));
    element = driver.findElement(By.name("passwd"));
    element = driver.findElement(By.xpath("//input[@id='passwd-id']"));

You can also look for an link by its text, but be careful! The text must be an 
exact match! You should also be careful when using `XpathInWebDriver`_. If 
there's more than one element that matches the query, then only the first will 
be returned. If nothing can be found, a ``NoSuchElementException`` will be 
thrown.

WebDriver has an "Object-based" API; we represent all types of elements using 
the same interface: `WebElement <http://selenium.googlecode.com/svn/webdriver/javadoc/org/openqa/selenium/WebElement.html>`_. 
This means that although you may see a lot of possible methods you could invoke 
when you hit your IDE's auto-complete key combination, not all of them will 
make sense or be valid. Don't worry! WebDriver will attempt to do the Right 
Thing, and if you call a method that makes no sense ("setSelected()" on a 
"meta" tag, for example) an exception will be thrown.

So, you've got an element. What can you do with it? First of all, you may want 
to enter some text into a text field:

.. code-block:: java

    element.sendKeys("some text");
    
You can simulate pressing the arrow keys by using the "Keys" class:

.. code-block:: java

    element.sendKeys(" and some", Keys.ARROW_DOWN);

It is possible to call sendKeys on any element, which makes it possible to test 
keyboard shortcuts such as those used on GMail. A side-effect of this is that 
typing something into a text field won't automatically clear it. Instead, what 
you type will be appended to what's already there. You can easily clear the 
contents of a text field or textarea:

.. code-block:: java

    element.clear();

Filling In Forms
~~~~~~~~~~~~~~~~

We've already seen how to enter text into a textarea or text field, but what 
about the other elements? You can "toggle" the state of checkboxes, and you 
can use "setSelected" to set something like an OPTION tag selected. Dealing 
with SELECT tags isn't too bad:

.. code-block:: java

    WebElement select = driver.findElement(By.xpath("//select"));
    List<WebElement> allOptions = select.findElements(By.tagName("option"));
    for (WebElement option : allOptions) {
        System.out.println(String.format("Value is: %s", option.getValue()));
        option.setSelected();
    }

This will find the first "SELECT" element on the page, and cycle through each 
of it's OPTIONs in turn, printing out their values, and selecting each in turn. 
As you can see, this isn't the most efficient way of dealing with SELECT 
elements. WebDriver's support classes come with one called "Select", which 
provides useful methods for interacting with these.

.. code-block:: java

    Select select = new Select(driver.findElement(By.xpath("//select")));
    select.deselectAll();
    select.selectByVisibleText("Edam");

This will deselect all OPTIONs from the first SELECT on the page, and then 
select the OPTION with the displayed text of "Edam".

Once you've finished filling out the form, you probably want to submit it. One 
way to do this would be to find the "submit" button and click it:

.. code-block:: java

    driver.findElement(By.id("submit")).click();  // Assume the button has the ID "submit" :)

Alternatively, WebDriver has the convenience method "submit" on every element. 
If you call this on an element within a form, WebDriver will walk up the DOM 
until it finds the enclosing form and then calls submit on that. If the 
element isn't in a form, then the ``NoSuchElementException`` will be thrown:

.. code-block:: java

    element.submit();

Getting Visual Information And Drag And Drop
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Sometimes you want to extract some visual information out of an element, 
perhaps to see if it's visible or where it is on screen. You can find out this 
information by casting the element to a ``RenderedWebElement``:

.. code-block:: java

    WebElement plain = driver.findElement(By.name("q"));
    RenderedWebElement element = (RenderedWebElement) element;

Not all drivers render their content to the screen (such as the 
`HtmlUnitDriver`_), so it's not safe to assume that the cast will work, but if 
it does you can gather additional information such as the size and location of 
the element. In addition, you can use drag and drop, either moving an element 
by a certain amount, or on to another element:

.. code-block:: java

    RenderedWebElement element = (RenderedWebElement) driver.findElement(By.name("source"));
    RenderedWebElement target = (RenderedWebElement) driver.findElement(By.name("target"));

    element.dragAndDropOn(target);

Moving Between Windows and Frames
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

It's rare for a modern web application not to have any frames or to be 
constrained to a single window. WebDriver supports moving between named 
windows using the "switchTo" method:

.. code-block:: java

    driver.switchTo().window("windowName");

All calls to ``driver`` will now be interpreted as being directed to the 
particular window. But how do you know the window's name? Take a look at the 
javascript or link that opened it:

.. code-block:: html

    <a href="somewhere.html" target="windowName">Click here to open a new window</a>

Alternatively, you can pass a "window handle" to the "switchTo().window()" 
method. Knowing this, it's possible to iterate over every open window like so:

.. code-block:: java

    for (String handle : driver.getWindowHandles()) {
        driver.switchTo().window(handle);
    }

You can also swing from frame to frame (or into iframes):

.. code-block:: java

    driver.switchTo().frame("frameName");

It's possible to access subframes by separating the path with a dot, and you 
can specify the frame by its index too. That is:

.. code-block:: java

    driver.switchTo().frame("frameName.0.child");

would go to the frame named "child" of the first subframe of the frame called 
"frameName". *All frames are evaluated as if from **top**.*

Navigation: History and Location
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Earlier, we covered navigating to a page using the "get" command (
``driver.get("http://www.example.com")``) As you've seen, WebDriver has a 
number of smaller, task-focused interfaces, and navigation is a useful task. 
Because loading a page is such a fundamental requirement, the method to do this 
lives on the main WebDriver interface, but it's simply a synonym to:

.. code-block:: java

    driver.navigate().to("http://www.example.com");

To reiterate: "``navigate().to()``" and "``get()``" do exactly the same thing. 
One's just a lot easier to type than the other!

The "navigate" interface also exposes the ability to move backwards and forwards in your browser's history:

.. code-block:: java

    driver.navigate().forward();
    driver.navigate().back();

Please be aware that this functionality depends entirely on the underlying 
browser. It's just possible that something unexpected may happen when you call 
these methods if you're used to the behaviour of one browser over another.

Cookies
~~~~~~~

Before we leave these next steps, you may be interested in understanding how to 
use cookies. First of all, you need to be on the domain that the cookie will be 
valid for:

.. code-block:: java

    // Go to the correct domain
    driver.get("http://www.example.com");

    // Now set the cookie. This one's valid for the entire domain
    Cookie cookie = new Cookie("key", "value");
    driver.manage().addCookie(cookie);

    // And now output all the available cookies for the current URL
    Set<Cookie> allCookies = driver.manage().getCookies();
    for (Cookie loadedCookie : allCookies) {
        System.out.println(String.format("%s -> %s", loadedCookie.getName(), loadedCookie.getValue()));
    }

Next, Next Steps!
~~~~~~~~~~~~~~~~~

This has been a high level walkthrough of WebDriver and some of its key 
capabilities. You may want to look at the `DesignPatterns`_ to get some ideas 
about how you can reduce the pain of maintaining your tests and how to make 
your code more modular.
