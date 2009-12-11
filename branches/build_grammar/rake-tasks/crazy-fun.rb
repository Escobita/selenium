require 'rake-tasks/checks'
require 'rake-tasks/java2'

def build_task_name(name)
  if "." != @base
    name = "//#{@base}:#{name}"
  else
    name = "//#{name}"
  end
end

def stub_task(args)
  name = build_task_name(args[:name])
  task name.to_sym do
    puts "Building: #{name}"
  end
  
  deps = args[:deps] || []
  deps.each do |dep|
    if dep.is_a? Symbol
      task name.to_sym => build_task_name(dep)
    else
      task name.to_sym => dep
    end
  end
  
end

Dir["**/build.desc"].each do |file|
  @base = File.dirname(file)
  raw = IO.read(file)
  eval raw, binding
  
  last = file.split("/")[-2]
  long_name = "//#{@base}:#{last}".to_sym
  task "//#{@base}".to_sym => long_name if Rake::Task.task_defined? long_name
  
  @base = nil
end
