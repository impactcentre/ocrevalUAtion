/*
 * Copyright (C) 2013 IMPACT Centre of Competence
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

import eu.digitisation.io.TextContent;
import eu.digitisation.xml.DocumentBuilder;
import eu.digitisation.xml.DocumentWriter;
import eu.digitisation.xml.Elements;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * PAGE-XML regions order in the document can differ form reading order.
 * Such information is stored under OrdereGroup (recursive) elements.
 * This class restores the appropriate order of elements in the document.
 * @author R.C.C.
 */
public class Sort {

    /**
     * Sort children consistently with the order defined for their id attribute
     * Remark: NodeList mutates when operations on nodes take place: an backup 
     * childList is used to avoid such conflicts.
     * @param node the parent node
     * @param order the array of id's in ascending order
     */
    private static void sort(Node node, List<String> order) {
        Map<String, Node> index = new HashMap<>();  // index of children nodes
        NodeList children = node.getChildNodes();
        List<Node> childList = new ArrayList<>();   // External copy of children

        // Initialize index (only nodes tah need reordering will be stored)
        for (String id : order) {
            index.put(id, null);
        }

        // Create an index ot text regions which need reordering
        for (int n = 0; n < children.getLength(); ++n) {
            Node child = children.item(n);
            childList.add(child);
            if (child instanceof Element) {
                String tag = child.getNodeName();
                String id = Elements.getAttribute(child, "id");
                if (tag.equals("TextRegion") && index.containsKey(id)) {
                    index.put(id, child);
                }
            }
        }

        // Clear internal list of child nodes
        while (children.getLength() > 0) {
            node.removeChild(children.item(0));
        }

        int norder = 0; // the posititon in the order list
        for (int n = 0; n < childList.size(); ++n) {
            Node child = childList.get(n);
            if (child instanceof Element) {
                String tag = child.getNodeName();
                String id = Elements.getAttribute(child, "id");
                if (tag.equals("TextRegion") && index.containsKey(id)) {
                    Node replacement = index.get(order.get(norder));
                    node.appendChild(replacement);
                    ++norder;
                } else {
                    node.appendChild(child);
                }
            } else {
                node.appendChild(child);
            }
        }
    }

    /**
     * Extract reading order as defined by an OrderedGroup element
     *
     * @param e the OrderedGroup element
     * @return list of identifiers in reading order
     * @throws IOException
     */
    private static List<String> readingOrder(Node node) throws IOException {
        NodeList children = node.getChildNodes();
        List<String> order = new ArrayList<>();

        for (int n = 0; n < children.getLength(); ++n) {
            Node child = children.item(n);
            if (child instanceof Element
                    && child.getNodeName().equals("RegionRefIndexed")) {
                String index = Elements.getAttribute(child, "index");
                assert Integer.parseInt(index) == order.size();
                String idref = Elements.getAttribute(child, "regionRef");
                order.add(idref);
            }
        }
        return order;
    }

    /**
     * Create document where ordered groups follow the order information
     *
     * @param file the input PAGE XML file
     * @return the sorted document
     * @throws java.io.IOException
     */
    public static Document sorted(File file) throws IOException {
        Document doc = DocumentBuilder.parse(file);
        NodeList groups = doc.getElementsByTagName("OrderedGroup");
        for (int n = 0; n < groups.getLength(); ++n) {
            Node group = groups.item(n);
            // group element -> ReadingOrder-> OrderedGroup
            Node container = group.getParentNode().getParentNode();
            List<String> order = readingOrder(group);
            sort(container, order);
        }
        return doc;
    }

    public static void main(String[] args) throws IOException {
        File ifile = new File(args[0]);
        File ofile = new File(args[1]);
        Document doc = Sort.sorted(ifile);
        DocumentWriter writer = new DocumentWriter(doc);
        writer.write(ofile);
    }
}
