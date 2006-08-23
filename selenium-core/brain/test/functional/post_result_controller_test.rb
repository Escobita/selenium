require File.dirname(__FILE__) + '/../test_helper'
require 'post_result_controller'

# Re-raise errors caught by the controller.
class PostResultController; def rescue_action(e) raise e end; end

class PostResultControllerTest < Test::Unit::TestCase
  def setup
    @controller = PostResultController.new 
    @request    = ActionController::TestRequest.new
    @response   = ActionController::TestResponse.new
  end
  
  def test_should_get_run_object_after_post_results
    version = "TEST_VERSION"
    parameters = {"selenium.version" => version}
    post(:index, parameters)
    run = Run.find_by_version(version)
    assert_not_nil(run)
  end  

  def test_should_record_user_agent_after_post_results
    @request.env["HTTP_USER_AGENT"] = "TEST_AGENT"
    version = "TEST_VERSION"
    parameters = {"selenium.version" => version}
    post(:index, parameters)
    run = Run.find_by_version(version)
    assert_equal("TEST_AGENT", run.agent)
  end
  
end
