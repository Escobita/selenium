class SummaryController < ApplicationController
  def index
    @runs = Run.find(:all)
  end
end
