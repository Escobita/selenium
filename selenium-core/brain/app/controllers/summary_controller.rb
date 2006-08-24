class SummaryController < ApplicationController
  def index
    @runs = Run.find(:all)
  end
  
  def detail
    @run = Run.find(params["id"])
  end
end
