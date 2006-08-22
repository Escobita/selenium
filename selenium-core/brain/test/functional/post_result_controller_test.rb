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

  # Replace this with your real tests.
  def test_truth
    assert true
  end
  
end
