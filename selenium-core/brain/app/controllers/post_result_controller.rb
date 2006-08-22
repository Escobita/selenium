
class PostResultController < ApplicationController
  
  def index
    Run.parse_and_save(params)
    render :text => CGI::unescape(request.raw_post)
  end
  
end
