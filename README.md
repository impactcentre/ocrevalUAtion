ocrevalUAtion [![Build Status](https://secure.travis-ci.org/impactcentre/ocrevalUAtion.png?branch=master)](http://travis-ci.org/impactcentre/ocrevalUAtion)
=============

This set of classes provides basic support to perform the comparison of
two text files: a reference file (a ground-truth document, usually in
PAGE XML format) and a the output from an OCR engine (a text file).

You can build an executable jar by using Maven and running
'mvn package'.

The classes defined are listed below:

**xml.XMLDocument**

This auxiliary class supports the opening and writing of XML files as
well as a number of basic additions of content to the XML file.

**xml.XML2Text**

Dumps the text content of an .xml file into a .txt file

**xml.PAGE**

Transforms PAGE-XML into a flat and sorted (according to reading
order) XML document. The current version selects TOC-entry, heading,
drop-capital, and paragraph elements; next version will read the
elements selected by default from a properties file.

**ocr.StringEditDistance**

Provides a very basic implementation of some popular edit distance
methods (currently, Levenshtein and indel) between two strings.
 
**ocr.ArrayEditDistance**

Provides a very basic implementation of some popular edit distance
methods (currently, Levenshtein and indel) between arrays of objects
(for example, Integers).

**ocr.FileEncoder**

Encode a file as an array of Integers (one per word) to allow faster
distance computations.

**ocr.WordScanner**

A simple and fast text scanner which reads words from a file and
performs the tokenization oriented by information-retrieval
requirements.

**ocr.Measure**

Computes character error rate (CER) and word error rate (WER) for two
text files.

**text.FileFilter**

Auxiliary class which performs statistical analysis of unicode files
and substitution of unicode sequences (for example, in case of
character not supported by the browser).

**text.UnicodeReader**

Auxiliary class that reads and interprets unicode files, for example,
to check if non-printable content is correct.

**util.Counter**

Auxiliary class implementing a simple counter, that is, a map between
objects and integers which can be incremented and decremented.

**util.MiniBrowser**

A deprecated class only for testing purposes (for example, display the
output).