
require 'tsort'
require 'rake-tasks/crazy_fun/mappings/common'

class Sortable < Hash
  include TSort
  alias tsort_each_node each_key
  def tsort_each_child(node, &block)
    fetch(node).each(&block)
  end
  
  def js_files(task)
    files = []
    deps = build_graph(task, self)

    deps.keys.each do |k|
#      puts "#{k} => #{deps[k].join(', ')}"
    end
    
    deps.tsort.each do |dep|
      if dep.to_s =~ /\.js$/ 
        if dep.to_s =~ /^build\//
          next
        end
        
        if !File.exists? dep and dep != output
          raise StandardError, "#{dep} does not exist" 
        end
        
        files.push dep
      end
    end
    
    files
  end
  
  def build_graph(task, graph)
    task.prerequisites.each do |dep|
      deps = graph[task.name] || []
      deps.push dep unless deps.include? dep
      graph[task.name] = deps
      deps = graph[dep] || []
      graph[dep] = deps
      
      if Rake::Task.task_defined? dep
        graph = build_graph(Rake::Task[dep], graph)
      end
    end
    
    graph
  end
  
end


class JavascriptMappings
  def add_all(fun)
    fun.add_mapping("js_library", Javascript::CheckPreconditions.new)
    fun.add_mapping("js_library", Javascript::CreateTask.new)
    fun.add_mapping("js_library", Javascript::CreateTaskShortName.new)
    fun.add_mapping("js_library", Javascript::AddDependencies.new)
    fun.add_mapping("js_library", Javascript::SpoofCompilation.new)
    
    fun.add_mapping("js_binary", Javascript::CheckPreconditions.new)
    fun.add_mapping("js_binary", Javascript::CreateTask.new)
    fun.add_mapping("js_binary", Javascript::CreateTaskShortName.new)
    fun.add_mapping("js_binary", Javascript::AddDependencies.new)
    fun.add_mapping("js_binary", Javascript::Compile.new)
  end
end

module Javascript
  class BaseJs < Tasks
    def js_name(dir, name)
      name = task_name(dir, name)
      js = "build/" + (name.slice(2 ... name.length))
      js = js.sub(":", "/")
      js << ".js"

      js.gsub("/", Platform.dir_separator)
    end
  end

  class CheckPreconditions
    def handle(fun, dir, args)
      raise StandardError, ":name must be set" if args[:name].nil?
      raise StandardError, ":srcs must be set" if args[:srcs].nil? and args[:deps].nil?
    end
  end
  
  class CreateTaskShortName < BaseJs
    def handle(fun, dir, args)
      name = task_name(dir, args[:name])

      if (name.end_with? "/#{args[:name]}:#{args[:name]}")
        name = name.sub(/:.*$/, "")

        task name => task_name(dir, args[:name])

        Rake::Task[name].out = js_name(dir, args[:name])
      end
    end
  end

  class CreateTask < BaseJs
    def handle(fun, dir, args)
      name = js_name(dir, args[:name])
      task_name = task_name(dir, args[:name])

      file name
      
      desc "Compile and optimize #{name}"
      task task_name => name
      
      Rake::Task[task_name].out = name
    end
  end
    
  class AddDependencies < BaseJs
    def handle(fun, dir, args)
      if args[:deps].nil? && args[:srcs].nil?
        return
      end
      
      task = Rake::Task[js_name(dir, args[:name])]
      add_dependencies(task, dir, args[:deps])
      add_dependencies(task, dir, args[:srcs])

      # Implicitly, every src file has a dependency on the deps
      # Expand the deps to file tasks
      all_deps = []
      (args[:deps] || []).each do |dep|
        task_name = task_name(dir, dep)
	if !Rake::Task.task_defined? task_name
	  file task_name
	end
	all_deps.push(task_name)
      end
      (args[:srcs] || []).each do |src|
	to_filelist(dir, src.to_s).each do |s|
	  file s => all_deps
	end
      end
    end
  end
  
  class SpoofCompilation < BaseJs
    def handle(fun, dir, args)
      output = js_name(dir, args[:name])
      
      file output do
        mkdir_p File.dirname(output)
        
        t = Rake::Task[task_name(dir, args[:name])]
        deps = Sortable.new.js_files(t)
        
#        puts "\n\n\nDeps: #{deps.join(", ")}"
        
        touch output
      end
    end
  end
  
  class Compile < BaseJs
    def handle(fun, dir, args)
      output = js_name(dir, args[:name])
      
      file output do
        puts "Compiling: #{task_name(dir, args[:name])} as #{output}"
        
        t = Rake::Task[task_name(dir, args[:name])]

	puts "  Calculating dependencies"

        deps = Sortable.new.js_files(t)

        puts "  Creating directory #{output}"
        
        mkdir_p File.dirname(output)
      
        cmd = "java -Xmx128m -Xms128m -jar third_party/closure/bin/compiler-2009-12-17.jar --js_output_file "
        cmd << output + " "
        cmd << "--third_party true "
        cmd << "--compilation_level WHITESPACE_ONLY "
        cmd << "--formatting PRETTY_PRINT "
        cmd << "--js "
        cmd << deps.join(" --js ")
        
	puts "  Building now"

        sh cmd, :verbose => true do |ok, res|
          if !ok
            rm_f output, :verbose => false
          end
        end
      end
    end    
  end
end
