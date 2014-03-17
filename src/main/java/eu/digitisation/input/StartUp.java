/*
 * Copyright (C) 2014 IMPACT Centre of Competence
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
import java.io.IOException;
import java.io.InputStream;
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
        try {
            InputStream in;
            // Read defaults
            Properties defaults = new Properties();
            in = StartUp.class.getResourceAsStream("/defaultProperties.xml");
            if (in != null) {
                defaults.loadFromXML(in);
                in.close();
                props = new Properties(defaults);
            }
            // Add user properties (may overwrite defaults)
            try {
                //String dir = System.getProperty("user.dir");
                String path = StartUp.class.getProtectionDomain()
                        .getCodeSource().getLocation().getPath();
                String dir = new File(path).getParent();
                Messages.info("Application folder is " + dir);
                File file = new File(dir, "userProperties.xml");
                if (file.exists()) {
                    in = new FileInputStream(file);
                    props.loadFromXML(in);
                    Messages.info("Read properties from " + file);
                    in.close();
                } else {
                    in = StartUp.class.getResourceAsStream("/userProperties.xml");
                    if (in != null) {
                        defaults.loadFromXML(in);
                        Messages.info("Read properties from " + file);
                        in.close();
                        props = new Properties(defaults);
                    } else {
                        Messages.info("No properties defined by user");
                    }
                }
            } catch (FileNotFoundException ex) {
                Messages.info("No user defined properties");
            }
        } catch (IOException ex) {
            Messages.severe(StartUp.class.getName() + ": " + ex);
        }
    }

    /**
     *
     *
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
}
