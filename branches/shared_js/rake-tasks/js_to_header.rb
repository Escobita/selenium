
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
          converted = line.chomp.gsub(/\\/, "\\\\").gsub(/"/, "\\\"")
          to << "L\"#{converted}\",\n"
        end
          
        to << "NULL\n"
        to << "};\n\n"
        to << "#endif\n"
      end
    end
  end
end