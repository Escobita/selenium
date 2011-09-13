module Selenium
  module WebDriver
    module Chrome

      describe Profile do
        let(:profile) { Profile.new }
        
        it "launches Chrome with a custom profile" do
          profile['autofill.disabled'] = true
          
          begin
            driver = WebDriver.for :chrome, :profile => profile
          ensure
            driver.quit if driver
          end
        end
        
        it "should be serializable to JSON" do
          profile['foo.boolean'] = true

          new_profile = Profile.from_json(profile.to_json)
          new_profile['foo.boolean'].should be_true
        end
      end

    end # Chrome
  end # WebDriver
end # Selenium

