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
package eu.digitisation.Page;

import tmp.XMLDocument;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Transform PAGE-XML to a flat and sorted XML document
 * @version 2012.06.20
 *
 * Remark: Since the DTD can be absent, the id attribute is not recognized as a
 * DOM attribute of type id and therefore, the full list of text regions must be
 * traversed to look for a particular id.
 */
public class PAGE {
    // Selected text-region types (space separated tags)
    final static String TextRegionTypes =
            "TOC-entry heading drop-capital paragraph";
    final static String lsep = System.getProperty("line.separator");

    /**
     * Get reading order (PAGE-XML text regions do not necessarily appear in the
     * reading order).
     *
     * @param doc a Document
     * @return List with the identifiers of text regions in reading order
     */
    private static ArrayList<String> getReadingOrder(XMLDocument doc) {
        ArrayList<String> reading = new ArrayList<String>();
        NodeList refs =
                doc.getDocument().getElementsByTagName("RegionRefIndexed");
        for (int n = 0; n < refs.getLength(); ++n) {
            Node node = refs.item(n);
            NamedNodeMap atts = node.getAttributes();
            String ref = atts.getNamedItem("regionRef").getNodeValue();
            String ind = atts.getNamedItem("index").getNodeValue();
            int pos = Integer.parseInt(ind);
            if (pos == reading.size()) {
                reading.add(ref);
            } else {
                System.err.println("Reading order is not consecutive");
            }
        }
        return reading;
    }

    /**
     * Get textual content from selected text-region
     *
     * @param node An XML node
     * @return the textual content under Unicode and PlainText nodes
     */
    private static String getTextContent(Node node) {
        NodeList children = node.getChildNodes();
        StringBuilder buff = new StringBuilder();
        for (int n = 0; n < children.getLength(); ++n) {
            Node child = children.item(n);
            String name = child.getNodeName();
            if (name.equals("Unicode") || name.equals("PlainText")) {
                buff.append(node.getNodeValue());
            }
        }
        return buff.toString();
    }

    /**
     * Retrieve textual content associated with region identifiers.
     *
     * @param doc a PAGE document
     * @return A mapping between text region identifiers and their content.
     */
    private static HashMap<String, String> getTextContent(XMLDocument doc) {
        HashMap<String, String> content = new HashMap<String, String>();
        NodeList regions = doc.getDocument().getElementsByTagName("TextRegion");
        for (int n = 0; n < regions.getLength(); ++n) {
            Node node = regions.item(n);
            NamedNodeMap atts = node.getAttributes();
            String type = atts.getNamedItem("type").getNodeValue();
            String id = atts.getNamedItem("id").getNodeValue();

            if (TextRegionTypes == null
                    || TextRegionTypes.contains(type)) {
                String text = node.getTextContent().trim();
                content.put(id, text);
            }
        }
        return content;
    }

    /**
     * Retrieve text region type associated with region identifiers.
     *
     * @param doc a PAGE document
     * @return A mapping between text region identifiers and text region types
     */
    public static HashMap<String, String> getRegionType(XMLDocument doc) {
        NodeList nodes = doc.getDocument().getElementsByTagName("TextRegion");
        HashMap<String, String> types = new HashMap<String, String>();

        for (int n = 0; n < nodes.getLength(); ++n) {
            Node node = nodes.item(n);
            NamedNodeMap atts = node.getAttributes();
            String type = atts.getNamedItem("type").getNodeValue();
            String id = atts.getNamedItem("id").getNodeValue();

            types.put(id, type);
        }
        return types;
    }

    /**
     * Transform the PAGE-XML document into an XML document where content
     * follows the reading order specified by PAGE element ReadingOrder.
     *
     * @param file The file containing the PAGE-XML
     * @return the output document
     */
    public static XMLDocument transform(File file) {
        XMLDocument doc = new XMLDocument(file);
        ArrayList<String> reading = getReadingOrder(doc);
        HashMap<String, String> content = getTextContent(doc);
        HashMap<String, String> types = getRegionType(doc);
        boolean dcap = false;
        String text = null;
        XMLDocument output = new XMLDocument("Flat_PAGE");

        for (String id : reading) {
            String type = types.get(id);
            if (TextRegionTypes != null
                    && TextRegionTypes.contains(type)) {
                if (dcap) {
                    System.out.println(text + " "
                            + content.get(id).substring(0, 20));
                }
                text = content.get(id);
                if (text == null) {
                    System.err.println("Empty text at region "
                            + id + " of type " + type);
                }
                dcap = type.equals("drop-capital");
                org.w3c.dom.Element element = output.addElement(type);
                assert text != null;
                for (String line : text.split(lsep)) {
                    output.addTextElement(element, "line", line + "\n");
                }
            }
        }

        return output;
    }

    /**
     * Transform a list of PAGE-XML document into a single XML document where
     * content follows the reading order specified by PAGE element ReadingOrder.
     *
     * Remark: Current version only creates p elements.
     *
     * @param files The PAGE-XML files
     * @return the output document
     */
    public static XMLDocument transform(ArrayList<File> files) {
        XMLDocument output = new XMLDocument("FlatPAGE");
        for (File file : files) {
            XMLDocument doc = new XMLDocument(file);
            ArrayList<String> reading = getReadingOrder(doc);
            HashMap<String, String> content = getTextContent(doc);
            HashMap<String, String> types = getRegionType(doc);
            boolean dcap = false;
            String text = null;

            for (String id : reading) {
                String type = types.get(id);
                if (TextRegionTypes != null
                        && TextRegionTypes.contains(type)) {
                    if (dcap) {
                        System.out.println(text + " "
                                + content.get(id).substring(0, 20));
                    }
                    text = content.get(id);
                    if (text == null) {
                        System.err.println("Empty text at region "
                                + id + " of type " + type);
                    }
                    dcap = type.equals("drop-capital");
                    org.w3c.dom.Element element = output.addElement(type);
                    assert text != null;
                    for (String line : text.split(lsep)) {
                        output.addTextElement(element, "line", line + "\n");
                    }
                }
            }
        }

        return output;
    }

    /**
     * Transform a single file and dump it to an output file
     * @param infile the input file
     * @param outfile the output file
     */
    public static void transform(File infile, File outfile) {
        XMLDocument doc = transform(infile);
        doc.write(outfile);
    }

/**
 * Transform PAGE documents into a single flat sorted XML document
 */
public static void main(String[] args) {
        String output = null;
        ArrayList<File> files = new ArrayList<File>();

        if (args.length == 0) {
            System.err.println("Usage:\t PAGE [-o output_file] file1 file2 ....");
        }

        for (int n = 0; n < args.length; ++n) {
            String arg = args[n];
            if (arg.equals("-o")) {
                output = args[++n];
            } else {
                files.add(new File(arg));
            }
        }

        try {
            XMLDocument doc = PAGE.transform(files);
            if (output == null) {
                System.out.println(doc);
            } else {
                File outfile = new File(output);
                doc.write(outfile);
            }
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }
}
