def find_sources(args)
  all_srcs = FileList.new
  
  srcs = args[:srcs] || []
  srcs.each do |src|
    all_srcs.add("#{@base}/#{src}")
  end
  
  all_srcs
end

def get_dependencies(task_name)
  all_prereqs = []
  if !Rake::Task.task_defined?(task_name)
    return [ task_name ]
  end
    
  task = Rake::Task[task_name]
  
  preqs = task.prerequisites

  preqs.each do |preq|
    preqs += get_dependencies(preq)
  end
  
  preqs
end

def build_classpath(task_name)
  classpath = []
  
  get_dependencies(task_name).each do |dep|
    classpath.push(dep) if dep.to_s =~ /\.jar$/ and File.exists? dep
  end
  
  classpath.sort.uniq
end

def java_library(args)
  name = build_task_name(args[:name])
  dir = "build/#{@base}"
  jar_name = "#{args[:name]}.jar"
  jar_file = "#{dir}/#{jar_name}"
  
  # Add the dependencies
  deps = args[:deps] || []
  deps.each do |dep|
    if dep.is_a? Symbol
      task name => "//#{@base}:#{dep}"
    elsif dep.to_s =~ /\/\//
      task name => dep
    elsif File.exists? "#{@base}/#{dep}"
      task name => "#{@base}/#{dep}"
    else
      puts "Cannot match #{dep}"
    end
  end
  
  srcs = find_sources(args)
  
  if (srcs.length() > 0)
    file jar_file => srcs do
      puts "Building: #{name} as #{jar_file}"
      
      classpath = build_classpath(name)
      
      output_dir = "#{jar_file}_build"
      mkdir_p output_dir
      cmd = "javac  -g -source 5 -target 5 -d #{output_dir} "
      cmd += "-cp #{classpath.join(':')} " if classpath.length > 0
      cmd += "#{srcs}"
      sh cmd
      sh "cd #{output_dir} && jar cMf ../#{jar_name} *"
      rm_rf output_dir
    end
    
    desc "Compile #{jar_file}"
    task name.to_sym => jar_file
  end
  
  java_test(args)
end

def java_test(args)
  jar_file = "build/#{@base}/#{args[:name]}.jar"
  
  name = build_task_name(args[:name])
  
  # Scan the sources looking for something that looks like a test
  add_test_target = false
  find_sources(args).each do |src|
    add_test_target |= src =~ /.*TestSuite.java$/
  end
  
  if !add_test_target
    return
  end
  
  desc "Run the test suites in the sources"
  task "#{name}_test" => name do
    tests = `jar tvf #{jar_file}` 
    tests = tests.split /\n/
    tests.map! do |clazz|
      clazz =~ /.*\s+(.*TestSuite\.class)/ ? $1.gsub("/", ".").gsub(/\.class\s*$/, "") : nil
    end
    tests.compact!

    args[:tests] = tests
    args[:task] = name
    run_test_ args
  end
end

def run_test_(args)
  classpath = build_classpath(args[:task])

  main = args[:main] || "junit.textui.TestRunner"
  
  test_string = 'java -Xmx128m -Xms128m '
  test_string += '-cp "' + classpath.join(classpath_separator?) + '" ' if classpath.length > 1
  test_string += main
  test_string += ' ' + args[:args] if args[:args]

  if args[:tests].length == 0
    result = sh test_string
  else
    args[:tests].each do |test|
      puts "Running: #{test}\n"
      test_string += " #{test} "
      result = sh test_string
    end
  end

  result
end