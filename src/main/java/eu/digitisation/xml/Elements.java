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

import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author R.C.C
 */
public class Elements {

    /**
     * Return the value of an attribute
     *
     * @param node the node containing the attribute
     * @param name the attribute name
     * @return he attribute value or null if the node contains no attribute with
     * that name
     */
    public static String getAttribute(Node node, String name) {
        Node att = node.getAttributes().getNamedItem(name);
        if (att != null) {
            return att.getNodeValue();
        } else {
            return null;
        }
    }

     /**
     *
     * @param e The parent element
     * @param name The child element name
     * @return list of children of e with the given tag
     */
    public static List<Element> getChildElementsByTagName(Element e, String name) {
        ArrayList<Element> list = new ArrayList<>();
        NodeList cnodes = e.getChildNodes();

        for (int n = 0; n < cnodes.getLength(); ++n) {
            Node node = cnodes.item(n);
            if (node instanceof Element && node.getNodeName().equals(name)) {
                list.add((Element) node);
            }
        }
        return list;
    }
    
    /**
     * @return the root element of the document
     */
    public static Element getRootElement(Document doc) {
        return doc.getDocumentElement();
    }
}
