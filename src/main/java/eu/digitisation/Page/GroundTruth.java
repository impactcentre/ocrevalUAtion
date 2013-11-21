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

import java.awt.Polygon;
import java.io.File;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.w3c.dom.DOMException;
import eu.digitisation.xml.DocumentBuilder;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores the ground truth content of a PAGE-XML file
 *
 * @author R.C.C.
 */
public class GroundTruth { 

    TextRegion[] regions;
    TextRegion[] lines;
    TextRegion[] words;

    /**
     * Construct GT from file
     *
     * @param file the input file
     */
    public GroundTruth(File file) {
        Document doc = DocumentBuilder.parse(file);

        // Get regions
        NodeList rnodes = doc.getElementsByTagName("TextRegion");
        int length = rnodes.getLength();
        regions = new TextRegion[length];

        for (int n = 0; n < length; ++n) {
            Element e = (Element) rnodes.item(n);
            String id = getAttribute(e, "id");
            String type = getAttribute(e, "type");
            Polygon p = getCoords((Element) rnodes.item(n));
            regions[n] = new TextRegion(id, type, p);
        }

        // Get lines
        rnodes = doc.getElementsByTagName("TextLine");
        length = rnodes.getLength();
        lines = new TextRegion[length];

        for (int n = 0; n < length; ++n) {
            Element e = (Element) rnodes.item(n);
            String id = getAttribute(e, "id");
            String type = "line";
            Polygon p = getCoords((Element) rnodes.item(n));
            lines[n] = new TextRegion(id, type, p);
        }

        // Get words
        rnodes = doc.getElementsByTagName("Word");
        length = rnodes.getLength();
        words = new TextRegion[length];

        for (int n = 0; n < length; ++n) {
            Element e = (Element) rnodes.item(n);
            String id = getAttribute(e, "id");
            String type = "word";
            Polygon p = getCoords((Element) rnodes.item(n));
            words[n] = new TextRegion(id, type, p);
        }

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
     *
     * @param e The parent element
     * @param name The child element name
     * @return list of children of e with the given tag
     */
    private static List<Element> getChildElementsByTagName(Element e, String name) {
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
     * Return the polygon delimiting a text region
     *
     * @param e the TextRegion element
     * @return the polygon delimiting a text region
     */
    private static Polygon getCoords(Element e) {
        Polygon region = new Polygon();
        List<Element> elements = getChildElementsByTagName(e, "Coords");
        if (elements.size() > 1) {
            throw new DOMException(DOMException.INVALID_ACCESS_ERR,
                    "Multiple Coords in TextRegion");
        } else {
            Element element = elements.get(0);
            NodeList pnodes = element.getChildNodes(); // points
            for (int n = 0; n < pnodes.getLength(); ++n) {
                Node pnode = pnodes.item(n);
                if (pnode.getNodeName().equals("Point")) {
                    int x = Integer.parseInt(getAttribute(pnode, "x"));
                    int y = Integer.parseInt(getAttribute(pnode, "y"));
                    region.addPoint(x, y);
                }
            }
        } 
        return region;
    }

    /**
     *
     * @return all text regions in this document
     */
    public TextRegion[] getTextRegions() {
        return regions;
    }

    public TextRegion[] getLines() {
        return lines;
    }

    public TextRegion[] getWords() {
        return words;
    }
}
