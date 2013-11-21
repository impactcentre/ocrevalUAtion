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
package eu.digitisation.Page;

import eu.digitisation.xml.DocumentBuilder;
import eu.digitisation.xml.Elements;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
 * Textual content in a PAGE XML file: selects only those elements listed in a
 * properties file
 *
 * @author R.C.C.
 */
public class TextContent {

    StringBuilder builder;
    static final Set<String> types;

    static {
        types = new HashSet<>();
        Properties prop = new Properties();
        try {
            FileReader reader = new FileReader("target/classes/General.properties");
            prop.load(reader);
            String s = prop.getProperty("TextRegionTypes");
            String separator = ",\\p{Space}+";
            types.addAll(Arrays.asList(s.trim().split(separator)));
        } catch (IOException ex) {
            Logger.getLogger(TextContent.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Create TextContent from file
     *
     * @param file the input file
     */
    public TextContent(File file) {
        Document doc = DocumentBuilder.parse(file);
        NodeList regions = doc.getElementsByTagName("TextRegion");

        builder = new StringBuilder();

        for (int r = 0; r < regions.getLength(); ++r) {
            Node region = regions.item(r);
            String type = Elements.getAttribute(region, "type");
            if (type != null && types.contains(type)) {
                NodeList nodes = region.getChildNodes();
                for (int n = 0; n < nodes.getLength(); ++n) {
                    Node node = nodes.item(n);

                    if (node.getNodeName().equals("TextEquiv")) {
                        if (builder.length() > 0) {
                            builder.append(' ');
                        }
                        builder.append(node.getTextContent());
                    }
                }
            }
        }
        builder.trimToSize();
    }

    @Override
    public String toString() {
        return builder.toString();
    }
}
