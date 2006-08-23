class TestCase < ActiveRecord::Base
	belongs_to :run
	
	def self.parse(table)
	 status = ''
	 pass = HTML::Document.new(table).find_all(:tag => 'tr', :attributes => {:bgcolor => '#ffcccc'}).size == 0
	 if(pass)
	   status = "passed"
	 else
	   status = "failed"
	 end
	 TestCase.new(:status => status, :note => table)
	end
	
	def self.create_by_name_and_table(name, table)
	 test_case = self.parse(table)
	 test_case.name = name
	 return test_case
	end
end
