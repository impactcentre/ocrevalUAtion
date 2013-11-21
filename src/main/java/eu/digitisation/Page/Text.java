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
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Extracts textual content form PAGE XML file
 *
 * @author R.C.C.
 */
public class Text {

    Document doc;
    static final Set<String> types;

    static {
        types = new HashSet<>();
        Properties prop = new Properties();
        try {
            File dir = new File(".");
            System.out.println(dir.getAbsolutePath());
            FileReader reader
                    = new FileReader("src/main/resources/General.properties");
            prop.load(reader);
            String s = prop.getProperty("TextRegionTypes");
            String separator = ",\\p{Space}+";
            types.addAll(Arrays.asList(s.trim().split(separator)));
        } catch (IOException ex) {
            Logger.getLogger(Text.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Create a PAGE document from file
     * @param file 
     */
    public Text(File file) {
        doc = DocumentBuilder.parse(file);
    }

    /**
     * 
     * @return the textual content under the selected region types
     */
    public String getText() {
        StringBuilder builder = new StringBuilder();
        NodeList regions = doc.getElementsByTagName("TextRegion");
        for (int r = 0; r < regions.getLength(); ++r) {
            Node region = regions.item(r);
            NodeList nodes = region.getChildNodes();
            for (int n = 0; n < nodes.getLength(); ++n) {
                Node node = nodes.item(n);
              
                if (node.getNodeName().equals("TextEquiv")) {
                    if (builder.length() > 0) {
                        builder.append(' ');
                    }
                      System.out.println(node.getNodeValue());
                    builder.append(node.getNodeValue());
                }
            }
        }
        return builder.toString();
    }
}
