#!/bin/sh

find src/main/webapp -name "*.html" | xargs tidy -i -m
find src/main/webapp -name "*.html" | xargs perl -p -i -e "s/HTML Tidy for Mac OS X/HTML Tidy/g"
find src/main/webapp -name "*.html" | xargs perl -p -i -e "s/vers 1 September 2005/version num removed/g"
