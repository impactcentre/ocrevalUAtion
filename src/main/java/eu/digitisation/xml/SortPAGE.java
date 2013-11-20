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
package eu.digitisation.xml;

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
 *
 * @author R.C.C.
 */
public class SortPAGE {

    Document doc;
    String[] order; // sorted identifiers

    /**
     * Constructor from XML file
     *
     * @param file
     */
    public SortPAGE(File file) {
        doc = DocumentBuilder.parse(file);
    }

    /**
     * Return the value of an attribute
     *
     * @param node the node containing the attribute
     * @param name the attribute name
     * @return he attribute value or null if the node contains no attribute with
     * that name
     */
    private static String getAttribute(Node node, String name) {
        Node att = node.getAttributes().getNamedItem(name);
        if (att != null) {
            return att.getNodeValue();
        } else {
            return null;
        }
    }

    /**
     * SortPAGE children consistently with the order defined for their id attribute
     *
     * @param node the parent node
     * @param order the array of id's in ascending order
     */
    private void sort(Node node, List<String> order) {
        Map<String, Node> index = new HashMap<>();
        NodeList children = node.getChildNodes();
        List<Node> backup = new ArrayList<>();
        //Node clone = null;///node.cloneNode(false);
        //System.out.println("Cloned " + node.getNodeName());
        for (String id : order) {
            index.put(id, null);
        }
       
        // Create an index ot text regions which need reordering
        for (int n = 0; n < children.getLength(); ++n) {
            Node child = children.item(n);
            backup.add(child);  // unsure if NodeList remains unchanged after insertions
            if (child instanceof Element
                    && child.getNodeName().equals("TextRegion")) {
                String id = getAttribute(child, "id");
                if (index.containsKey(id)) {
                    index.put(id, child);
                }
            }
        }
        while(children.getLength()>0) {
            node.removeChild(children.item(0));
        }
        int norder = 0;
        for (int n = 0; n < backup.size(); ++n) {
            Node child = backup.get(n);
           
            if (child instanceof Element
                    && child.getNodeName().equals("TextRegion")) {
                String id = getAttribute(child, "id");
                if (index.containsKey(id)) {
                    String nextreg = order.get(norder);
                    Node r = index.get(nextreg);
                    node.appendChild(r);
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
     * Extract reading order
     *
     * @param e
     * @return
     * @throws IOException
     */
    private List<String> readingOrder(Node e) throws IOException {
        NodeList children = e.getChildNodes();
        int length = children.getLength();
        List<String> order = new ArrayList<String>();

        for (int n = 0; n < length; ++n) {
            Node child = children.item(n);
            if (child instanceof Element
                    && child.getNodeName().equals("RegionRefIndexed")) {
                int index = Integer.parseInt(getAttribute(child, "index"));
                assert (index == order.size());
                String idref = getAttribute(child, "regionRef");
                order.add(idref);
            }
        }
        return order;
    }

    public void sort() throws IOException {
        NodeList groups = doc.getElementsByTagName("OrderedGroup");
        //List<Node> cloned = new ArrayList<>();
        for (int n = 0; n < groups.getLength(); ++n) {
            Node group = groups.item(n);
            Node container = group.getParentNode().getParentNode();
            List<String> order = readingOrder(group);
           sort(container, order);
        }
    }

    public static void main(String[] args) throws IOException {
        File ifile = new File(args[0]);
        File ofile = new File(args[1]);
        SortPAGE s = new SortPAGE(ifile);
        s.sort();
        DocumentWriter writer = new DocumentWriter(s.doc);
        writer.write(ofile);
    }

}
