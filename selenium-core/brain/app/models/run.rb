class Clock
  def now
    Time.now
  end
end

class Run < ActiveRecord::Base
  has_many :test_cases
  
  @@clock = Clock.new
  def self.clock= (clock)
    @@clock = clock
  end
  
  def self.parse(params)
    run = Run.new(
                     :version => params["selenium.version"],
                     :revision => params["selenium.revision"],
                     :total_time => params["totalTime"].to_f,
                     :time => @@clock.now )		
    names = parse_test_case_names(params["suite"])
    (0...names.size).each do |i|
      run.test_cases << TestCase.create_by_name_and_table(names[i], params["testTable.#{i+1}"])
    end
    return run
  end
  
  private 
  
  def self.parse_test_case_names(suite)
    return [] if suite == nil
    HTML::Document.new(suite).find_all(:tag => "a").map do |e|
      e.children.first.to_s
    end
  end			
end
