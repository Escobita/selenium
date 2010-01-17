
class Tasks
  def task_name(dir, name)
    # Strip any leading ".", "./" or ".\\"
    # I am ashamed
    use = dir.gsub /\\/, '/'
    use = use.sub(/^\./, '').sub(/^\//, '')

    "//#{use}:#{name}"    
  end  
  
  def add_dependencies(target, dir, all_deps)
    return if all_deps.nil?
    
    all_deps.each do |dep|
      target.enhance dep_type(dir, dep)
    end
  end
  
  def copy_prebuilt(fun, out)
    src = fun.find_prebuilt(out)
    
    mkdir_p File.dirname(out)
    cp src, out
  end
  
  def copy_all(dir, srcs, dest)
    if srcs.is_a? Array
      copy_array(dir, srcs, dest)
    elsif srcs.is_a? String
      copy_string(dir, srcs, dest)
    elsif srcs.is_a? Hash
      copy_hash(dir, srcs, dest)
    elsif srcs.is_a? Symbol
      copy_symbol(dir, srcs, dest)
    else
      raise StandardError, "Undetermined type: #{srcs.class}"
    end
  end
  
  def zip(src, dest)
    out = File.expand_path(dest)
    
    sh "cd #{src} && jar cMf #{out} *"
  end
  
  private
  def copy_string(dir, src, dest)
    if Rake::Task.task_defined? src
      from = Rake::Task[src].out
    else
      from = FileList[dir + Platform.dir_separator + src]
    end
    
    cp_r from, to_dir(dest)
  end
  
  def copy_symbol(dir, src, dest)
    from = Rake::Task[task_name(dir, src)].out
    
    cp_r from, to_dir(dest)
  end

  def copy_array(dir, src, dest)
    src.each do |item|
      if item.is_a? Hash
        raise StandardError, "Undetermined type: #{item.class}"
      elsif item.is_a? Array
        raise StandardError, "Undetermined type: #{item.class}"
      elsif item.is_a? String
        copy_string(dir, item, dest)
      elsif item.is_a? Symbol
        copy_symbol(dir, item, dest)
      else 
        raise StandardError, "Undetermined type: #{item.class}"
      end
    end
  end
  
  def copy_hash(dir, src, dest)
    src.each do |key, value|
      cp_r dir + Platform.dir_separator + key, dest + Platform.dir_separator + value
    end
    
  end
  
  def to_dir(name)
    if !File.exists? name
      mkdir_p name
    end
    name
  end
  
  def dep_type(dir, dep)
    if dep.is_a? String
      if (dep.start_with? "//")
        return [ dep ]
      else
        return FileList["#{dir}/#{dep}"]
      end
    end
  
    if dep.is_a? Symbol
      return [ task_name(dir, dep) ]
    end
    
    throw "Unmatched dependency type"
  end
end

module Platform
  def windows?
    RUBY_PLATFORM.downcase.include?("win32")
  end

  def mac?
    RUBY_PLATFORM.downcase.include?("darwin")
  end

  def linux?
    RUBY_PLATFORM.downcase.include?("linux")
  end

  def cygwin?
    RUBY_PLATFORM.downcase.include?("cygwin")
  end
  
  def Platform.dir_separator
    Rake::Win32.windows? ? "\\" : "/"
  end
  
  def Platform.env_separator
    Rake::Win32.windows? ? ";" : ":"
  end
end