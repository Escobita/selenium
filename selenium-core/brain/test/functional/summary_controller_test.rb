require File.dirname(__FILE__) + '/../test_helper'
require 'summary_controller'

# Re-raise errors caught by the controller.
class SummaryController; def rescue_action(e) raise e end; end

class SummaryControllerTest < Test::Unit::TestCase
  fixtures :runs

  def setup
    @controller = SummaryController.new
    @request    = ActionController::TestRequest.new
    @response   = ActionController::TestResponse.new
  end

  def test_should_load_all_runs_in_index_action
    get :index
    assert_equal(2, assigns(:runs).size)
  end
  
end
