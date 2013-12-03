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
package eu.digitisation.io;

import eu.digitisation.xml.DocumentParser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Creates a StringBuilder with the (normalized) textual content in a file.
 * Normalization collapses white-spaces and prefers composed form (see
 * java.text.Normalizer.Form) For PAGE XML files it selects only those elements
 * listed in a properties file.
 *
 * @author R.C.C.
 */
public final class TextContent {

    StringBuilder builder;
    String encoding;
    CharFilter filter;
    static final int maxlen;
    static final String defaultEncoding;
    static final Set<String> types;

    static {
        Properties props = new Properties();
        try {
            InputStream in = 
                    TextContent.class.getResourceAsStream("/General.properties");

            props.load(in);
        } catch (IOException ex) {
            Logger.getLogger(TextContent.class.getName()).log(Level.SEVERE, null, ex);
        }

        maxlen = Integer.parseInt(props.getProperty("maxlen", "10000"));

        defaultEncoding = props.getProperty("defaultEncoding",
                System.getProperty("file.encoding").trim());

        types = new HashSet<String>();

        String typesProp = props.getProperty("PAGE.TextRegionTypes");
        String separator = ",\\p{Space}+";
        if (typesProp != null) {
            types.addAll(Arrays.asList(typesProp.trim().split(separator)));
        }
    }

    /**
     * Create TextContent from file
     *
     * @param file the input file
     * @param encoding the text encoding
     * @param filter optional CharFilter (can be null)
     */
    public TextContent(File file, String encoding, CharFilter filter) {
        FileType type = FileType.valueOf(file);

        builder = new StringBuilder();
        this.encoding = (encoding == null) ? defaultEncoding : encoding;
        this.filter = filter;
        try {
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
                    readALTOfile(file);
                    break;
                default:
                    throw new IOException("Unsupported file format " + type);
            }
        } catch (IOException ex) {
            Logger.getLogger(TextContent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Constructor only for debugging purposes
     *
     * @param s
     * @param filter
     */
    public TextContent(String s, CharFilter filter) {
        builder = new StringBuilder();
        encoding = defaultEncoding;
        this.filter = filter;
        add(s, true);
    }

    /**
     * The length of the stored text
     * @return the length of the stored text
     */
    public int length() {
        return builder.length();
    }

    /**
     * The content as a string
     * @return the text a String
     */
    @Override
    public String toString() {
        return builder.toString();
    }

    /**
     * Add content after normalization and filtering
     *
     * @param s input text
     * @param pad true if space must be inserted between consecutive additions
     */
    private void add(String s, boolean pad) {
        String filtered = (filter == null)
                ? s : filter.translate(s);
        String reduced = StringNormalizer.reduceWS(filtered);
        if (reduced.length() > 0) {
            String canonical = StringNormalizer.canonical(reduced);
            if (pad && builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(canonical);
            if (builder.length() > maxlen) {
                throw new RuntimeException("Text length limited to "
                        + maxlen + " characters");
            }
        }
    }

    /**
     * Get the region type: if the attribute is not available then return
     * unknown type
     *
     * @param region
     * @return the region type as specified by the type attribute
     */
    private String getType(Element region) {
        String type = region.getAttribute("type");
        if (type.isEmpty()) {
            type = "unknown";
        }
        return type;
    }

    /**
     * Read textual content and collapse whitespace: contiguous spaces are
     * considered a single one
     *
     * @param file the input text file
     */
    protected void readTextFile(File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis, encoding);
            BufferedReader reader = new BufferedReader(isr);

            while (reader.ready()) {
                add(reader.readLine(), true);
            }
        } catch (IOException ex) {
            Logger.getLogger(TextContent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Reads textual content and collapse whitespace: contiguous spaces are
     * considered a single one
     *
     * @param file the input XML file
     */
    protected void readPageFile(File file) {
        Document doc = DocumentParser.parse(file);
        String xmlEncoding = doc.getXmlEncoding();
        NodeList regions = doc.getElementsByTagName("TextRegion");

        if (xmlEncoding != null) {
            encoding = xmlEncoding;
            System.err.println("XML file " + file + " encoding is " + encoding);
        } else {
            System.err.println("No encoding declaration in "
                    + file + ". Using " + encoding);
        }

        for (int r = 0; r < regions.getLength(); ++r) {
            Element region = (Element) regions.item(r);
            String type = getType(region);
            if (type == null || types.isEmpty()
                    || types.contains(type)) {
                NodeList nodes = region.getChildNodes();
                for (int n = 0; n < nodes.getLength(); ++n) {
                    Node node = nodes.item(n);
                    if (node.getNodeName().equals("TextEquiv")) {
                        String text = node.getTextContent();
                        add(text, true);
                    }
                }
            }
        }
        builder.trimToSize();
    }

    /**
     * Reads textual content from FR10 XML file
     *
     * @param file the input XML file
     */
    protected void readFR10File(File file) {
        Document doc = DocumentParser.parse(file);
        String xmlEncoding = doc.getXmlEncoding(); 
        NodeList pars = doc.getElementsByTagName("par");

        if (xmlEncoding != null) {
            encoding = xmlEncoding;
            System.err.println("XML file " + file + " encoding is " + encoding);
        } else {
            System.err.println("No encoding declaration in "
                    + file + ". Using " + encoding);
        }

        for (int npar = 0; npar < pars.getLength(); ++npar) {
            Element par = (Element) pars.item(npar);
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
                add(text.toString(), true);
            }
        }
        builder.trimToSize();
    }

    /**
     * Reads textual content from HOCR HTML file
     *
     * @param file the input HTML file
     */
    protected void readHOCRFile(File file) {
        try {
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse(file, null);
            String htmlEncoding = doc.outputSettings().charset().toString();

            if (htmlEncoding != null) {
                encoding = htmlEncoding;
                System.err.println("HTML file " + file
                        + " encoding is " + encoding);
            } else {
                System.err.println("No charset declaration in "
                        + file + ". Using " + encoding);
            }

            for (org.jsoup.nodes.Element e
                    : doc.body().select("*[class=ocr_line")) {
                String text = e.text();
                add(text, true);
            }
        } catch (IOException ex) {
            Logger.getLogger(TextContent.class.getName()).log(Level.SEVERE, null, ex);
        }
        builder.trimToSize();
    }

    /**
     * Reads textual content from HOCR HTML file
     *
     * @param file
     */
    protected void readALTOfile(File file) {
        Document doc = DocumentParser.parse(file);
        String xmlEncoding = doc.getXmlEncoding();
        NodeList lines = doc.getElementsByTagName("TextLine");

        if (xmlEncoding != null) {
            encoding = xmlEncoding;
            System.err.println("XML file " + file + " encoding is " + encoding);
        } else {
            System.err.println("No encoding declaration in "
                    + file + ". Using " + encoding);
        }

        for (int nline = 0; nline < lines.getLength(); ++nline) {
            Element line = (Element) lines.item(nline);
            NodeList strings = line.getElementsByTagName("String");
            for (int nstring = 0; nstring < strings.getLength(); ++nstring) {
                Element string = (Element) strings.item(nstring);
                String text = string.getAttribute("CONTENT");
                add(text, true);
            }
        }
        builder.trimToSize();
    }
}
