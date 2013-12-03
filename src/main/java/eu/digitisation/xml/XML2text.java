/*
 * Copyright (C) 2013 Universidad de Alicante
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package eu.digitisation.xml;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import javax.xml.parsers.*;

/**
 * Removes markup, declarations, PI's and comments from XML files. Implemented
 * as a SAX parser.
 */
public class XML2text extends DefaultHandler {

    private StringBuilder buffer;
    private static final FilenameFilter filter = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
            return name.endsWith(".xml");
        }
    };

    @Override
    public void characters(char[] c, int start, int length) {
        if (length > 0) {
            buffer.append(c, start, length);
        }
    }

    @Override
    public void startElement(String uri, String localName,
            String tag, Attributes attributes) {
        buffer.append(" ");
    }

    @Override
    public void endElement(String uri, String localName, String tag) {
        buffer.append(" ");
    }

    private XMLReader getXMLReader() {
        XMLReader reader = null;
        try {
            reader = SAXParserFactory.newInstance()
                    .newSAXParser().getXMLReader();
            reader.setContentHandler(this);

        } catch (SAXException ex) {
            Logger.getLogger(XML2text.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(XML2text.class.getName()).log(Level.SEVERE, null, ex);
        }
        return reader;
    }

    /**
     * Read file and return text content.
     *
     * @param fileName the name of the file.
     * @return text in file without markup.
     */
    public String getText(String fileName) {
        XMLReader reader = getXMLReader();
        buffer = new StringBuilder(10000);
        try {
            reader.parse(fileName);
        } catch (IOException ex) {
            Logger.getLogger(XML2text.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(XML2text.class.getName()).log(Level.SEVERE, null, ex);
        }
        return buffer.toString();
    }

    /**
     * Main function
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        XML2text xml = new XML2text();
        String outDir = null;

        if (args.length == 0) {
            System.err.println("usage: XML2text [-d outdir]"
                    + "file1.xml file2.xml...");
        }
        for (int n = 0; n < args.length; ++n) {
            String arg = args[n];

            if (arg.equals("-d")) {
                outDir = args[++n];
                boolean test = new File(outDir).mkdir();
            } else if (arg.endsWith(".xml")) {
                File infile = new File(arg);
                String outfileName = arg.replace(".xml", ".txt");
                File outfile = new File(outDir, outfileName);
                if (outfile.exists()) {
                    System.err.println(outfileName + "already exists ");
                } else {
                    BufferedWriter writer = 
                            new BufferedWriter(new FileWriter(outfileName));
                    writer.write(xml.getText(arg));
                }
            }
        }
    }
}
