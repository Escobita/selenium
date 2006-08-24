class TestCase < ActiveRecord::Base
	belongs_to :run
    FAILED_COLOR = "#ffcccc"
	
	def self.parse_status(table)
	 return "skipped" unless table
	 status = ''
	 # dirty way: check whether result table include the pink color or not
	 pass = !(table.include?(FAILED_COLOR))
	 if(pass)
	   status = "passed"
	 else
	   status = "failed"
	 end
	 return status
	end
	
	def self.create_by_name_and_table(name, table)
	 status = parse_status(table)
	 return TestCase.new(:name => name, :status => status, :note => table)
	end
end
