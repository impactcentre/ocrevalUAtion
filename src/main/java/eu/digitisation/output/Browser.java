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
package eu.digitisation.output;

import eu.digitisation.log.Messages;
import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

/**
 * Open a file or URL with an operating system application
 *
 * @author R.C.C.
 */
public class Browser {

    /**
     * Open a URI
     *
     * @param uri the location of the file or resource
     */
    public static void open(URI uri) {
        System.out.println(uri);
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            if (desktop.isSupported(Action.BROWSE)) {
                try {
                    Desktop.getDesktop().browse(uri);
                } catch (IOException ex) {
                    Messages.info(Browser.class.getName() + ": " + ex);
                }
            }
        } else {
            try {
                Runtime.getRuntime().exec(
                        "rundll32 url.dll,FileProtocolHandler " + uri);
            } catch (IOException ex) {
                Messages.info(Browser.class.getName() + ": " + ex);
            }
        }
    }

    /**
     * Deprecated: avoid using it
     *
     * @param url a file location
    
    public static void open(String url) {
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            if (desktop.isSupported(Action.BROWSE)) {
                try {
                    String os = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
                    URI uri = os.contains("win")
                            ? new URI(url.replace("\\", "/"))
                            : new URI(url);
                 
                    Desktop.getDesktop().browse(uri);
                } catch (IOException ex) {
                    Messages.severe(Browser.class
                            .getName() + ": " + ex);
                } catch (URISyntaxException ex) {
                    Messages.severe(Browser.class
                            .getName() + ": " + ex);
                }
            } else {
                try {
                    Runtime.getRuntime().exec(
                            "rundll32 url.dll,FileProtocolHandler " + url);

                } catch (IOException ex) {
                    Messages.info(Browser.class.getName() + ": " + ex);
                }
            }
        }
    }
    */
}
