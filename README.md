ocrevalUAtion [![Build Status](https://secure.travis-ci.org/impactcentre/ocrevalUAtion.png?branch=master)](http://travis-ci.org/impactcentre/ocrevalUAtion)
=============

This set of classes provides basic support to perform the comparison of
two text files: a reference file (a ground-truth document, usually in
PAGE XML format) and a the output from an OCR engine (a text file).

You can download the latest release from [here](https://bintray.com/impactocr/impactocr-maven/ocrevalUAtion).

Instructions on how to use ocrevalUAtion can be found in the [wiki](https://github.com/impactcentre/ocrevalUAtion/wiki).

The classes defined are listed below:

[distance.ArrayEditDistance<Type>](https://github.com/impactcentre/ocrevalUAtion/blob/master/src/main/java/eu/digitisation/distance/ArrayEditDistance.java)<br>
Provides a basic implementations of some popular edit distance methods
(currently, Levenshtein and indel) applied to arrays of objects.

[distance.BagOfWords](https://github.com/impactcentre/ocrevalUAtion/blob/master/src/main/java/eu/digitisation/distance/BagOfWords.java)<br>
Computes distances between two bags of words (order independent distance).

[distance.EditTable]
Compact storage for a large table containing four basic edit operations.

[distance.StringEditDistance](https://github.com/impactcentre/ocrevalUAtion/blob/master/src/main/java/eu/digitisation/distance/StringEditDistance.java)<br>
Provides basic implementations of some popular edit distance methods 
operating on strings (currently, Levenshtein, Damerau-Levenshtein, and indel).

[distance.TextFileEncoder](https://github.com/impactcentre/ocrevalUAtion/blob/master/src/main/java/eu/digitisation/distance/TextFileEncoder.java)<br>
Encode a text file as an array of Integers (one code per word).

[io.CharFilter](https://github.com/impactcentre/ocrevalUAtion/blob/master/src/main/java/eu/digitisation/io/CharFilter.java)<br>
Transform text according to a mapping between (source, target) 
Unicode character sequences.

[io.StringNormalizer]
Normalizes strings: collapse whitespace and use composed form (see java.text.Normalizer.Form)

[io.TextContent](https://github.com/impactcentre/ocrevalUAtion/blob/master/src/main/java/eu/digitisation/io/TextContent.java)<br>
Reads and normalizes text from file content, 
and optionally applies a CharFilter. Now, it supports text files and PAGE XML files (selects only those
elements listed in a properties file, TOC-entry, heading,
drop-capital, paragraph).

[io.UnicodeReader](https://github.com/impactcentre/ocrevalUAtion/blob/master/src/main/java/eu/digitisation/io/UnicodeReader.java)<br>
Transformations between Unicode strings and codepoints.

[io.WordScanner](https://github.com/impactcentre/ocrevalUAtion/blob/master/src/main/java/eu/digitisation/io/WordScanner.java)<br>
A simple and fast text scanner that reads words 
from a File, String or InputStream which reads words from a file and
performs the tokenization oriented by information-retrieval
requirements.

[math.ArrayMath](https://github.com/impactcentre/ocrevalUAtion/blob/master/src/main/java/eu/digitisation/math/ArrayMath.java)<br>
Standard operations on arrays: sum, average, max, min, standard deviation.

[math.Counter<T>](https://github.com/impactcentre/ocrevalUAtion/blob/master/src/main/java/eu/digitisation/math/Counter.java)<br>
Counts the number of different objects, a map between
objects and integers which can be incremented and decremented.

[math.BiCounter<T>](https://github.com/impactcentre/ocrevalUAtion/blob/master/src/main/java/eu/digitisation/math/BiCounter.java)<br>
Counts the number of different pairs of objects, a map between
pairs of objects and integers which can be incremented and decremented.

[math.Pair](https://github.com/impactcentre/ocrevalUAtion/blob/master/src/main/java/eu/digitisation/math/Pair.java)<br>
A pair of objects.

[ocr.ErrorMeasure](https://github.com/impactcentre/ocrevalUAtion/blob/master/src/main/java/eu/digitisation/ocr/ErrorMeasure.java)<br>
The main class which computes character and word error rates.

[Page.Geometry](https://github.com/impactcentre/ocrevalUAtion/blob/master/src/main/java/eu/digitisation/Page/Geometry.java)<br>
Geometry information contained in one PAGE-XML file.

[Page.Sort](https://github.com/impactcentre/ocrevalUAtion/blob/master/src/main/java/eu/digitisation/Page/Sort.java)<br>
PAGE-XML regions order in the document can differ form reading order. 
This class makes the order of elements in the document consistent 
with the reading order stored therein.


[Page.TextRegion](https://github.com/impactcentre/ocrevalUAtion/blob/master/src/main/java/eu/digitisation/Page/TextRegion.java)<br>
A TextRegion in a PAGE-XML document.

[Page.Viewer](https://github.com/impactcentre/ocrevalUAtion/blob/master/src/main/java/eu/digitisation/Page/Viewer.java)<br>
Shows text regions (as stored in PAGE XML) on image.

[xml.DocumentBuilder](https://github.com/impactcentre/ocrevalUAtion/blob/master/src/main/java/eu/digitisation/xml/DocumentBuilder.java)<br>
A builder and parser for XML documents.

[xml.DocumentWriter](https://github.com/impactcentre/ocrevalUAtion/blob/master/src/main/java/eu/digitisation/xml/DocumentWriter.java)<br>
Writes XML document to String or File.

[xml.Elements](https://github.com/impactcentre/ocrevalUAtion/blob/master/src/main/java/eu/digitisation/xml/Elements.java)<br>
Auxiliary functions to access and modify elements in a document.

