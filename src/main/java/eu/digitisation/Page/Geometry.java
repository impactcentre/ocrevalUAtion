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

import eu.digitisation.xml.DocumentParser;
import java.awt.Polygon;
import java.io.File;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Geometry information contained in one PAGE-XML file
 *
 * @author R.C.C.
 */
class Geometry {

    private final TextRegion[] regions;
    private final TextRegion[] lines;
    private final TextRegion[] words;

    /**
     *
     * @return all line regions in this document
     */
    public TextRegion[] getLines() {
        return lines;
    }

    /**
     *
     * @return all word regions in this document
     */
    public TextRegion[] getWords() {
        return words;
    }

    /**
     * @return the regions
     */
    public TextRegion[] getRegions() {
        return regions;
    }

    
    /**
     * Return the polygon delimiting a text region
     *
     * @param e the TextRegion element
     * @return the polygon delimiting a text region
     */
    private static Polygon getCoords(Element element) {
        Polygon poly = new Polygon();
        NodeList children = element.getChildNodes();

        for (int nchild = 0; nchild < children.getLength(); ++nchild) {
            Node child = children.item(nchild);
            if (child instanceof Element
                    && child.getNodeName().equals("Coords")) {
                Element coords = (Element) child;
                if (poly.npoints > 0) {
                    throw new DOMException(DOMException.INVALID_ACCESS_ERR,
                            "Multiple Coords in TextRegion");
                }
                NodeList nodes = coords.getChildNodes(); // points
                for (int n = 0; n < nodes.getLength(); ++n) {
                    Node node = nodes.item(n);
                    if (node.getNodeName().equals("Point")) {
                        Element point = (Element) node;
                        int x = Integer.parseInt(point.getAttribute("x"));
                        int y = Integer.parseInt(point.getAttribute("y"));
                        poly.addPoint(x, y);
                    }
                }
            }
        }
        return poly;
    }

    /**
     * Construct GT from file
     *
     * @param file the input file
     */
    public Geometry(File file) {
        Document doc = DocumentParser.parse(file);

        // Get regions
        NodeList rnodes = doc.getElementsByTagName("TextRegion");
        int length = rnodes.getLength();
        regions = new TextRegion[length];

        for (int n = 0; n < length; ++n) {
            Element e = (Element) rnodes.item(n);
            String id = e.getAttribute("id");
            String type = e.getAttribute("type");
            Polygon p = getCoords((Element) rnodes.item(n));
            regions[n] = new TextRegion(id, type, p);
        }

        // Get lines
        rnodes = doc.getElementsByTagName("TextLine");
        length = rnodes.getLength();
        lines = new TextRegion[length];

        for (int n = 0; n < length; ++n) {
            Element e = (Element) rnodes.item(n);
            String id = e.getAttribute("id");
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
            String id = e.getAttribute("id");
            String type = "word";
            Polygon p = getCoords((Element) rnodes.item(n));
            words[n] = new TextRegion(id, type, p);
        }

    }
}
