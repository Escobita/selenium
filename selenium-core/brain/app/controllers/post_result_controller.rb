class PostResultController < ApplicationController
  def index
    puts request.raw_post
  end
end
