Some rules
==========

Here you'll find some rules we must follow to keep the documentation source 
files as clean as possible.

1) Try to keep lines of no more that 80 columns, this way most text editors 
   will be able to render the rst file in the same way, and will save us the
   annoyance of unending lines.
   The final html will be rendered 
   as espected as the parser joins all 
   consecutive lines to a single 
   paragraph until the next blank line.

2) Don't add extra spaces. The parser removes duplicated   spaces     anyway, 
   but it's better to keep the source rst files as clean as possible.
   
3) Don't use unicode characters, like ¶ ñ á or even “” and – (double dashes). 
   They sometimes are parsed correctly, but in some environment they don't, 
   which brings more problems than advantages...


Tips
====

Text highlighting
-----------------
You can apply certain style to text, just by putting some marks on it, here are
some examples: **bold**, *italics*, ``monospaced text``.

Links
-----

To make links you have 3 alternatives:

1) Easy but cluttered way: `Text linking <http://url.linked.com>`_
2) Common way: `Text linking`_ then you can write more text and you can call the
   link anywhere you want like now `Text linking`_
   Then, when you have some free space, you can write the target to that link
   (see the end of the file for the target linking).
3) Titles are link targets also! All you have to do is put the title followed 
   with an underscore like in `Some rules`_ or Tips_
   
Further reading
===============

Off course, the next point to go from here if you find any problems is:
http://docutils.sourceforge.net/docs/user/rst/quickref.html

.. _`Text linking`: http://url.linked.com