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

/**
 * Supported input file types
 *
 * @author R.C.C.
 */
public enum FileType {

    TXT, PAGE, FR10, HOC, UNKNOWN;
    
    String tag;
    String schemaLocation;  // schema URL

    static {
        Properties prop = new Properties();
        try (InputStream in = FileType.class.getResourceAsStream("/General.properties")) {
            prop.load(in);
        } catch (IOException ex) {
            Logger.getLogger(FileType.class.getName()).log(Level.SEVERE, null, ex);
        }
        PAGE.tag = prop.getProperty("PcGts");
        PAGE.schemaLocation = prop.getProperty("PAGE");
        FR10.tag = "document";
        FR10.schemaLocation = prop.getProperty("FR10");
    }

    /**
     *
     * @param file a file
     * @return the FileType of file
     */
    public static FileType valueOf(File file) {
        String name = file.getName().toLowerCase();
        if (name.endsWith(".txt")) {
            return TXT;
        } else if (name.endsWith(".xml")) {
            Document doc = DocumentBuilder.parse(file);
            String root = Elements.getRootElement(doc).getTagName();
            String url = Elements.getAttribute(doc, "xsi:schemaLocation");
            if (root.equals(PAGE.tag) && url.equals(PAGE.schemaLocation)) {
                return PAGE;
            } else if (root.equals(FR10.tag) && url.equals(FR10.schemaLocation)) {
                return FR10;
            } 
        }
        return UNKNOWN;
    }

}
