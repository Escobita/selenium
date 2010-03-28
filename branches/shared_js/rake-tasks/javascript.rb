
def js_library(args)
  out = "build/#{args[:name]}.js"
  
  file out => FileList.new(args[:srcs]) do
    mkdir_p File.dirname(out)
    
    cmd = ""
    cmd << "java -jar third_party/py/jython.jar third_party/closure/bin/calcdeps.py "
    cmd << "-c third_party/closure/bin/compiler-20100201.jar "
    cmd << "-p third_party/closure/goog "
    cmd << "-p common/src/js "
    cmd << "-f \"--formatting=PRETTY_PRINT\" "
    cmd << "--output_file=#{out} "
    cmd << "-o compiled "
    FileList.new(args[:srcs]).each do |src|
      cmd << "-i #{src} "
    end
    
    sh cmd
  end
  task args[:name] => out
  Rake::Task[args[:name]].out = out
end


# Compile a single JS method into a compressed fragment
def js_fragment(args)
  out = "build/#{args[:name]}.js"
  temp = "build/#{args[:name]}.tmp.js"
  
  file out do
    mkdir_p File.dirname(out)
    
    rm_f "#{out}.tmp"
    
    File.open(temp, "w") do |file|
      file << "goog.require('#{args[:module]}'); goog.exportSymbol('#{args[:name]}', #{args[:function]});"
    end
    
    cmd = ""
    cmd << "java -jar third_party/py/jython.jar third_party/closure/bin/calcdeps.py "
    cmd << "-c third_party/closure/bin/compiler-20100201.jar "
    cmd << "-o compiled "
    cmd << "-f \"--third_party=true\" "
    cmd << "-f \"--js_output_file=#{out}\" "
    cmd << "-f \"--compilation_level=ADVANCED_OPTIMIZATIONS\" "
    cmd << "-p third_party/closure/goog/ "
    cmd << "-p common/src/js "
    cmd << "-i #{temp}"
    
    sh cmd
    
    rm_f "#{out}.tmp"
  end
  
  task args[:name] => out
  Rake::Task[args[:name]].out = out
end


# Script to convert a fragment of JS into a series of strings in a C header

def js_to_header(args)
  out = "#{args[:root]}/#{args[:name]}.h"
  upper = args[:name].upcase
  
  file "#{args[:name]}.h" => args[:src] do
    mkdir_p File.dirname(out)
    
    File.open(args[:src], "r") do |from|    
      File.open(out, "w") do |to|
        to << "/* AUTO GENERATED - Do not edit by hand. */\n"
        to << "/* See rake-tasts/js_to_header.rb instead. */\n\n"
        to << "#ifndef #{upper}_H\n"
        to << "#define #{upper}_H\n\n"
        to << "wchar_t* #{upper}[] = {\n"
          
        while line = from.gets
          converted = line.chomp.gsub(/\\/, "\\\\\\").gsub(/"/, "\\\"")
          to << "L\"#{converted}\",\n"
        end
          
        to << "NULL\n"
        to << "};\n\n"
        to << "#endif\n"
      end
    end
  end
  
  task args[:name] => out
  Rake::Task[args[:name]].out = out
end