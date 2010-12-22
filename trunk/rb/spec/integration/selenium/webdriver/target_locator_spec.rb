require File.expand_path("../spec_helper", __FILE__)

describe "WebDriver::TargetLocator" do
  it "should switch to a frame" do
    driver.navigate.to url_for("iframes.html")
    driver.switch_to.frame("iframe1")

    # TODO: expectations?
  end

  not_compliant_on :browser => :ie do
    it "should switch to a frame by Element" do
      driver.navigate.to url_for("iframes.html")

      iframe = driver.find_element(:tag_name => "iframe")
      driver.switch_to.frame(iframe)

      # TODO: expectations?
    end

    it "should switch to a window and back when given a block" do
      driver.navigate.to url_for("xhtmlTest.html")

      driver.find_element(:link, "Open new window").click
      driver.title.should == "XHTML Test Page"

      driver.switch_to.window("result") do
        driver.title.should == "We Arrive Here"
      end

      driver.title.should == "XHTML Test Page"

      reset_driver!
    end

    it "should handle exceptions inside the block" do
      driver.navigate.to url_for("xhtmlTest.html")

      driver.find_element(:link, "Open new window").click
      driver.title.should == "XHTML Test Page"

      lambda {
        driver.switch_to.window("result") { raise "foo" }
      }.should raise_error(RuntimeError, "foo")

      driver.title.should == "XHTML Test Page"

      reset_driver!
    end
  end

  not_compliant_on :browser => [:chrome, :ie] do
    it "should switch to a window" do
      driver.navigate.to url_for("xhtmlTest.html")

      driver.find_element(:link, "Open new window").click
      driver.title.should == "XHTML Test Page"

      driver.switch_to.window("result")
      driver.title.should == "We Arrive Here"

      reset_driver!
    end

    it "should use the original window if the block closes the popup" do
      driver.navigate.to url_for("xhtmlTest.html")

      driver.find_element(:link, "Open new window").click
      driver.title.should == "XHTML Test Page"

      driver.switch_to.window("result") do
        driver.title.should == "We Arrive Here"
        driver.close
      end

      driver.current_url.should include("xhtmlTest.html")
      driver.title.should == "XHTML Test Page"
      reset_driver!
    end
  end

  it "should switch to default content" do
    driver.navigate.to url_for("iframes.html")

    driver.switch_to.frame 0
    driver.switch_to.default_content

    driver.find_element(:id => "iframe_page_heading")
  end

  it "should find active element" do
    driver.navigate.to url_for("xhtmlTest.html")
    driver.switch_to.active_element.should be_an_instance_of(WebDriver::Element)
  end
end

