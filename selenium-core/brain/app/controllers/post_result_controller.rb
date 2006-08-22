
class PostResultController < ApplicationController
  def index
	run = Run.parse(params)
    render :text => CGI::unescape(request.raw_post)
  end
  
end
