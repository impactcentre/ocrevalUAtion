package eu.digitisation.Page;

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
import eu.digitisation.io.FileType;
import java.awt.Polygon;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A region in a document (a page, a block, line (TextLine) or word)
 *
 * @author R.C.C.
 */
public class Region extends Polygon {

    private static final long serialVersionUID = 1L;

    String id;       // id attribute
    RegionType type; // the type of region (page, block, line, word)
    String subtype;  // block type (paragraph, header, TOC).
    String content;  // text content

    /**
     * Constructor
     *
     * @param id region identifier
     * @param type the type of region
     * @param subtype region subtype (paragraph, TOC, etc)
     * @param content the textual content
     * @param poly the polygon in the image containing the region
     */
    public Region(String id, RegionType type, String subtype, String content, Polygon poly) {
        super(poly.xpoints, poly.ypoints, poly.npoints);
        this.id = id;
        this.type = type;
        this.subtype = subtype;
        this.content = content;
    }

    /**
     * Constructor from a PAGE-XML element
     *
     * @param element the XML element containing the region
     */
    public Region(Element element) {
        id = element.getAttribute("id");
        type = RegionType.valueOf(FileType.PAGE, element.getTagName());
        subtype = element.getAttribute("type");

        // Get coordinates and content
        NodeList children = element.getChildNodes();
        for (int nchild = 0; nchild < children.getLength(); ++nchild) {
            Node child = children.item(nchild);
            if (child instanceof Element) {
                if (child.getNodeName().equals("Coords")) {
                    Element coords = (Element) child;
                    if (npoints > 0) {
                        throw new DOMException(DOMException.INVALID_ACCESS_ERR,
                                "Multiple Coords in region " + id);
                    }
                    NodeList nodes = coords.getChildNodes(); // points
                    for (int n = 0; n < nodes.getLength(); ++n) {
                        Node node = nodes.item(n);
                        if (node.getNodeName().equals("Point")) {
                            Element point = (Element) node;
                            int x = Integer.parseInt(point.getAttribute("x"));
                            int y = Integer.parseInt(point.getAttribute("y"));
                            addPoint(x, y);
                        }
                    }
                } else if (child.getNodeName().equals("TextEquiv")) {
                    String text = child.getTextContent();
                    if (content != null) {
                        throw new DOMException(DOMException.INVALID_ACCESS_ERR,
                                "Multiple content in region " + id);
                    }
                    content = text;
                }
            }
        }
    }

    /**
     * Constructor from an hOCR-HTML element
     *
     * @param element the HTML element containing the region
     */
    public Region(org.jsoup.nodes.Element element) {
        id = element.attr("id");
        type = RegionType.valueOf(FileType.HOCR, element.attr("type"));
        subtype = element.attr("type");
        content = element.text();

        // extract coordinates
        String[] coords = element.attr("title").trim().split("\\p{Space}+");
        if (coords[0].equals("bbox")) {
            int x0 = Integer.parseInt(coords[1]);
            int y0 = Integer.parseInt(coords[2]);
            int x1 = Integer.parseInt(coords[3]);
            int y1 = Integer.parseInt(coords[4]);
            addPoint(x0, y0);
            addPoint(x0, y1);
            addPoint(x1, y1);
            addPoint(x1, y0);
        } else if (coords[0].equals("poly")) {
            int n = 1;
            while (n + 1 < coords.length) {
                int x = Integer.parseInt(coords[n]);
                int y = Integer.parseInt(coords[n + 1]);
                addPoint(x, y);
                n += 2;
            }
        }
    }
}
