
class PostResultController < ApplicationController
  
  def index
    run = Run.parse(params)
    run.agent = request.env["HTTP_USER_AGENT"]
    run.save
    render :text => CGI::unescape(request.raw_post)
  end
  
end
