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
package eu.digitisation.io;

import eu.digitisation.xml.DocumentBuilder;
import eu.digitisation.xml.Elements;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Supported input file types
 *
 * @author R.C.C.
 */
public enum FileType {

    TEXT, PAGE, FR10, HOCR, ALTO, UNKNOWN;
    String tag;
    String schemaLocation;  // schema URL

    static {
        Properties prop = new Properties();
        try (InputStream in = FileType.class.getResourceAsStream("/General.properties")) {
            prop.load(in);
        } catch (IOException ex) {
            Logger.getLogger(FileType.class.getName()).log(Level.SEVERE, null, ex);
        }
        TEXT.tag = null;  // no tag for this type 
        TEXT.schemaLocation = null; // no schema associated to this type
        PAGE.tag = "PcGts";
        PAGE.schemaLocation
                = StringNormalizer.reduceWS(prop.getProperty("schemaLocation.PAGE"));
        FR10.tag = "document";
        FR10.schemaLocation
                = StringNormalizer.reduceWS(prop.getProperty("schemaLocation.FR10"));
        ALTO.tag = "alto";
        ALTO.schemaLocation
                = StringNormalizer.reduceWS(prop.getProperty("schemaLocation.ALTO"));
        HOCR.tag = "html";
        HOCR.schemaLocation = null;  // no schema for this type 
    }

    /**
     *
     * @param file a file
     * @return the FileType of file
     */
    public static FileType valueOf(File file) {
        String name = file.getName().toLowerCase();

        if (name.endsWith(".txt")) {
            return TEXT;
        } else if (name.endsWith(".xml")) {
            Document doc = DocumentBuilder.parse(file);
            Element root = doc.getDocumentElement();
            String doctype = root.getTagName();
            String url = StringNormalizer
                    .reduceWS(root.getAttribute("xsi:schemaLocation"));

            if (doctype.equals(PAGE.tag) && url.contains(PAGE.schemaLocation)) {
                return PAGE;
            } else if (doctype.equals(FR10.tag) && url.contains(FR10.schemaLocation)) {
                return FR10;
            }
        } else if (name.endsWith(".html")) {
            Document doc = DocumentBuilder.parse(file);
            Element root = doc.getDocumentElement();
            String doctype = root.getTagName();
            if (doctype.equals(HOCR.tag)) {
                return HOCR;
            }
        }
        return UNKNOWN;
    }
}
