require "spec_helper"

describe "Error" do

  it "should have an appropriate message" do
    driver.navigate.to url_for("xhtmlTest.html")

    lambda { driver.find_element(:id, "nonexistant") }.should raise_error(
        WebDriver::Error::NoSuchElementError, /unable to (find|locate) element/i # TODO: pick one of "find" vs "locate"
    )
  end

  it "should show stack trace information" do
    driver.navigate.to url_for("xhtmlTest.html")
    rescued = false
    ex = nil

    begin
      driver.find_element(:id, "nonexistant")
    rescue => ex
      rescued = true
    end

    ex.backtrace.first.should include("[remote server]")
  end
end