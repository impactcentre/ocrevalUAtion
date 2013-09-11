/**
 * Copyright (C) 2012 Rafael C. Carrasco
 * This code can be distributed or modified
 * under the terms of the GNU General Public License V3.
 */
package eu.digitisation.xml;

import java.io.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import javax.xml.parsers.*;

/**
 * Removes markup, declarations, PI's and comments from XML files.
 * Implemented as a SAX parser
 */
public class XML2text extends DefaultHandler {
    private StringBuilder buffer;
    private static FilenameFilter filter = new FilenameFilter() {
            @Override
	    public boolean accept(File dir, String name) {
		return name.endsWith(".xml");
	    }
	};

    @Override
    public void characters(char[] c, int start, int length) {
	if (length > 0) {
	    try {
		buffer.append(c, start, length);
	    } catch (java.nio.BufferOverflowException x) {
		System.err.println("Not enough text buffer size");
		System.exit(1);
	    }
	}
    }

    @Override
    public void startElement(String uri, String localName,
			      String tag, Attributes attributes) {
	//   if (tag.equals("p") || tag.equals("note") || tag.equals("sp")) 
	buffer.append(" ");
    }

    @Override
    public void endElement(String uri, String localName, String tag) { 
	//   if (tag.equals("p") || tag.equals("note")) 
	buffer.append(" ");
    }
    
    private XMLReader getXMLReader() {
	XMLReader reader = null;
	try {
	    reader = SAXParserFactory.newInstance()
		.newSAXParser().getXMLReader();
	    reader.setContentHandler(this);
	} catch (Exception x) {
	    System.err.println(x.getMessage());
	}
	return reader;
    }
    
    /**
     * Read file and return text content.
     * @param fileName the name of the file.
     * @return text in file without markup.
     */
    public String getText(String fileName) {
	XMLReader reader = getXMLReader();
	buffer = new StringBuilder(10000);
	try {
	    reader.parse(fileName);
	} catch (Exception x) {
	    System.err.println("Error parsing " + fileName +
				": " + x.getMessage());
	}
	return buffer.toString();
    }
    
    public static void main(String[] args) throws IOException {
	XML2text xml = new XML2text();
	String outDir = null;

	if (args.length == 0) {
	    System.err.println("usage: java XML2text [-d outdir]"
			       + "file1.xml file2.xml...");
	}
	for (int n = 0; n < args.length; ++n) {
	    String arg = args[n];
	    
	    if (arg.equals("-d")) {
		outDir = args[++n];
		boolean test = new File(outDir).mkdir();
	    } else {
		File infile = new File(arg);
		String outfileName =  arg.replace(".xml", ".txt");
		File outfile = new File(outDir, outfileName);
		if (outfile.exists()) {
		    System.err.println(outfileName + "already exists ");
		} else {
		    BufferedWriter writer = 
			new BufferedWriter(new FileWriter(outfileName));
		    writer.write(xml.getText(arg)); 
		    writer.close();
		}
	    }
	}	
    }
}
