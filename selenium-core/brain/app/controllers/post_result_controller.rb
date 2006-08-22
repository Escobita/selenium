class PostResultController < ApplicationController
  def index
    puts request.raw_post
    render :text => request.raw_post
  end
end
