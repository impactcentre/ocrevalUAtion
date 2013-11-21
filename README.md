ocrevalUAtion [![Build Status](https://secure.travis-ci.org/impactcentre/ocrevalUAtion.png?branch=master)](http://travis-ci.org/impactcentre/ocrevalUAtion)
=============

This set of classes provides basic support to perform the comparison of
two text files: a reference file (a ground-truth document, usually in
PAGE XML format) and a the output from an OCR engine (a text file).

You can build an executable jar by using Maven and running
'mvn package'.

The classes defined are listed below:

**distance.ArrayEditDistance<Type>**
Provides a basic implementations of some popular edit distance methods 
(currently, Levenshtein and indel) applied to arrays of objects.

**distance.StringEditDistance**	
Provides basic implementations of some popular edit distance methods 
operating on strings (currently, Levenshtein and indel).

**distance.TextFileEncoder**	
Encode a text file as an array of Integers (one code per word).

**io.CharFilter**
Transform text according to a mapping between (source, target) 
Unicode character sequences.

**io.TextBuilder**
Makes text (either as StringBuilder or as array of strings) from file content
and optionally applies a CharFilter.

**io.UnicodeReader**	
Transformations between Unicode strings and codepoints.

**io.WordScanner**
A simple and fast text scanner that reads words 
from a File, String or InputStream which reads words from a file and
performs the tokenization oriented by information-retrieval
requirements.

**math.ArrayMath**	
Standard operations on arrays: sum, average, max, min, standard deviation.

**math.Counter<T>**
Counts the number of different objects, a map between
objects and integers which can be incremented and decremented.

**ErrorMeasure**	
The main class which computes character and word error rates.

**Page.Geometry**	
Geometry information contained in one PAGE-XML file.

**Page.Sort**
PAGE-XML regions order in the document can differ form reading order. 
This class makes the order of elements in the document consistent 
with the reading order stored therein.

**Page.TextContent**	
Textual content in a PAGE XML: selects only those
elements listed in a properties file (TOC-entry, heading,
drop-capital, paragraph).

**Page.TextRegion**	
A TextRegion in a PAGE-XML document.

**Page.Viewer**	 
Shows text regions (as stored in PAGE XML) on image.

**xml.DocumentBuilder**	
A builder and parser for XML documents.

**xml.DocumentWriter**	
Writes XML document to String or File.

**xml.Elements**
Auxiliary functions to accesss and modify elements in a document.

