#/usr/bin/python
# coding: UTF-8

import glob
from docutils.core import publish_file

files = glob.glob("*.rst")

for file in files:
    publish_file(source_path=file,
                 destination_path=file[:-4] + ".html",
                 writer_name="html")
    
print "Done!!"