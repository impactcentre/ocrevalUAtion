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

import eu.digitisation.xml.DocumentBuilder;
import eu.digitisation.xml.Elements;
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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Creates a StringBuilder with the (normalized) textual content in a file.
 * Normalization collapses whitespaces and prefers composed form (see
 * java.text.Normalizer.Form) For PAGE XML files it selects only those elements
 * listed in a properties file.
 *
 * @author R.C.C.
 */
public class TextContent {

    StringBuilder builder;
    static final int maxlen;
    static final Set<String> types;

    static {
        types = new HashSet<>();
        Properties prop = new Properties();
        try (InputStream in = TextContent.class.getResourceAsStream("/General.properties")) {
            prop.load(in);
        } catch (IOException ex) {
            Logger.getLogger(TextContent.class.getName()).log(Level.SEVERE, null, ex);
        }
        maxlen = Integer.parseInt(prop.getProperty("maxlen"));
        String s = prop.getProperty("TextRegionTypes");
        String separator = ",\\p{Space}+";
        types.addAll(Arrays.asList(s.trim().split(separator)));
    }

    /**
     * Create TextContent from file
     *
     * @param file the input file
     * @param encoding the text encoding
     * @param filter optional CharFilter (can be null)
     * @throws java.io.IOException
     */
    public TextContent(File file, String encoding, CharFilter filter)
            throws IOException {
        FileType type = FileType.valueOf(file);
        builder = new StringBuilder();

        switch (type) {
            case PAGE:
                readPageFile(file, encoding, filter);
                break;
            case TXT:
                readTextFile(file, encoding, filter);
                break;
            default:
                throw new IOException("Unsupported file format " + type);
        }
    }

    /**
     * Add content after normalization and filtering
     *
     * @param s input text
     * @param filter optional filter (can be nul)
     */
    private void add(String s, CharFilter filter) {
        String reduced = StringNormalizer.reduceWS(s);
        if (reduced.length() > 0) {
            String canonical = StringNormalizer.canonical(s);

            if (builder.length() > 0) {
                builder.append(' ');
            }
            if (filter == null) {
                builder.append(canonical);
            } else {
                builder.append(filter.translate(canonical));
            }
            if (builder.length() > maxlen) {
                throw new RuntimeException("Input text length limited to "
                        + maxlen + " characters");
            }
        }
    }

    /**
     * Get the region type: if the attribute is not available then return unknown type
     * @param region
     * @return 
     */
    private String getType(Node region) {
        String type = Elements.getAttribute(region, "type");
        if(type ==null) {
            type = "unknown";
        } 
        return type;
    }
    private void readPageFile(File file, String encoding, CharFilter filter) {
        Document doc = DocumentBuilder.parse(file);
        NodeList regions = doc.getElementsByTagName("TextRegion");

        // TBD: check if encoding is consistent with XML PI
        builder = new StringBuilder();

        for (int r = 0; r < regions.getLength(); ++r) {
            Node region = regions.item(r);
            String type = getType(region);
            if (types.contains(type)) {
                NodeList nodes = region.getChildNodes();
                for (int n = 0; n < nodes.getLength(); ++n) {
                    Node node = nodes.item(n);
                    if (node.getNodeName().equals("TextEquiv")) {
                        String text = node.getTextContent();
                        add(text, filter);
                    }
                }
            }
        }
        builder.trimToSize();
    }

    /**
     * Return textual content and collapse whitespace: contiguous spaces are
     * considered a single one
     *
     * @param file the input text file
     * @param encoding the text file encoding
     * @param filter optional CharFilter
     * @return String as StringBuilder
     * @throws IOException
     */
    private void readTextFile(File file, String encoding, CharFilter filter) {
        try {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis, encoding);
            BufferedReader reader = new BufferedReader(isr);
            int size = 0;

            while (reader.ready()) {
                add(reader.readLine(), filter);
            }
        } catch (IOException ex) {
            Logger.getLogger(TextContent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @return the text a String
     */
    @Override
    public String toString() {
        return builder.toString();
    }
    
    /**
     * 
     * @return the length of the stored text
     */
    public int length () {
        return builder.length();
    }
}
