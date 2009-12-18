module Selenium
  module WebDriver
    class TargetLocator

      #
      # @api private
      #

      def initialize(driver)
        @bridge = driver.bridge
      end

      #
      # switch to the frame with the given id
      #

      def frame(id)
        @bridge.switchToFrame id
      end

      #
      # switch to the frame with the given id
      #
      # If given a block, this method will return to the original window after
      # block execution.
      #
      # @param id
      #   A window handle
      #

      def window(id)
        if block_given?
          original = @bridge.getCurrentWindowHandle
          @bridge.switchToWindow id
          yield
          @bridge.switchToWindow original
        else
          @bridge.switchToWindow id
        end
      end

      #
      # get the active element
      #
      # @return [WebDriver::Element]
      #

      def active_element
        @bridge.switchToActiveElement
      end

    end # TargetLocator
  end # WebDriver
end  # Selenium
