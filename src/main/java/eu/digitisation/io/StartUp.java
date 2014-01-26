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
package eu.digitisation.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Start-up actions: load default and user properties (user-defined values
 * overwrite defaults).
 *
 * @author R.C.C.
 */
public class StartUp {

    private static Properties props = new Properties();

    static {
        InputStream in;
        try {
            // Read defaults
            Properties defaults = new Properties();
            in = FileType.class.getResourceAsStream("/default.properties");
            if (in != null) {
                defaults.load(in);
                in.close();
                props = new Properties(defaults);
            }
            // Add user properties (may overwrite defaults)
            try {
                in = new FileInputStream(new File("user.properties"));
                props.load(in);
                in.close();
            } catch (FileNotFoundException ex) {
                // continue
            }
        } catch (IOException ex) {
            Logger.getLogger(FileType.class.getName()).log(Level.SEVERE, null, ex);
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
