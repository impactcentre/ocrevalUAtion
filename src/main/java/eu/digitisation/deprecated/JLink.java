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
package eu.digitisation.deprecated;

import eu.digitisation.output.Browser;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;

/**
 *
 * @author R.C.C
 */
public class JLink extends JLabel {
    private static final long serialVersionUID = 1L;
    
    /**
     * Basic constructor
     * @param title the text to be shown
     * @param url the linked URL 
     * @param color the color of the link
     */
    public JLink(final String title, final String url, Color color) {
        super();
        setPreferredSize(new Dimension(400,10));
        setText("<html><body>" + title
                + "<a style=\"color:#4c501E\" href=\""
                + url + "\">" + url
                + "</a></body></html>");
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Browser.open(url);
            }
        });
    }
}
