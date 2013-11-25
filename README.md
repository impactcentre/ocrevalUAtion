ocrevalUAtion [![Build Status](https://secure.travis-ci.org/impactcentre/ocrevalUAtion.png?branch=master)](http://travis-ci.org/impactcentre/ocrevalUAtion)
=============

This set of classes provides basic support to perform the comparison of
two text files: a reference file (a ground-truth document, usually in
PAGE XML format) and a the output from an OCR engine (a text file).

You can build an executable jar by using Maven and running
'mvn package'.

The classes defined are listed below:

[https://github.com/impactcentre/ocrevalUAtion/blob/master/src/main/java/eu/digitisation/distance/ArrayEditDistance.java](distance.ArrayEditDistance<Type>)
Provides a basic implementations of some popular edit distance methods
(currently, Levenshtein and indel) applied to arrays of objects.

[https://github.com/impactcentre/ocrevalUAtion/blob/master/src/main/java/eu/digitisation/distance/StringEditDistance.java](distance.StringEditDistance)
Provides basic implementations of some popular edit distance methods 
operating on strings (currently, Levenshtein and indel).

[https://github.com/impactcentre/ocrevalUAtion/blob/master/src/main/java/eu/digitisation/distance.TextFileEncoder.java](distance.TextFileEncoder)
Encode a text file as an array of Integers (one code per word).

[https://github.com/impactcentre/ocrevalUAtion/blob/master/src/main/java/eu/digitisation/io/CharFilter.java](io.CharFilter)
Transform text according to a mapping between (source, target) 
Unicode character sequences.

[https://github.com/impactcentre/ocrevalUAtion/blob/master/src/main/java/eu/digitisation/io/TextContent.java](io.TextContent)
Reads and normalizes text from file content, 
and optionally applies a CharFilter.

[https://github.com/impactcentre/ocrevalUAtion/blob/master/src/main/java/eu/digitisation/io/UnicodeReader.java](io.UnicodeReader)
Transformations between Unicode strings and codepoints.

[https://github.com/impactcentre/ocrevalUAtion/blob/master/src/main/java/eu/digitisation/io/WordScanner.java](io.WordScanner)
A simple and fast text scanner that reads words 
from a File, String or InputStream which reads words from a file and
performs the tokenization oriented by information-retrieval
requirements.

[https://github.com/impactcentre/ocrevalUAtion/blob/master/src/main/java/eu/digitisation/math/ArrayMath.java](math.ArrayMath)
Standard operations on arrays: sum, average, max, min, standard deviation.

[https://github.com/impactcentre/ocrevalUAtion/blob/master/src/main/java/eu/digitisation/math/Counter.java](math.Counter<T>)
Counts the number of different objects, a map between
objects and integers which can be incremented and decremented.

[https://github.com/impactcentre/ocrevalUAtion/blob/master/src/main/java/eu/digitisation/ocr/ErrorMeasure.java](ocr.ErrorMeasure)
The main class which computes character and word error rates.

[https://github.com/impactcentre/ocrevalUAtion/blob/master/src/main/java/eu/digitisation/Page/Geometry.java](Page.Geometry)
Geometry information contained in one PAGE-XML file.

[https://github.com/impactcentre/ocrevalUAtion/blob/master/src/main/java/eu/digitisation/Page/Sort.java](Page.Sort)
PAGE-XML regions order in the document can differ form reading order. 
This class makes the order of elements in the document consistent 
with the reading order stored therein.

[https://github.com/impactcentre/ocrevalUAtion/blob/master/src/main/java/eu/digitisation/Page/TextContent.java](Page.TextContent)
Textual content in a PAGE XML: selects only those
elements listed in a properties file (TOC-entry, heading,
drop-capital, paragraph).

[https://github.com/impactcentre/ocrevalUAtion/blob/master/src/main/java/eu/digitisation/Page/TextRegion.java](Page.TextRegion)
A TextRegion in a PAGE-XML document.

[https://github.com/impactcentre/ocrevalUAtion/blob/master/src/main/java/eu/digitisation/Page/Viewer.java](Page.Viewer)
Shows text regions (as stored in PAGE XML) on image.

[https://github.com/impactcentre/ocrevalUAtion/blob/master/src/main/java/eu/digitisation/xml/DocumentBuilder.java](xml.DocumentBuilder)
A builder and parser for XML documents.

[https://github.com/impactcentre/ocrevalUAtion/blob/master/src/main/java/eu/digitisation/xml/DocumentWriter.java](xml.DocumentWriter)
Writes XML document to String or File.

[https://github.com/impactcentre/ocrevalUAtion/blob/master/src/main/java/eu/digitisation/xml/Elements.java](xml.Elements)
Auxiliary functions to access and modify elements in a document.

