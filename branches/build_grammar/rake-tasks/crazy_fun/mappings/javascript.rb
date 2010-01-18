
require 'rake-tasks/crazy_fun/mappings/common'

class JavascriptMappings
  def add_all(fun)
    fun.add_mapping("js_library", Javascript::CheckPreconditions.new)
    fun.add_mapping("js_library", Javascript::CreateTask.new)
    fun.add_mapping("js_library", Javascript::CreateTaskShortName.new)
    fun.add_mapping("js_library", Javascript::AddDependencies.new)
    fun.add_mapping("js_library", Javascript::Compile.new)
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
  
  class AddDependencies < BaseJs
    def handle(fun, dir, args)
      if args[:deps].nil?
        return
      end
      
      task = Rake::Task[js_name(dir, args[:name])]
      add_dependencies(task, dir, args[:deps])
    end
  end

  class Compile < BaseJs
    def handle(fun, dir, args)
      output = js_name(dir, args[:name])
      
      file output do
        mkdir_p File.dirname(output)
      
        cmd = "java -Xmx128m -Xms128m -jar third_party/closure/bin/compiler-2009-12-17.jar --js_output_file "
        cmd << output + " "
        cmd << "--third_party true --compilation_level WHITESPACE_ONLY --formatting PRETTY_PRINT "

        (args[:srcs] || []).each do |src|
          if src.is_a? String
            if Rake::Task.task_defined? src
              raise RuntimeError, "Unable to build symbol sources yet"
            else
              cmd << " --js "
              cmd << to_filelist(dir, src).to_a.join(" --js ")
            end
          end
        end
        deps = args[:deps] || []
        add_all_deps(cmd, dir, args[:deps])

        sh cmd do |ok, res|
          if !ok
            rm_f output, :verbose => false
          end
        end
      end
    end

    def add_all_deps(cmd, dir, deps)
      return if deps.nil? || deps.empty?

      deps.each do |dep|
        if Rake::Task.task_defined? dep
          t = Rake::Task[dep]
        else
          t = Rake::Task[task_name(dir, dep)]
        end
        cmd << " --js " + t.out + " "
      end
    end
  end
end
