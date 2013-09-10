/**
 * Copyright (C) 2012 Rafael C. Carrasco
 * This code can be distributed or modified
 * under the terms of the GNU General Public License V2.
 */
package eu.digitisation.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * A parser for XML documents
 *
 * @author Rafael C. Carrasco
 * @version 2012.06.20
 */
public class XMLDocument {

    Document doc;
    static javax.xml.parsers.DocumentBuilder docBuilder;
    static javax.xml.transform.Transformer transformer;
    static javax.xml.transform.stream.StreamResult result;

    static {
        try {
            docBuilder = javax.xml.parsers.DocumentBuilderFactory
                    .newInstance().newDocumentBuilder();
            transformer = javax.xml.transform.TransformerFactory
                    .newInstance().newTransformer();
        } catch (Exception ex) {
            System.err.
                    println("Could not create document builder/transformer");
        }
    }

    /**
     * Create XML document from file
     */
    public XMLDocument(java.io.File file) {
        try {
            doc = docBuilder.parse(file);
        } catch (Exception e) {
            System.err.println("Could not read " + file);
        }
    }

    /**
     * Create XML document from input stream
     */
    public XMLDocument(java.io.InputStream is) {
        try {
            doc = docBuilder.parse(is);
        } catch (Exception e) {
            System.err.println("Could not read " + is);
        }
    }

    /**
     * Create a new document with the specified root label
     */
    public XMLDocument(String root) {
        doc = docBuilder.newDocument();
        doc.appendChild(doc.createElement(root));
    }

    /**
     * Create a string representation of the XML document
     */
    @Override
    public String toString() {
        try {
            javax.xml.transform.dom.DOMSource source =
                    new javax.xml.transform.dom.DOMSource(doc);
            result = new javax.xml.transform.stream.StreamResult(new java.io.StringWriter());
            transformer.transform(source, result);
            return result.getWriter().toString();
        } catch (Exception e) {
            System.err.println("XMLDocument could not write " + doc);
        }
        return null;
    }

    /**
     * Write the XML document to a file
     */
    public void write(java.io.File file) {
        try {
            javax.xml.transform.dom.DOMSource source =
                    new javax.xml.transform.dom.DOMSource(doc);
            result = new javax.xml.transform.stream.StreamResult(file);
            transformer.transform(source, result);
        } catch (Exception e) {
            System.err.println("XMLDocument could not write " + doc);
        }
    }

    /**
     * Create a new element under the root of the document with the specified
     * tag.
     *
     * @param tag the element tag
     */
    public Element addElement(String tag) {
        Element element = doc.createElement(tag);
        root().appendChild(element);
        return element;
    }

    /**
     * Add a text element under the root of the document with the specified
     * textual content.
     *
     * @param tag the element tag
     * @param content the textual content
     */
    public Element addTextElement(String tag, String content) {
        Element element = doc.createElement(tag);
        element.appendChild(doc.createTextNode(content));
        root().appendChild(element);
        return element;
    }

    /**
     * Add a child text element with the specified textual content under the
     * designated element
     *
     * @param parent the parent element
     * @param tag the tag of the new child
     * @param content the textual content of the new child
     * @return the new child element
     */
    public Element addTextElement(Element parent, String tag, String content) {
        Element element = doc.createElement(tag);
        element.appendChild(doc.createTextNode(content));
        parent.appendChild(element);
        return element;
    }

    /**
     * @return the XMLDocument as a org.w3c.dom.Document
     */
    public Document getDocument() {
        return doc;
    }

    /**
     * @return the root element of the document
     */
    public Element root() {
        return doc.getDocumentElement();
    }
}