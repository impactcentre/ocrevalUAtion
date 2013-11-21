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
package eu.digitisation.tmp;

import eu.digitisation.xml.DocumentBuilder;
import eu.digitisation.xml.DocumentWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Extended XML documents
 *
 * @version 2012.06.20
 */
public class XMLDocument {

    Document doc;

    /**
     * Create XMLDocuemnt from org.w3c.dom.Document
     *
     * @param doc the source document
     */
    public XMLDocument(Document doc) {
        this.doc = doc;
    }

    /**
     * Create XML document from file
     * @param file th input file
     */
    public XMLDocument(java.io.File file) {
        try {
            doc = DocumentBuilder.parse(file);
        } catch (Exception e) {
            System.err.println("Could not read " + file);
        }
    }

    /**
     * Create XML document from input stream
     * @param is the input stream
     */
    public XMLDocument(java.io.InputStream is) {
        try {
            doc = DocumentBuilder.parse(is);
        } catch (Exception e) {
            System.err.println("Could not read " + is);
        }
    }

    /**
     * Create a new document with the specified root label
     * @param root the document type (tag of root element)
     */
    public XMLDocument(String root) {
        doc = DocumentBuilder.newDocument();
        doc.appendChild(doc.createElement(root));
    }

    /**
     * Create a string representation of the XML document
     * @return string representation of the XML document
     */
    @Override
    public String toString() {
        DocumentWriter writer = new DocumentWriter(doc);
        return writer.toString();
    }

    /**
     * Write the XML document to a file
     * @param file the input file
     */
    public void write(java.io.File file) {
        DocumentWriter writer = new DocumentWriter(doc);
        writer.write(file);
    }

    /**
     * Create a new element under the root of the document with the specified
     * tag.
     *
     * @param tag the element tag
     * @return the created element
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
     * @return the create element
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
     * @return the XMLDocument as an org.w3c.dom.Document
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
