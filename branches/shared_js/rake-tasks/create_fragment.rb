
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
end
