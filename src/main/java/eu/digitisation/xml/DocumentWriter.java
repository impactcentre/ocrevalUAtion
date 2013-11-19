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

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Document;

/**
 * Writes XML document to String or File
 * @author R.C.C.
 */
public class DocumentWriter {
    static javax.xml.transform.Transformer transformer;
    static javax.xml.transform.stream.StreamResult result;

    javax.xml.transform.dom.DOMSource source;

    static {
        try {
            transformer = javax.xml.transform.TransformerFactory
                    .newInstance().newTransformer();
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(DocumentWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public DocumentWriter(Document document) {
        source = new javax.xml.transform.dom.DOMSource(document);
    }

    /**
     * Write XML to string
     *
     * @return string representation
     */
    @Override
    public String toString() {
        result = new javax.xml.transform.stream.StreamResult(new java.io.StringWriter());
        try {
            transformer.transform(source, result);
        } catch (TransformerException ex) {
            Logger.getLogger(DocumentBuilder.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
        return result.getWriter().toString();
    }

    public void write(java.io.File file) {
        result = new javax.xml.transform.stream.StreamResult(file);
        try {
            transformer.transform(source, result);
        } catch (TransformerException ex) {
            Logger.getLogger(DocumentBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
