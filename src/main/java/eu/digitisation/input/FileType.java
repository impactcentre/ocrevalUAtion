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
package eu.digitisation.input;

import eu.digitisation.log.Messages;
import eu.digitisation.text.StringNormalizer;
import eu.digitisation.xml.DocumentParser;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Supported input file types
 *
 * @author R.C.C.
 */
@SuppressWarnings("javadoc")
public enum FileType {

    TEXT, PAGE, FR10, HOCR, ALTO, UNKNOWN;
    String tag;
    String schemaLocation;  // schema URL

    static {
        Properties props = Settings.properties();
        String location;

        TEXT.tag = null;  // no tag for this type 
        TEXT.schemaLocation = null; // no schema associated to this type

        PAGE.tag = "PcGts";
        location = props.getProperty("schemaLocation.PAGE");
        PAGE.schemaLocation = (location == null) ? "" : location;

        FR10.tag = "document";
        location = props.getProperty("schemaLocation.FR10");
        FR10.schemaLocation = (location == null) ? "" : location;

        ALTO.tag = "alto";
        location = props.getProperty("schemaLocation.ALTO");
        ALTO.schemaLocation = (location == null) ? "" : location;

        HOCR.tag = "html";
        HOCR.schemaLocation = null;  // no schema for this type 
    }

    /**
     * Add anew schema location to this type of file
     *
     * @param type a FileType
     * @param schemaLocation a new schemaLocation for this type of files
     */
    public static void addLocation(FileType type, String schemaLocation) {
        if (type.schemaLocation == null || type.schemaLocation.isEmpty()) {
            type.schemaLocation = "\t " + schemaLocation + '\n';
        } else {
            type.schemaLocation += "\t" + schemaLocation + '\n';
        }
    }

    /**
     *
     * @return the schemaLocations as a properties object: a (key, value) map
     */
    public static Properties asProperties() {
        Properties props = new Properties();
        for (FileType type : values()) {
            String key = "schemaLocation." + type.name();
            String value = type.schemaLocation;
            if (value != null) {
                props.setProperty(key, value);
            }
        }
        return props;
    }

    /**
     *
     * @param locations1 string of URL schema locations separated by spaces
     * @param locations2 string of URL schema locations separated by spaces
     * @return True if at least one URL is in both locations
     */
    private static boolean sameLocation(String locations1, String locations2) {
        String[] urls = locations2.trim().split("\\p{Space}+");

        for (String url : urls) {
            System.out.println(url);
            if (!url.isEmpty() && locations1.contains(url)) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param file a file
     * @return the FileType of file
     * @throws eu.digitisation.input.SchemaLocationException
     * @throws java.io.IOException
     */
    public static FileType valueOf(File file)
            throws SchemaLocationException, IOException {
        String name = file.getName().toLowerCase(Locale.ENGLISH);
        FileType type = UNKNOWN;

        if (name.endsWith(".txt")) {
            return TEXT;
        } else if (name.endsWith(".xml")) {
            Document doc = DocumentParser.parse(file);
            Element root = doc.getDocumentElement();
            String doctype = root.getTagName();
            String location;

            if (root.hasAttribute("xsi:schemaLocation")) {
                location = StringNormalizer
                        .reduceWS(root.getAttribute("xsi:schemaLocation"));
            } else if (root.hasAttribute("xsi:noNamespaceSchemaLocation")) {
                location = StringNormalizer
                        .reduceWS(root.getAttribute("xsi:noNamespaceSchemaLocation"));
            } else {
                throw new IOException("XML file must specify an schema location");
            }
//            System.out.println("Schema location is " + location);

            if (doctype.equals(PAGE.tag)) {
                if (sameLocation(location, PAGE.schemaLocation)) {
                    type = PAGE;
                } else if (!location.isEmpty()) {
                    throw new SchemaLocationException(PAGE, location);
                }
            } else if (doctype.equals(FR10.tag)) {
                if (sameLocation(location, FR10.schemaLocation)) {
                    type = FR10;
                } else if (!location.isEmpty()) {
                    throw new SchemaLocationException(FR10, location);
                }
            } else if (doctype.equals(ALTO.tag)) {
                if (sameLocation(location, ALTO.schemaLocation)) {
                    type = ALTO;
                } else if (!location.isEmpty()) {
                    throw new SchemaLocationException(ALTO, location);
                }
            }

        } else if (name.endsWith(".html")) {
            try {
                org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse(file, null);
                if (!doc.head().select("meta[name=ocr-system").isEmpty()) {
                    type = HOCR;
                }
            } catch (IOException ex) {
                Messages.info(FileType.class
                        .getName() + ": " + ex);
            }
        }
        Messages.info(file.getName() + " is " + type);
        return type;
    }
}
