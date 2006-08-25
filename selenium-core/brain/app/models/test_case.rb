class TestCase < ActiveRecord::Base
	belongs_to :run
	
	def self.parse_status(table)
	 return "skipped" unless table
	 status = ''
	 pass = HTML::Document.new(table).find_all(:tag => 'tr', :attributes => {:bgcolor => '#ffcccc'}).size == 0
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
