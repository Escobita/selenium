module Selenium
  module RSpec
    module Reporting
      
      class SystemCapture
        
        def initialize(selenium_driver, example, file_path_strategy)
          @selenium_driver = selenium_driver
          @example = example
          @file_path_strategy = file_path_strategy 
        end
        
        def capture_system_state
          # Selenium RC seems to 'freeze' every so often when calling 
          # getHTMLSource, especially when DeepTest timeout is low, I need to investigate...
          # Set deeptest :timeout_in_seconds => 30 to see it happen
          begin
            capture_html_snapshot
          rescue Exception => e
            STDERR.puts "WARNING: Could not capture HTML snapshot: #{e}"
          end
          begin
            capture_screenshot
          rescue Exception => e
            STDERR.puts "WARNING: Could not capture screenshot: #{e}"
          end
          begin
            retrieve_remote_control_logs
          rescue Exception => e
            STDERR.puts "WARNING: Could not retrieve remote control logs: #{e}"
          end
        end

        def capture_html_snapshot
          unless @selenium_driver.session_started?
            puts "Skipping HTML Snapshot retrieval, there is no current Selenium session"
            return
          end
          html = @selenium_driver.get_html_source
          File.open(@file_path_strategy.file_path_for_html_capture(@example), "w") { |f| f.write html }
        end

        def capture_screenshot
          @selenium_driver.window_maximize if @selenium_driver.session_started?
          encodedImage = @selenium_driver.capture_screenshot_to_string
          pngImage = Base64.decode64(encodedImage)
          File.open(@file_path_strategy.file_path_for_png_capture(@example), "w") { |f| f.write pngImage }
        end

        def retrieve_remote_control_logs
          logs = @selenium_driver.retrieve_last_remote_control_logs
          File.open(@file_path_strategy.file_path_for_remote_control_logs(@example), "w") { |f| f.write logs }
        end
        
      end
      
    end
  end
end