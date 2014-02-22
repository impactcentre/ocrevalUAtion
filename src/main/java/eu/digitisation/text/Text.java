/*
 * Copyright (C) 2013  Universidad de Alicante
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
package eu.digitisation.text;

import eu.digitisation.input.FileType;
import eu.digitisation.input.StartUp;
import eu.digitisation.input.WarningException;
import eu.digitisation.layout.SortPageXML;
import eu.digitisation.xml.DocumentParser;
import eu.digitisation.xml.ElementList;
import eu.digitisation.xml.XPathFilter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.xpath.XPathExpressionException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Extracts the text content in a file. Normalization collapses white-spaces and
 * prefers composed form (see java.text.Normalizer.Form). For XML files,
 * filtering options can be provided
 *
 * @author R.C.C.
 */
public class Text {

    StringBuilder builder;
    static final int maxlen;
    String encoding;
    XPathFilter filter;

    static {
        Properties props = StartUp.properties();
        maxlen = Integer.parseInt(props.getProperty("maxlen", "10000").trim());
        System.out.println("max length of text set to " + maxlen);
        try {
            File inclusions = new File("inclusions.txt");
            File exclusions = new File("exclusions.txt");
            XPathFilter filter = inclusions.exists()
                    ? new XPathFilter(inclusions, exclusions)
                    : null;
        } catch (IOException ex) {
            Logger.getLogger(Text.class.getName()).log(Level.SEVERE, null, ex);
        } catch (XPathExpressionException ex) {
            Logger.getLogger(Text.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Create TextContent from file
     *
     * @param file the input file
     * @param encoding the text encoding for text files (optional; can be null)
     * @param filter XPAthFilter for XML files (extracts textual content from
     * selected elements)
     * @throws eu.digitisation.input.WarningException
     */
    public Text(File file, String encoding, XPathFilter filter)
            throws WarningException {

        builder = new StringBuilder();
        this.encoding = encoding;
        this.filter = filter;

        try {
            FileType type = FileType.valueOf(file);
            switch (type) {
                case PAGE:
                    readPageFile(file);
                    break;
                case TEXT:
                    readTextFile(file);
                    break;
                case FR10:
                    readFR10File(file);
                    break;
                case HOCR:
                    readHOCRFile(file);
                    break;
                case ALTO:
                    readALTOFile(file);
                    break;
                default:
                    throw new WarningException("Unsupported file format ("
                            + type + " format) for file "
                            + file.getName());
            }
        } catch (IOException ex) {
            Logger.getLogger(Text.class.getName()).log(Level.SEVERE, null, ex);
        }
        builder.trimToSize();
    }

    /**
     * Create Text from file
     *
     * @param file the input file
     * @throws eu.digitisation.io.WarningException
     */
    public Text(File file)
            throws WarningException {
        this(file, null, null);
    }

    /**
     * Constructor only for debugging purposes
     *
     * @param s
     * @throws eu.digitisation.io.WarningException
     */
    public Text(String s) throws WarningException {
        builder = new StringBuilder();
        encoding = "utf8";
        add(s);
    }

    /**
     * The length of the stored text
     *
     * @return the length of the stored text
     */
    public int length() {
        return builder.length();
    }

    /**
     * The content as a string
     *
     * @return the text a String
     */
    @Override
    public String toString() {
        return builder.toString();
    }

    /**
     * The content as a string
     * @param filter a CharFilter
     * @return the text after the application of the filter
     */
    public String toString(CharFilter filter) {
        return filter == null 
                ? builder.toString()
                : filter.translate(builder.toString());
    }

    /**
     * Add content after normalization of whitespace and composition of
     * diacritics
     *
     * @param s input text
     */
    private void add(String s) throws WarningException {
        String reduced = StringNormalizer.reduceWS(s);
        if (reduced.length() > 0) {
            String canonical = StringNormalizer.composed(reduced);
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(canonical);
            if (builder.length() > maxlen) {
                throw new WarningException("Text length limited to "
                        + maxlen + " characters");
            }
        }
    }

    private Document loadXMLFile(File file) {
        Document doc = DocumentParser.parse(file);
        String xmlEncoding = doc.getXmlEncoding();
        if (xmlEncoding != null) {
            encoding = xmlEncoding;
            System.err.println("XML file " + file.getName() + " encoding is " + encoding);
        } else {
            if (encoding == null) {
                encoding = Encoding.detect(file);
            }
            System.err.println("No encoding declaration in "
                    + file + ". Using " + encoding);
        }
        return doc;
    }

    /**
     * Read textual content and collapse whitespace: contiguous spaces are
     * considered a single one
     *
     * @param file the input text file
     */
    private void readTextFile(File file) throws WarningException {
        // guess encoding if none is provided
        if (encoding == null) {
            encoding = Encoding.detect(file);
        }
        System.err.println("Text file " + file.getName() + " encoding is " + encoding);

        // read content
        try {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis, encoding);
            BufferedReader reader = new BufferedReader(isr);

            while (reader.ready()) {
                add(reader.readLine().trim());
            }
        } catch (IOException ex) {
            Logger.getLogger(Text.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Reads textual content in a PAGE element of type TextRegion
     *
     * @param region the TextRegion element
     */
    private void readPageTextRegion(Element region) throws IOException, WarningException {
        NodeList nodes = region.getChildNodes();
        for (int n = 0; n < nodes.getLength(); ++n) {
            Node node = nodes.item(n);
            if (node.getNodeName().equals("TextEquiv")) {
                String text = node.getTextContent();
                add(text);
            }
        }
    }

    /**
     * Reads textual content in PAGE XML document. By default selects all
     * TextREgion elements
     *
     * @param file the input XML file
     */
    private void readPageFile(File file) throws IOException, WarningException {
        Document doc = loadXMLFile(file);
        Document sorted = SortPageXML.isSorted(doc) ? doc : SortPageXML.sorted(doc);
        List<Element> regions = (filter == null)
                ? new ElementList(sorted.getElementsByTagName("TextRegion"))
                : filter.selectElements(sorted);

        for (int r = 0; r < regions.size(); ++r) {
            Element region = regions.get(r);
            readPageTextRegion(region);
        }
    }

    /**
     * Reads textual content from FR10 XML paragraph
     *
     * @param oar the paragraph (par) element
     */
    private void readFR10Par(Element par) throws WarningException {
        NodeList lines = par.getElementsByTagName("line");
        for (int nline = 0; nline < lines.getLength(); ++nline) {
            Element line = (Element) lines.item(nline);
            StringBuilder text = new StringBuilder();
            NodeList formattings = line.getElementsByTagName("formatting");
            for (int nform = 0; nform < formattings.getLength(); ++nform) {
                Element formatting = (Element) formattings.item(nform);
                NodeList charParams = formatting.getElementsByTagName("charParams");
                for (int nchar = 0; nchar < charParams.getLength(); ++nchar) {
                    Element charParam = (Element) charParams.item(nchar);
                    String content = charParam.getTextContent();
                    if (content.length() > 0) {
                        text.append(content);
                    } else {
                        text.append(' ');
                    }
                }
            }
            add(text.toString());
        }
    }

    /**
     * Reads textual content from FR10 XML file
     *
     * @param file the input XML file
     */
    private void readFR10File(File file) throws WarningException {
        Document doc = loadXMLFile(file);

        List<Element> pars = (filter == null)
                ? new ElementList(doc.getElementsByTagName("par"))
                : filter.selectElements(doc);

        for (int npar = 0; npar < pars.size(); ++npar) {
            Element par = pars.get(npar);
            readFR10Par(par);
        }
    }

    /**
     * Reads textual content from HOCR HTML file
     *
     * @param file the input HTML file
     */
    private void readHOCRFile(File file) throws WarningException {
        try {
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse(file, null);
            String htmlEncoding = doc.outputSettings().charset().toString();

            if (htmlEncoding == null) {
                encoding = Encoding.detect(file);
                System.err.println("No charset declaration in "
                        + file + ". Using " + encoding);
            } else {
                encoding = htmlEncoding;
                System.err.println("HTML file " + file
                        + " encoding is " + encoding);
            }

            for (org.jsoup.nodes.Element e
                    : doc.body().select("*[class=ocr_line")) {
                String text = e.text();
                add(text);

            }
        } catch (IOException ex) {
            Logger.getLogger(Text.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Reads textual content in ALTO XML element of type TextLine
     *
     * @param file the input ALTO file
     */
    private void readALTOTextLine(Element line) throws WarningException {
        NodeList strings = line.getElementsByTagName("String");

        for (int nstring = 0; nstring < strings.getLength(); ++nstring) {
            Element string = (Element) strings.item(nstring);
            String text = string.getAttribute("CONTENT");
            add(text);
        }

    }

    /**
     * Reads textual content from ALTO XML file
     *
     * @param file the input ALTO file
     */
    private void readALTOFile(File file) throws WarningException {
        Document doc = loadXMLFile(file);
        NodeList lines = doc.getElementsByTagName("TextLine");

        for (int nline = 0; nline < lines.getLength(); ++nline) {
            Element line = (Element) lines.item(nline);
            readALTOTextLine(line);
        }
    }

    /**
     * Extract text content (under the filtered elements)
     *
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException, WarningException, XPathExpressionException {
        if (args.length < 1 | args[0].equals("-h")) {
            System.err.println("usage: Text xmlfile [xpathfile] [xpathfile]");
        } else {
            File xmlfile = new File(args[0]);
            File inclusions = args.length > 1 ? new File(args[1]) : null;
            File exclusions = args.length > 2 ? new File(args[2]) : null;
            XPathFilter filter = (inclusions == null)
                    ? null
                    : new XPathFilter(inclusions, exclusions);

            Text text = new Text(xmlfile, null, filter);
            System.out.println(text);
        }
    }
}
