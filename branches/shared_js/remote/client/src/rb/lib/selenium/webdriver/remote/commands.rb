# @private
class Selenium::WebDriver::Remote::Bridge

  #
  # http://code.google.com/p/selenium/wiki/JsonWireProtocol#Command_Reference
  #

  command :newSession,                      :post,    "session"
  command :getCapabilities,                 :get,     "session/:session_id"
  command :quit,                            :delete,  "session/:session_id"
  command :getCurrentWindowHandle,          :get,     "session/:session_id/window_handle"
  command :getWindowHandles,                :get,     "session/:session_id/window_handles"
  command :getCurrentUrl,                   :get,     "session/:session_id/url"
  command :get,                             :post,    "session/:session_id/url"
  command :goForward,                       :post,    "session/:session_id/forward"
  command :goBack,                          :post,    "session/:session_id/back"
  command :refresh,                         :post,    "session/:session_id/refresh"
  command :executeScript,                   :post,    "session/:session_id/execute"
  command :screenshot,                      :get,     "session/:session_id/screenshot"
  command :switchToFrame,                   :post,    "session/:session_id/frame"
  command :switchToWindow,                  :post,    "session/:session_id/window"
  command :getSpeed,                        :get,     "session/:session_id/speed"
  command :setSpeed,                        :post,    "session/:session_id/speed"
  command :getAllCookies,                   :get,     "session/:session_id/cookie"
  command :addCookie,                       :post,    "session/:session_id/cookie"
  command :deleteAllCookies,                :delete,  "session/:session_id/cookie"
  command :deleteCookieNamed,               :delete,  "session/:session_id/cookie/:name"
  command :getPageSource,                   :get,     "session/:session_id/source"
  command :getTitle,                        :get,     "session/:session_id/title"
  command :findElement,                     :post,    "session/:session_id/element"
  command :findElements,                    :post,    "session/:session_id/elements"
  command :getActiveElement,                :post,    "session/:session_id/element/active"
  command :describeElement,                 :get,     "session/:session_id/element/:id"
  command :findChildElement,                :post,    "session/:session_id/element/:id/element"
  command :findChildElements,               :post,    "session/:session_id/element/:id/elements"
  command :clickElement,                    :post,    "session/:session_id/element/:id/click"
  command :submitElement,                   :post,    "session/:session_id/element/:id/submit"
  command :getElementValue,                 :get,     "session/:session_id/element/:id/value"
  command :sendKeysToElement,               :post,    "session/:session_id/element/:id/value"
  command :getElementTagName,               :get,     "session/:session_id/element/:id/name"
  command :clearElement,                    :post,    "session/:session_id/element/:id/clear"
  command :isElementSelected,               :get,     "session/:session_id/element/:id/selected"
  command :setElementSelected,              :post,    "session/:session_id/element/:id/selected"
  command :toggleElement,                   :post,    "session/:session_id/element/:id/toggle"
  command :isElementEnabled,                :get,     "session/:session_id/element/:id/enabled"
  command :getElementAttribute,             :get,     "session/:session_id/element/:id/attribute/:name"
  command :elementEquals,                   :get,     "session/:session_id/element/:id/equals/:other"
  command :isElementDisplayed,              :get,     "session/:session_id/element/:id/displayed"
  command :getElementLocation,              :get,     "session/:session_id/element/:id/location"
  command :getElementLocationInView,        :get,     "session/:session_id/element/:id/location_in_view"
  command :getElementSize,                  :get,     "session/:session_id/element/:id/size"
  command :hoverOverElement,                :post,    "session/:session_id/element/:id/hover"
  command :dragElement,                     :post,    "session/:session_id/element/:id/drag"
  command :getElementValueOfCssProperty,    :get,     "session/:session_id/element/:id/css/:property_name"

  command :close,                           :delete,  "session/:session_id/window"
  command :getElementText,                  :get,     "session/:session_id/element/:id/text"
  command :getVisible,                      :get,     "session/:session_id/visible"
  command :setVisible,                      :post,    "session/:session_id/visible"
  # command :switchToFrameByIndex # TODO: switchToFrameByIndex
  # command :switchToDefaultContent # TODO: switchToDefaultContent
end