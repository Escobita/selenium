#!/bin/sh

find src/main/webapp -name "*.html" | xargs tidy -i -m