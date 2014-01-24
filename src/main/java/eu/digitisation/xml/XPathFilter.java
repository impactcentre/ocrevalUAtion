/*
 * Copyright (C) 2014 Universidad de Alicante
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Test elements against XPath expressions
 *
 * @author R.C.C.
 */
public class XPathFilter {

    static XPath xpath = XPathFactory.newInstance().newXPath();
    List<XPathExpression> expressions; // a list of valid XPath expressions

    private void add(String expression) throws XPathExpressionException {
        expressions.add(xpath.compile("self::" + expression));
    }

    private void addAll(String[] array) throws XPathExpressionException {
        for (String s : array) {
            add(s);
        }
    }

    /**
     * Create an ElementFilter from an array of XPath expressions
     *
     * @param array an array of XPath expressions
     * @throws javax.xml.xpath.XPathExpressionException
     */
    public XPathFilter(String[] array) throws XPathExpressionException {
        expressions = new ArrayList<XPathExpression>();
        addAll(array);
    }

    /**
     * Create an ElementFilter from the XPath expressions in a file (one per
     * line)
     *
     * @param file a file containing XPath expressions (one per line)
     * @throws java.io.IOException
     */
    public XPathFilter(File file) throws IOException {
        expressions = new ArrayList<XPathExpression>();
        BufferedReader reader = new BufferedReader(new FileReader(file));

        while (reader.ready()) {
            String line = reader.readLine().trim();
            try {
                add(line);
            } catch (XPathExpressionException ex) {
                throw new IOException(ex.getMessage());
            }
        }
    }

    /**
     * Check if the element matches any valid expression
     *
     * @param element
     * @return true if the element matches any of the valid XPAth expressions
     */
    public boolean accepts(Element element) {
        for (XPathExpression expression : expressions) {
            try {
                Boolean match = (Boolean) expression.evaluate(element, XPathConstants.BOOLEAN);
                if (match) {
                    return true;
                }
            } catch (XPathExpressionException ex) {
                // not a valid match
            }
        }
        return false;
    }

    /**
     * Select elements matching the XPath valid expression
     *
     * @param element a parent element
     * @return all descendent elements matching at least one of the XPath
     * expressions in this filter
     */
    public NodeList selectElements(Element element) {
        //NodeList nodeList = document.getElementsByTagName("*");
        Node elements = element.getOwnerDocument().createElement("void");
        NodeList nodeList = element.getElementsByTagName("*");

        for (int n = 0; n < nodeList.getLength(); n++) {
            Node node = nodeList.item(n);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element e = (Element) node;
                if (accepts(e)) {
                    elements.appendChild(node);
                }
            }
        }
        return elements.getChildNodes();
    }

    /**
     * Select elements matching the XPath valid expression
     *
     * @param doc a container XML document
     * @return all elements in the document matching at least one of the XPath
     * expressions in this filter
     */
    public NodeList selectElements(Document doc) {
        return selectElements(doc.getDocumentElement());
    }

    /**
     * Select content under the filtered elements
     *
     * @param args
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.err.println("usage: ElementFilter xmlfile xpathfile");
        } else {
            File xmlfile = new File(args[0]);
            File xpathfile = new File(args[1]);
            XPathFilter filter = new XPathFilter(xpathfile);
            NodeList nodes = filter.selectElements(DocumentParser.parse(xmlfile));
            for (int n = 0; n < nodes.getLength(); ++n) {
                System.out.println(nodes.item(n).getNodeName());
            }
        }
    }
}

