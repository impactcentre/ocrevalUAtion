package eu.digitisation.layout;

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
import java.io.IOException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A region in a document (a page, a block, line, or word)
 *
 * @author R.C.C.
 */
public class TextComponent {

    private static final long serialVersionUID = 1L;

    String id;          // identifier
    ComponentType type; // the type of component (page, block, line, word)
    String subtype;     // the type of block (paragraph, header, TOC).
    String content;     // text content
    Polygon frontier;   // the component frontier

    /**
     * Basic constructor
     *
     * @param id identifier
     * @param type the type of component (page, block, line, word)
     * @param subtype the type of block (paragraph, header, TOC).
     * @param content text content
     * @param frontier the component frontier
     */
    public TextComponent(String id, ComponentType type, String subtype,
            String content, Polygon frontier) {
        this.id = id;
        this.type = type;
        this.subtype = subtype;
        this.content = content;
        this.frontier = frontier;
    }

    /**
     *
     * @return the component identifier
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @return the type of this component
     */
    public ComponentType getType() {
        return type;
    }

    /**
     *
     * @return the subtype of this component
     */
    public String getSubtype() {
        return subtype;
    }

    /**
     * Get the text content
     *
     * @return the textual contend of this component
     */
    public String getContent() {
        return content;
    }

    /**
     * Get the frontier
     *
     * @return the polygonal frontier of this component
     */
    public Polygon getFrontier() {
        return frontier;
    }

    /**
     *
     * @return a string representation of this TextComponent
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("<TextComponent>\n")
                .append("\t<id>").append(id).append("</id>\n")
                .append("\t<type>").append(type).append("</type>\n")
                .append("\t<subtype>").append(subtype).append("</subtype>\n")
                .append("\t<content>").append(content).append("</content>\n")
                .append("\t<frontier>\n");
        if (frontier != null) {
            for (int n = 0; n < frontier.npoints; ++n) {
                builder.append("\t\t<x>").append(frontier.xpoints[n]).append("</x>")
                        .append("<y>").append(frontier.ypoints[n]).append("</y>\n");
            }
        }
        builder.append("\t</frontier>\n");
        builder.append("</TextComponent>\n");
        return builder.toString();
    }

    /**
     * Constructor from XML element
     *
     * @param ftype the file type
     * @param element the XML element containing the region
     * @throws java.io.IOException
     */
    public TextComponent(FileType ftype, Element element) throws IOException {
        if (ftype == FileType.PAGE) {
            id = element.getAttribute("id");
            type = ComponentType.valueOf(ftype, element.getTagName());
            subtype = element.getAttribute("type");

            // Get frontier and content
            NodeList children = element.getChildNodes();
            for (int nchild = 0; nchild < children.getLength(); ++nchild) {
                Node child = children.item(nchild);
                if (child instanceof Element) {
                    if (child.getNodeName().equals("Coords")) {
                        Element coords = (Element) child;
                        if (frontier.npoints > 0) {
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
                                frontier.addPoint(x, y);
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

//      }  else if (ftype == FileType.FR10) {
//            id = element.getAttribute("id");
//            type = ComponentType.valueOf(ftype, element.getTagName());
//            subtype = (type == ComponentType.BLOCK)
//                    ? element.getAttribute("blockType")
//                    : null;
//
//            NodeList pars = element.getElementsByTagName(tag);
        } else {
            throw new java.lang.IllegalArgumentException("unsupported format " + ftype);
        }
    }

    /**
     * Constructor from an hOCR-HTML element
     *
     * @param element the HTML element containing the region
     */
    public TextComponent(org.jsoup.nodes.Element element) {
        id = element.attr("id");
        type = ComponentType.valueOf(FileType.HOCR, element.attr("type"));
        subtype = element.attr("type");
        content = element.text();

        // extract coordinates
        String[] coords = element.attr("title").trim().split("\\p{Space}+");
        if (coords[0].equals("bbox")) {
            int x0 = Integer.parseInt(coords[1]);
            int y0 = Integer.parseInt(coords[2]);
            int x1 = Integer.parseInt(coords[3]);
            int y1 = Integer.parseInt(coords[4]);
            frontier = new BoundingBox(x0, y0, x1, y1).asPolygon();
        } else if (coords[0].equals("poly")) {
            int n = 1;
            while (n + 1 < coords.length) {
                int x = Integer.parseInt(coords[n]);
                int y = Integer.parseInt(coords[n + 1]);
                frontier.addPoint(x, y);
                n += 2;
            }
        }
    }
}
