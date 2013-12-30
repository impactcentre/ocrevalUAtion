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

import eu.digitisation.io.FileType;
import eu.digitisation.io.TextContent;
import eu.digitisation.xml.DocumentParser;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.select.Elements;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Geometry information contained in one PAGE-XML file
 *
 * @author R.C.C.
 */
class Geometry {

    private Region[] textRegions;
    private Region[] textLines;
    private Region[] words;

    /**
     *
     * @return all line textRegions in this document
     */
    public Region[] getTextLines() {
        return textLines;
    }

    /**
     *
     * @return all word textRegions in this document
     */
    public Region[] getWords() {
        return words;
    }

    /**
     * @return the textRegions
     */
    public Region[] getTextRegions() {
        return textRegions;
    }

    Geometry(File file) throws IOException {
        FileType type = FileType.valueOf(file);
        switch (type) {
            case PAGE:
                readPageFile(file);
                break;
//            case FR10:
//                readFR10File(file);
//                break;
            case HOCR:
                readHOCRFile(file);
                break;
 //           case ALTO:
 //               readALTOfile(file);
 //               break;
            default:
                throw new IOException("Unsupported file format " + type);
        }
    }

    /**
     * Constructor from PAGE-XML file
     *
     * @param file the input PAGE-XML file
     */
    final void readPageFile(File file) {
        Document doc = DocumentParser.parse(file);
        NodeList nodes;
        int length;

        // Get textRegions
        nodes = doc.getElementsByTagName("TextRegion");
        length = nodes.getLength();
        textRegions = new Region[length];

        for (int n = 0; n < length; ++n) {
            Element e = (Element) nodes.item(n);
            textRegions[n] = new Region(e);
        }

        // Get textLines
        nodes = doc.getElementsByTagName("TextLine");
        length = nodes.getLength();
        textLines = new Region[length];

        for (int n = 0; n < length; ++n) {
            Element e = (Element) nodes.item(n);
            textLines[n] = new Region(e);
        }

        // Get words
        nodes = doc.getElementsByTagName("Word");
        length = nodes.getLength();
        words = new Region[length];

        for (int n = 0; n < length; ++n) {
            Element e = (Element) nodes.item(n);
            words[n] = new Region(e);
        }

    }

    final void readHOCRFile(File file) {
        try {
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse(file, null);
            Elements elements;
            int length;

            elements = doc.body().select("*[class=ocr_par");
            length = elements.size();
            textRegions = new Region[length];
            for (int n = 0; n < length; ++n) {
                textRegions[n] = new Region(elements.get(n));
            }

            elements = doc.body().select("*[class=ocr_line");
            length = elements.size();
            textLines = new Region[length];
            for (int n = 0; n < length; ++n) {
                textLines[n] = new Region(elements.get(n));
            }

            elements = doc.body().select("*[class=ocrx_word");
            length = elements.size();
            words = new Region[length];
            for (int n = 0; n < length; ++n) {
                words[n] = new Region(elements.get(n));
            }
        } catch (IOException ex) {
            Logger.getLogger(TextContent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
