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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A program option with a name and value and,optionally. help text and help
 * URL.
 *
 * @author R.C.C.
 * @param <Type> the type op option (Boolean, File)
 */
public class Option<Type> {

    String name;
    Type value;
    String text;
    URL url;

    /**
     * Crete an Option with the given name and value
     *
     * @param name the option's name
     * @param value the option's value
     */
    Option(String name, Type value) {
        this.name = name;
        this.value = value;
    }

    /**
     * Crete an Option with the given name (and null value)
     *
     * @param name the option's name
     */
    Option(String name) {
        this(name, null);
    }

    /**
     * Set this option's value
     *
     * @param value the option's value
     */
    public void setValue(Type value) {
        this.value = value;
    }

    /**
     * Get this option's value
     *
     * @return the option's value
     */
    public Type getValue() {
        return value;
    }

    /**
     * Get the option value type (Boolean, File, Integer,...)
     * @return 
     */
    public Class getType() {
        return value.getClass();
    }

    /**
     * Set this option's help text and URL with additional help
     *
     * @param helpText help text for this option
     * @param helpURL URL address with additional information
     */
    public void setHelp(String helpText, String helpURL) {
        this.text = helpText;
        if (helpURL != null) {
            try {
                this.url = new URL(helpURL);
            } catch (MalformedURLException ex) {
                Logger.getLogger(Option.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     *
     * @return the help text for this option
     */
    public String getHelpText() {
        return text;
    }

    /**
     *
     * @return the URL address with additional information
     */
    public URL getHelpURL() {
        return url;
    }
}
