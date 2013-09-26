Description
===========

`asciidivs2html` is a command line program that reads a special formatted ascii art file
that descrives a set of html divs and generates the corresponding html 

This program was made to answer the following stackoverflow question:

[How can I parse ASCII Art to HTML using Java or Javascript?](http://stackoverflow.com/questions/18637676/how-can-i-parse-ascii-art-to-html-using-java-or-javascript#18637676)

Example: 

The following input

```text
--------------------------------
I                              I
I   -------          -------   I
I   I     I          I     I   I
I   I  A  I          I  B  I   I
I   I     I          I     I   I
I   -------          -------   I
I                              I
I                              I
--------------------------------
```

will give the following output: 

```text
<div>
   <div>
      A
   </div>
   <div>
      B
   </div>
</div>
``` 

And this input:

```text
---------------------------------
I   ----------------  -------   I
I   I              I  I     I   I
I   I   --------   I  I  B  I   I
I   I   I      I   I  -------   I
I   I A I  C   I   I            I
I   I   I      I   I  -------   I
I   I   --------   I  I  D  I   I
I   ----------------  -------   I
---------------------------------
``` 

will generate this output:

```text
<div>
   <div>
      B
   </div>
   <div>
      A
      <div>
            C
      </div>
   </div>
   <div>
      D
   </div>
</div>
```

Changelog
=========

## 1.1
- Added a java translation of the c++ code

## 1.0
- First release of the program


