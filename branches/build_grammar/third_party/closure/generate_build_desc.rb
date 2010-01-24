#!/usr/bin/ruby

seen = {}

File.readlines("goog/deps.js").each do |line|
  next unless line =~ /^goog.addDependency/
  
  # Each dependency is src, provides, deps. Extract the pieces
  m = /goog.addDependency\('(.*?)', \[(.*?)\], \[(.*?)\]/.match(line)
  srcs = m[1]
  provides = m[2]
  deps = m[3].gsub(".", "_").gsub("'", "").downcase.split(", ")
  
  # Everything implictly depends on "goog" except goog itself
  deps.push("goog") unless deps.include? "goog" or srcs == "base.js"
  
  deps = deps.join(", :")
  
  if (deps.length > 0)
    deps = ":" + deps
  end
  
  # provides could contain a list, but we're going to use it as the name of the
  # library. We'll munge it and break it up. One moment please, caller
  provides = provides.gsub(".", "_") # Make name more like a symbol
  provides = provides.gsub("'", "") # Get rid of unnecessary quotes
  provides = provides.downcase # names are normally lowercased
  provides = provides.split(", ")

  # Output time
  provides.each do |name|
    if seen[srcs].nil?
      seen[srcs] = name
      
      if deps.length > 0
        puts "js_library(:name => \"#{name}\",\n  :srcs => [ \"goog/#{srcs}\"],\n  :deps => [ #{deps} ])\n\n"
      else
        puts "js_library(:name => \"#{name}\",\n  :srcs => [ \"goog/#{srcs}\"])\n\n"
      end
    else
      puts "js_library(:name => \"#{name}\",\n  :deps => [ :#{seen[srcs]} ])\n\n"
    end
  end  
end