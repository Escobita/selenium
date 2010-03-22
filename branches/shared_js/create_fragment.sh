#!/bin/bash
           
# Use like:
#
# create_fragment.sh bot.locator bot.locator.findElement findElement foo.js

REQUIRE=$1
METHOD=$2
CALLABLE=$3
OUTPUT=$4

[ -d build ] || mkdir build

# Create the source file
echo "goog.require('${REQUIRE}'); goog.exportSymbol('${CALLABLE}', ${METHOD});" >> build/${OUTPUT}.tmp.js

python third_party/closure/bin/calcdeps.py \
  -c third_party/closure/bin/compiler-20100201.jar \
  -o compiled \
  -f "--third_party=true" \
  -f "--js_output_file=build/${OUTPUT}.js" \
  -f "--compilation_level=ADVANCED_OPTIMIZATIONS" \
  -p third_party/closure/goog/ \
  -p common/src/js \
  -i build/${OUTPUT}.tmp.js

rm build/${OUTPUT}.tmp.js