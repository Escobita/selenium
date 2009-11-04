module WebDriver
  module Chrome
    class Bridge
      include BridgeHelper

      def initialize
        @executor = CommandExecutor.new

        @launcher = Launcher.launcher
        @launcher.launch
        # TODO: @launcher.kill
      end

      def browser
        :chrome
      end

      def get(url)
        execute :request => 'get',
                :url     => url
      end

      def goBack
        execute :request => 'goBack'
      end

      def goForward
        execute :request => 'goForward'
      end

      def getCurrentUrl
        execute :request => 'getCurrentUrl'
      end

      def getTitle
        execute :request => 'getTitle'
      end

      def getPageSource
        execute :request => 'getPageSource'
      end

      def switchToWindow(name)
        execute :request    => 'switchToWindow',
                :windowName => name
      end

      def switchToFrame(id)
        execute :request => 'switchToFrameByName',
                :name    => id
      end

      def quit
        @launcher.kill # FIXME: let chrome extension take care of this
        execute :request => 'quit'
      end

      def close
        execute :request => 'close'
      end

      def getWindowHandles
        execute :request => 'getWindowHandles'
      end

      def getCurrentWindowHandle
        execute :request => 'getCurrentWindowHandle'
      end

      def setSpeed(value)
        @speed = value
      end

      def getSpeed
        @speed
      end

      def executeScript(script, *args)
        typed_args = args.map { |e| wrap_script_argument(e) }

        resp = execute :request => 'executeScript',
                       :script  => script,
                       :args    => typed_args

        unwrap_script_argument resp
      end

      def addCookie(cookie)
        execute :request => 'addCookie',
                :cookie  => cookie
      end

      def deleteCookie(name)
        execute :request => 'deleteCookie',
                :name    => name
      end

      def getAllCookies
        execute :request => 'getCookies'
      end

      def deleteAllCookies
        execute :request => 'deleteAllCookies'
      end

      def findElementByClassName(parent, class_name)
        find_element_by 'class name', class_name, parent
      end

      def findElementsByClassName(parent, class_name)
        find_elements_by 'class name', class_name, parent
      end

      def findElementById(parent, id)
        find_element_by 'id', id, parent
      end

      def findElementsById(parent, id)
        find_elements_by 'id', id, parent
      end

      def findElementByLinkText(parent, link_text)
        find_element_by 'link text', link_text, parent
      end

      def findElementsByLinkText(parent, link_text)
        find_elements_by 'link text', link_text, parent
      end

      def findElementByPartialLinkText(parent, link_text)
        find_element_by 'partial link text', link_text, parent
      end

      def findElementsByPartialLinkText(parent, link_text)
        find_elements_by 'partial link text', link_text, parent
      end

      def findElementByName(parent, name)
        find_element_by 'name', name, parent
      end

      def findElementsByName(parent, name)
        find_elements_by 'name', name, parent
      end

      def findElementByTagName(parent, tag_name)
        find_element_by 'tag name', tag_name, parent
      end

      def findElementsByTagName(parent, tag_name)
        find_elements_by 'tag name', tag_name, parent
      end

      def findElementByXpath(parent, xpath)
        find_element_by 'xpath', xpath, parent
      end

      def findElementsByXpath(parent, xpath)
        find_elements_by 'xpath', xpath, parent
      end


      #
      # Element functions
      #

      def clickElement(element)
        execute :request   => 'clickElement',
                :id        => element
      end

      def getElementTagName(element)
        execute :request   => 'getElementTagName',
                :id        => element
      end

      def getElementAttribute(element, name)
        execute :request  => 'getElementAttribute',
                :id       => element,
                :name     => name
      end

      def getElementValue(element)
        execute :request   => 'getElementValue',
                :id        => element
      end

      def getElementText(element)
        execute :request   => 'getElementText',
                :id        => element
      end

      def getElementLocation(element)
        data = execute :request   => 'getElementLocation',
                       :id        => element

        Point.new data['x'], data['y']
      end

      def getElementSize(element)
        execute :request   => 'getElementSize',
                :id        => element
      end

      def sendKeysToElement(element, string)
        execute :request   => 'sendKeysToElement',
                :id        => element,
                :value     => string.split(//u)
      end

      def clearElement(element)
        execute :request   => 'clearElement',
                :id        => element
      end

      def isElementEnabled(element)
        execute :request   => 'isElementEnabled',
                :id        => element
      end

      def isElementSelected(element)
        execute :request   => 'isElementSelected',
                :id        => element
      end

      def isElementDisplayed(element)
        execute :request   => 'isElementDisplayed',
                :id        => element
      end

      def submitElement(element)
        execute :request   => 'submitElement',
                :id        => element
      end

      def toggleElement(element)
        execute :request   => 'toggleElement',
                :id        => element
      end

      def setElementSelected(element)
        execute :request   => 'setElementSelected',
                :id        => element
      end

      def getElementValueOfCssProperty(element, prop)
        execute :request      => 'getElementValueOfCssProperty',
                :id           => element,
                :propertyName => prop
      end

      def getActiveElement
        Element.new self, element_id_from(execute(:request => 'getActiveElement'))
      end
      alias_method :switchToActiveElement, :getActiveElement

      def hoverOverElement
        execute :request   => 'hoverOverElement',
                :id        => element
      end

      def dragElement(element, rigth_by, down_by)
        raise UnsupportedOperationError, "drag and drop unsupported in Chrome"
        execute :drag_element, {:id => element}, element, rigth_by, down_by
      end

      private

      def find_element_by(how, what, parent = nil)
        if parent
          id = execute :request => 'findChildElement',
                       :id      => parent,
                       :using   => how,
                       :value   => what
        else
          id = execute :request => 'findElement',
                       :using   => how,
                       :value   => what
        end

        Element.new self, element_id_from(id)
      end

      def find_elements_by(how, what, parent = nil)
        if parent
          ids = execute :request => 'findChildElements',
                        :id      => parent,
                        :using   => how,
                        :value   => what
        else
          ids = execute :request => 'findElements',
                        :using   => how,
                        :value   => what
        end

        ids.map { |id| Element.new self, element_id_from(id) }
      end


      private

      def execute(command)
        resp = raw_execute command
        code = resp['statusCode']
        if e = Error.for_code(code)
          msg = resp['value']['message'] if resp['value']
          msg ||= "unknown exception for #{command.inspect}"
          msg << " (#{code})"

          raise e, msg
        end

        resp['value']
      end

      def raw_execute(command)
        @executor.execute command
      end

    end # Bridge
  end # Chrome
end # WebDriver
