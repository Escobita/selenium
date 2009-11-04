# Used to map a name to a set of dependencies

class Java < BaseGenerator
  def build_classpath_(dep_name)
    t = Rake::Task[dep_name.to_sym]
    if t.nil? 
      puts "No match for #{dep_name}"
      return []
    end

    classpath = []
    deps = t.deps || []
    deps += t.prerequisites

    deps.each do |dep|      
      if Rake::Task.task_defined?(dep.to_sym) then
        classpath += build_classpath_(dep)
      else
        classpath += FileList[dep]
      end
    end
    
    FileList[dep_name].each do |match|
      classpath.push match if match =~ /\.jar/
    end
    classpath.push t.out unless t.out.nil?
    classpath
  end
  
  def out_path_(args)
    "build/#{args[:name]}.jar".to_sym
  end
  
  def jar(args)
    if !jar?
      puts "Unable to locate 'jar' command"
      exit -1
    end
    
    if args[:srcs].nil?
      puts "No srcs specified for #{args[:name]}"
    end

    out = out_path_(args)

    create_deps_(out_path_(args), args)

    t = Rake::Task[args[:name].to_sym]
    t.deps = args[:deps]
    t.out = out

    file out do
      # Build the classpath from the list of dependencies
      classpath = build_classpath_(args[:name].to_sym)

      temp = "#{out}_classes"
      mkdir_p temp

      puts "Building: #{args[:name]} as #{out}"

      # Compile
      cmd = "javac -cp #{classpath.join(classpath_separator?)} -g -source 5 -target 5 -d #{temp} #{FileList[args[:srcs]]} " 
      sh cmd, :verbose => false

      # TODO(simon): make copy_resource_ handle this for us
      # Copy resources over
      resources = args[:resources] || []
      resources.each do |res|
        if (res.kind_of? Symbol)
          res = Rake::Task[res].out
        end
        
        if (res.kind_of? Hash) 
          res.each do |from, to|
            dir = to.gsub(/\/.*?$/, "")
            mkdir_p "#{temp}/#{dir}", :verbose => false
            cp_r find_file(from), "#{temp}/#{to}"
          end
        else
          target = res.gsub(/build\//, '')
          copy_resource_(target, temp)
        end
      end

      cmd = "cd #{temp} && jar cMf ../../#{out} *"
      sh cmd, :verbose => false

      rm_rf temp
    end
  end
  
  def run_test_(args)
    classpath = build_classpath_(args[:name])
    
    main = args[:main] || "junit.textui.TestRunner"
    
    test_string = 'java -Xmx128m -Xms128m '
    test_string += '-cp "' + classpath.join(classpath_separator?) + '" ' if classpath.length > 1
    test_string += main
    test_string += ' ' + args[:args] if args[:args]

    if args[:tests].length == 0
      result = sh test_string, :verbose => false
    else
      args[:tests].each do |test|
        puts "Running: #{test}\n"
        test_string += " #{test} "
        result = sh test_string, :verbose => false
      end
    end

    result
  end
  
  def test(args)
    jar(args)
    
    out = out_path_(args)
    
    file "#{args[:name]}_never_there" => [ "#{out}" ] do
      if (args[:run].nil? || args[:run])    
        tests = `jar tvf #{out}` 
        tests = tests.split /\n/
        tests.map! do |clazz|
          clazz =~ /.*\s+(.*TestSuite\.class)/ ? $1.gsub("/", ".").gsub(/\.class\s*$/, "") : nil
        end
        tests.compact!

        args[:tests] = tests
        run_test_ args
      else 
        puts "Skipping tests for #{args[:name]}"
      end
    end

    task "#{args[:name]}" => "#{args[:name]}_never_there"
  end
  
  def uberjar(args)
    out = out_path_(args)
    
    create_deps_(out_path_(args), args)
    
    file out do
      puts "Building #{args[:name]} as #{out}"
      
      # Take each dependency, extract and then rezip
      temp = "#{out}_temp"
      mkdir_p temp
      
      all = build_uberlist_(args[:deps])
      all.each do |dep|
        sh "cd #{temp} && jar xf ../../#{dep}", :verbose => false
      end
      
      sh "cd #{temp} && jar cMf ../../#{out} *", :verbose => false
      rm_rf temp
    end
  end
  
  def build_uberlist_(task_names)
    all = []
    tasks = task_names || []
    tasks.each do |dep|
      next unless Rake::Task.task_defined? dep.to_sym
      t = Rake::Task[dep.to_sym]
      
      all.push t.out if t.out.to_s =~ /\.jar$/
      
      all += build_uberlist_(t.deps)
      all += build_uberlist_(t.prerequisites)
    end
    
    all.uniq
  end
end

def java_jar(args)
  Java.new().jar(args)
end

def java_test(args)
  Java.new().test(args)
end

def java_uberjar(args)
  Java.new().uberjar(args)
end

def jruby(args)
  str = "java "
  str << "-Djava.awt.headless=true " if args[:headless]

  str << "-jar third_party/jruby/jruby-complete-1.4.0RC1.jar "
  str << "-I#{args[:include].join(File::PATH_SEPARATOR)} " if args[:include]

  Array(args[:require]).each do |f|
    str << "-r#{f} "
  end

  str << "#{args[:command]} "
  str << args[:files].join(' ') if args[:files]

  sh str

end