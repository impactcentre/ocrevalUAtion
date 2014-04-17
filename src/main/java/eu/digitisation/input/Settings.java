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
package eu.digitisation.input;

import eu.digitisation.log.Messages;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

/**
 * Global settings: load default and user properties (user-defined values
 * overwrite defaults).
 *
 * @author R.C.C.
 */
public class Settings {

    private final static InputStream defaults; // the InputSteam storing the default global settings
    private final static File user;  // the file storing the user defined global settings
    private final static Properties props;

    static {

        defaults = Settings.class.getResourceAsStream("/defaultProperties.xml");
        Messages.info("Default props in file " + defaults);
        File dir = appFolder();
        File primary = (dir == null) ? null : new File(dir, "userProperties.xml");
        File secondary = open("userProperties.xml");
        if (primary != null && primary.exists()) {
            user = primary;  // first choice for user properties
        } else {
            if (secondary != null) {
                user = secondary;  // second choice for user properties
            } else {
                user = primary; // if nothing found set the default location for output
            }
        }

        props = new Properties();
        try {
            if (defaults != null) {
                props.loadFromXML(defaults);
            }
            if (user != null && user.exists()) {
                InputStream in = new FileInputStream(user);
                Properties uprops = new Properties();
                uprops.loadFromXML(in);
                merge(uprops);
                in.close();
                Messages.info("Read properties from " + user);
            }
        } catch (FileNotFoundException ex) {
            Messages.severe(Settings.class.getName() + ": " + ex);
        } catch (IOException ex) {
            Messages.severe(Settings.class.getName() + ": " + ex);
        }
    }

    /**
     * Identify where the application has been launched
     *
     * @return the folder where the application has been launched
     */
    private static File appFolder() {
        URL url = Settings.class.getProtectionDomain()
                .getCodeSource().getLocation();
        String location = new File(url.getPath()).getParent();

        Messages.info("Application folder is " + url);
        return (location == null) ? null : new File(location);
    }

    /**
     * Look recursively for a file
     *
     * @param fileName a file name
     * @return a file with this name under the application path
     */
    private static File open(String fileName) {
        File file = null;
        URL url = Settings.class.getResource(fileName);
        if (url != null) {
            try {
                URI uri = url.toURI();
                file = new File(uri);
            } catch (URISyntaxException ex) {
                Messages.severe(Settings.class.getName() + ": " + ex);

            }
        }
        return file;
    }

    /**
     * @return the properties defined at startup (user-defined overwrite
     * defaults).
     */
    public static Properties properties() {
        return props;
    }

    /**
     *
     * @param key a property name
     * @return the property with the specified key as defined by the user, and
     * otherwise, its default value ( (if the default is not defined, then the
     * method returns null).
     */
    public static String property(String key) {
        return props.getProperty(key);
    }

    public static void save() {
        FileOutputStream os;
        StringBuilder comments = new StringBuilder();

        comments.append("schemaLocation.ZZZ:")
                .append("valid schema locations for the ZZZ filetype (e.g., ALTO)")
                .append('\n')
                .append("maxlen: max number of chars in input file.")
                .append("If zero, no limit is applied (computation may take long");

        try {
            os = new FileOutputStream(user);
            props.storeToXML(os, comments.toString());
            Messages.info(Settings.class
                    .getName() + ": created new properties file " + user);
        } catch (FileNotFoundException ex) {
            Messages.severe(Settings.class
                    .getName() + ": " + ex);
        } catch (IOException ex) {
            Messages.severe(Settings.class
                    .getName() + ": " + ex);
        }
    }

    /**
     * Set and store the value of a configuration parameter
     *
     * @param key th parameter name
     * @param value the new value of the parameter
     */
    public static void setValue(String key, String value) {
        props.setProperty(key, value);
    }

    /**
     * Add values (overwrites old values)
     *
     * @param other another properties object
     */
    public static void merge(Properties other) {
        props.putAll(other);
        save();
    }
}
