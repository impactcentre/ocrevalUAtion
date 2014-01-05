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
package eu.digitisation.layout;

import eu.digitisation.io.FileType;
import eu.digitisation.io.TextContent;
import eu.digitisation.xml.DocumentParser;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.select.Elements;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * OldPage information contained in one GT or OCR file. Pending: store nested
 * structure
 *
 * @author R.C.C.
 */
class OldPage {

    private TextComponent[] blocks;
    private TextComponent[] lines;
    private TextComponent[] words;

    /**
     *
     * @return all line textTextComponents in this document
     */
    public TextComponent[] getLines() {
        return lines;
    }

    /**
     *
     * @return all word textTextComponents in this document
     */
    public TextComponent[] getWords() {
        return words;
    }

    /**
     * @return the textTextComponents
     */
    public TextComponent[] getBlocks() {
        return blocks;
    }

    OldPage(File file) throws IOException {
        FileType type = FileType.valueOf(file);
        switch (type) {
            case PAGE:
                readPageFile(file);
                break;
            case FR10:
                readFR10File(file);
                break;
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
     * Return all TextComponents with a given tag
     *
     * @param doc the container XML document
     * @param tag the element tag
     * @return the array of all TextComponents in doc with this tag
     */
    private TextComponent[] getComponents(Document doc, FileType ftype, String tag) {
        NodeList nodes = doc.getElementsByTagName(tag);

        TextComponent[] components = new TextComponent[nodes.getLength()];
        for (int n = 0; n < components.length; ++n) {
            Element e = (Element) nodes.item(n);
 //           components[n] = new TextComponent(ftype, e);
        }
        return components;
    }

    /**
     * Return all TextComponents with a given class
     *
     * @param doc the container HTML document
     * @param tag the element tag
     * @return the array of all TextComponents in doc with this tag
     */
    private TextComponent[] getComponents(org.jsoup.nodes.Document doc, String tag) {
        Elements elements = doc.body().select(tag);
        TextComponent[] components = new TextComponent[elements.size()];

        for (int n = 0; n < components.length; ++n) {
            components[n] = new TextComponent(elements.get(n));
        }
        return components;
    }

    /**
     * Constructor from PAGE XML-file
     *
     * @param file the input PAGE XML-file
     */
    private void readPageFile(File file) {
        Document doc = DocumentParser.parse(file);

        blocks = getComponents(doc, FileType.PAGE, "TextRegion");
        lines = getComponents(doc, FileType.PAGE, "TextLine");
        words = getComponents(doc, FileType.PAGE, "Word");
    }

    /**
     * Read content in hOCR HTML-file
     *
     * @param file the hOCR HTML-file
     */
    private void readHOCRFile(File file) {
        try {
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse(file, null);

            blocks = getComponents(doc, "*[class=ocr_par");
            lines = getComponents(doc, "*[class=ocr_line");
            words = getComponents(doc, "*[class=ocrx_word");

        } catch (IOException ex) {
            Logger.getLogger(TextContent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Read FR10 file Words are not explicitly tagged in FR10 XML and,
     * therefore, the specific function getFR10words is called to parse the
     * content of every line
     *
     * @param file the FR10 XML-file
     */
    private void readFR10File(File file) {
        Document doc = DocumentParser.parse(file);

        blocks = getComponents(doc, FileType.FR10, "block");
        lines = getComponents(doc, FileType.FR10, "line");
        words = getFR10Words(doc);
    }

    /**
     * Specific function to split FR10 formatting elements into smaller
     * components (words). Formatting is a sequence of charParams elements
     * containing characters. An empty charParams element indicates word
     * boundaries.
     *
     * @param element a formatting element
     * @return the TextComponents (words) in this element
     */
    private static TextComponent[] getFR10Words(Document doc) {
        List<TextComponent> words = new ArrayList<TextComponent>();
        NodeList charParams = doc.getElementsByTagName("charParams");
        StringBuilder builder = new StringBuilder();
        BoundingBox bbox = new BoundingBox();

        for (int nchar = 0; nchar < charParams.getLength(); ++nchar) {
            Element charParam = (Element) charParams.item(nchar);
            String content = charParam.getTextContent();
            if (content.length() > 0) {
                int x0 = Integer.parseInt(charParam.getAttribute("l"));
                int y0 = Integer.parseInt(charParam.getAttribute("t"));
                int x1 = Integer.parseInt(charParam.getAttribute("r"));
                int y1 = Integer.parseInt(charParam.getAttribute("b"));
                BoundingBox cbox = new BoundingBox(x0, y0, x1, y1);
                bbox.add(cbox);
                builder.append(content);
            } else if (builder.length() > 0) {
                TextComponent word
                        = new TextComponent(null, null, null,
                                builder.toString(), bbox.asPolygon());
                words.add(word);
                builder = new StringBuilder();
                bbox = new BoundingBox();
            }
        }
        return (TextComponent[]) words.toArray();
    }

    private static String getFR10TextContent(Element e) {
        StringBuilder builder = new StringBuilder();
        NodeList formattings = e.getElementsByTagName("formatting");
        for (int nform = 0; nform < formattings.getLength(); ++nform) {
            Element element = (Element) formattings.item(nform);
            NodeList charParams = element.getElementsByTagName("charParams");
            for (int nchar = 0; nchar < charParams.getLength(); ++nchar) {
                Element charParam = (Element) charParams.item(nchar);
                String content = charParam.getTextContent();
                builder.append(content.length() > 0 ? content : ' ');
            }
        }
        return builder.toString();
    }

    private static TextComponent[] getFR10Lines(Document doc) {
        NodeList nodes = doc.getElementsByTagName("line");
        TextComponent[] lines = new TextComponent[nodes.getLength()];
        for (int n = 0; n < nodes.getLength(); ++n) {
            Element line = (Element) nodes.item(n);
            int x0 = Integer.parseInt(line.getAttribute("l"));
            int y0 = Integer.parseInt(line.getAttribute("t"));
            int x1 = Integer.parseInt(line.getAttribute("r"));
            int y1 = Integer.parseInt(line.getAttribute("b"));
            BoundingBox bbox = new BoundingBox(x0, y0, x1, y1);
            String content = getFR10TextContent(line);
            lines[n] = new TextComponent(null, null, null, content, bbox.asPolygon());
        }
        return lines;
    }
}
