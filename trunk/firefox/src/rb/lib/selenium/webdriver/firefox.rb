require "timeout"
require "socket"

require "selenium/webdriver/firefox/util"
require "selenium/webdriver/firefox/binary"
require "selenium/webdriver/firefox/profiles_ini"
require "selenium/webdriver/firefox/profile"
require "selenium/webdriver/firefox/extension_connection"
require "selenium/webdriver/firefox/launcher"
require "selenium/webdriver/firefox/bridge"

module Selenium
  module WebDriver
    module Firefox

       DEFAULT_PROFILE_NAME         = "WebDriver".freeze
       DEFAULT_PORT                 = 7055
       DEFAULT_ENABLE_NATIVE_EVENTS = [:windows, :linux].include? Platform.os

    end
  end
end