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
package eu.digitisation.gui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.net.URI;
import java.net.URISyntaxException;
import javax.swing.JButton;
import javax.swing.JOptionPane;

/**
 *
 * @author R.C.C.
 */
public class HelpButton extends JButton {

    private static final long serialVersionUID = 1L;
    String text;  // help text
    URI uri;      // URI with extended help

    /**
     * Default constructor
     *
     * @param helpText the help text
     * @param helpUri the address with further information
     * @param forecolor foreground color
     * @param bgcolor background color
     */
    public HelpButton(String helpText, String helpUri, Color forecolor, Color bgcolor) {
        super("?");
        setPreferredSize(new Dimension(10, 10));
        setContentAreaFilled(false);
        setForeground(forecolor);
        setBackground(bgcolor);

        this.text = helpText;
        try {
            this.uri = new URI(helpUri);
        } catch (URISyntaxException ex) {
            this.uri = null;
        }

        addActionListener(new ActionListener() {
            Container container = getParent();

            @Override
            public void actionPerformed(ActionEvent e) {
                if (text != null) {
                    JOptionPane.showMessageDialog(container, text);
                }
                if (uri != null) {
                    Browser.open(uri);
                }
            }
        });
    }

    /**
     * Default constructor
     *
     * @param help the help text or address with further information
     * @param forecolor foreground color
     * @param bgcolor background color
     */
    public HelpButton(String help, Color forecolor, Color bgcolor) {
        this(help.startsWith("http") ? null : help,
                help.startsWith("http") ? help : null,
                forecolor, bgcolor);
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (getModel().isArmed()) {
            g.setColor(Color.lightGray);
        } else {
            g.setColor(getBackground());
        }
        g.fillOval(7, 0, getSize().width - 16, getSize().height - 1);

        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {
        g.setColor(getForeground());
        g.drawOval(7, 0, getSize().width - 16, getSize().height - 1);
    }

    Shape shape;

    @Override
    public boolean contains(int x, int y) {
        if (shape == null
                || !shape.getBounds().equals(getBounds())) {
            shape = new Ellipse2D.Float(7, 0, getWidth() - 7, getHeight());
        }
        return shape.contains(x, y);
    }
}
