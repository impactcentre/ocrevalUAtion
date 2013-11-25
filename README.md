ocrevalUAtion [![Build Status](https://secure.travis-ci.org/impactcentre/ocrevalUAtion.png?branch=master)](http://travis-ci.org/impactcentre/ocrevalUAtion)
=============

This set of classes provides basic support to perform the comparison of
two text files: a reference file (a ground-truth document, usually in
PAGE XML format) and a the output from an OCR engine (a text file).

You can build an executable jar by using Maven and running
'mvn package'.

The classes defined are listed below:

[distance.ArrayEditDistance<Type>](https://github.com/impactcentre/ocrevalUAtion/blob/master/src/main/java/eu/digitisation/distance/ArrayEditDistance.java)
Provides a basic implementations of some popular edit distance methods
(currently, Levenshtein and indel) applied to arrays of objects.

[distance.StringEditDistance](https://github.com/impactcentre/ocrevalUAtion/blob/master/src/main/java/eu/digitisation/distance/StringEditDistance.java)
Provides basic implementations of some popular edit distance methods 
operating on strings (currently, Levenshtein and indel).

[distance.TextFileEncoder](https://github.com/impactcentre/ocrevalUAtion/blob/master/src/main/java/eu/digitisation/distance.TextFileEncoder.java)
Encode a text file as an array of Integers (one code per word).

[io.CharFilter](https://github.com/impactcentre/ocrevalUAtion/blob/master/src/main/java/eu/digitisation/io/CharFilter.java)
Transform text according to a mapping between (source, target) 
Unicode character sequences.

[io.TextContent](https://github.com/impactcentre/ocrevalUAtion/blob/master/src/main/java/eu/digitisation/io/TextContent.java)
Reads and normalizes text from file content, 
and optionally applies a CharFilter.

[io.UnicodeReader](https://github.com/impactcentre/ocrevalUAtion/blob/master/src/main/java/eu/digitisation/io/UnicodeReader.java)
Transformations between Unicode strings and codepoints.

[io.WordScanner](https://github.com/impactcentre/ocrevalUAtion/blob/master/src/main/java/eu/digitisation/io/WordScanner.java)
A simple and fast text scanner that reads words 
from a File, String or InputStream which reads words from a file and
performs the tokenization oriented by information-retrieval
requirements.

[math.ArrayMath](https://github.com/impactcentre/ocrevalUAtion/blob/master/src/main/java/eu/digitisation/math/ArrayMath.java)
Standard operations on arrays: sum, average, max, min, standard deviation.

[math.Counter<T>](https://github.com/impactcentre/ocrevalUAtion/blob/master/src/main/java/eu/digitisation/math/Counter.java)
Counts the number of different objects, a map between
objects and integers which can be incremented and decremented.

[ocr.ErrorMeasure](https://github.com/impactcentre/ocrevalUAtion/blob/master/src/main/java/eu/digitisation/ocr/ErrorMeasure.java)
The main class which computes character and word error rates.

[Page.Geometry](https://github.com/impactcentre/ocrevalUAtion/blob/master/src/main/java/eu/digitisation/Page/Geometry.java)
Geometry information contained in one PAGE-XML file.

[Page.Sort](https://github.com/impactcentre/ocrevalUAtion/blob/master/src/main/java/eu/digitisation/Page/Sort.java)
PAGE-XML regions order in the document can differ form reading order. 
This class makes the order of elements in the document consistent 
with the reading order stored therein.

[Page.TextContent](https://github.com/impactcentre/ocrevalUAtion/blob/master/src/main/java/eu/digitisation/Page/TextContent.java)
Textual content in a PAGE XML: selects only those
elements listed in a properties file (TOC-entry, heading,
drop-capital, paragraph).

[Page.TextRegion](https://github.com/impactcentre/ocrevalUAtion/blob/master/src/main/java/eu/digitisation/Page/TextRegion.java)
A TextRegion in a PAGE-XML document.

[Page.Viewer](https://github.com/impactcentre/ocrevalUAtion/blob/master/src/main/java/eu/digitisation/Page/Viewer.java)
Shows text regions (as stored in PAGE XML) on image.

[xml.DocumentBuilder](https://github.com/impactcentre/ocrevalUAtion/blob/master/src/main/java/eu/digitisation/xml/DocumentBuilder.java)
A builder and parser for XML documents.

[xml.DocumentWriter](https://github.com/impactcentre/ocrevalUAtion/blob/master/src/main/java/eu/digitisation/xml/DocumentWriter.java)
Writes XML document to String or File.

[xml.Elements](https://github.com/impactcentre/ocrevalUAtion/blob/master/src/main/java/eu/digitisation/xml/Elements.java)
Auxiliary functions to access and modify elements in a document.

