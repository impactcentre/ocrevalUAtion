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
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

/**
 * Start-up actions: load default and user properties (user-defined values
 * overwrite defaults).
 *
 * @author R.C.C.
 */
public class StartUp {

    private static Properties props = new Properties();

    static {
        props = defaultProps();
        props.putAll(userDefinedProps());
    }

    /**
     * Read default properties form file defaultProperties.xml
     *
     * @return the default properties
     */
    private static Properties defaultProps() {
        Properties defProps = new Properties();
        InputStream in;

        try {
            in = StartUp.class.getResourceAsStream("/defaultProperties.xml");
            if (in != null) {
                defProps.loadFromXML(in);
                in.close();
                props = new Properties(defProps);
            }
        } catch (IOException ex) {
            Messages.severe(StartUp.class.getName() + ": " + ex);
        }
        return defProps;
    }

    /**
     *
     * @return the folder where the application has been launched
     */
    private static URL appFolder() {
        return StartUp.class.getProtectionDomain()
                .getCodeSource().getLocation();

    }

    /**
     *
     * @return the userProperties.xml file
     */
    private static File userFile() {
        URL url = appFolder();
        String dir = new File(url.getPath()).getParent();
        File file = new File(dir, "userProperties.xml");

        Messages.info("Application folder is " + url);

        if (!file.exists()) {
            url = StartUp.class.getResource("/userProperties.xml");
            if (url != null) {
                try {
                    file = new File(url.toURI());
                } catch (URISyntaxException ex) {
                    Messages.severe(StartUp.class.getName() + ": " + ex);
                }
            }
        }
        return file;
    }

    private static Properties userDefinedProps() {
        Properties userProps = new Properties();

        try {
            File file = userFile();
            if (file != null && file.exists()) {
                InputStream in = new FileInputStream(file);
                userProps.loadFromXML(in);
                Messages.info("Read properties from " + file);
                in.close();
            }
        } catch (IOException ex) {
            Messages.severe(StartUp.class
                    .getName() + ": " + ex);
        }
        return userProps;
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

    /**
     * Add a property value
     *
     * @param key
     * @param value
     */
    static void addValue(String key, String value) {
        try {
            File file = userFile();
            FileOutputStream os = new FileOutputStream(file);
            String values = props.getProperty(key);
            if (values == null) {
                props.setProperty(key, value);
            } else {
                props.setProperty(key, values + " " + value);
            }
            props.storeToXML(os, null);
        } catch (FileNotFoundException ex) {
            Messages.severe(StartUp.class
                    .getName() + ": " + ex);
        } catch (IOException ex) {
            Messages.severe(StartUp.class
                    .getName() + ": " + ex);
        }

    }
}
