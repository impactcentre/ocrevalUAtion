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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

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
     * @return the root element of the document
     */
    public Element getRootElement(Document doc) {
        return doc.getDocumentElement();
    }
}
