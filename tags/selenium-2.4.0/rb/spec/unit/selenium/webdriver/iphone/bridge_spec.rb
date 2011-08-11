require File.expand_path("../../spec_helper", __FILE__)


module Selenium
  module WebDriver
    module IPhone
      describe Bridge do
        let(:default_url) { URI.parse(IPhone::Bridge::DEFAULT_URL) }
        let(:resp)        { {"sessionId" => "foo", "value" => Remote::Capabilities.iphone.as_json }}
        let(:http)        { mock(Remote::Http::Default, :call => resp).as_null_object   }

        it "uses the default iPhone driver URL" do
          http.should_receive(:server_url=).with default_url
          Bridge.new(:http_client => http)
        end

        it "uses the user-provided URL" do
          http.should_receive(:server_url=).with URI.parse("http://example.com")
          Bridge.new(:http_client => http, :url => "http://example.com")
        end
      end

    end # IPhone
  end # WebDriver
end # Selenium

