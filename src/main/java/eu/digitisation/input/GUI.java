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

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.io.File;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;

/**
 *
 * @author R.C.C
 */
public class GUI extends JFrame {

    public final void init(String title, int width, int height) {
        setTitle(title);
        setSize(width, height);
        setForeground(Color.decode("#4C501E"));
        setBackground(Color.decode("#FAFAFA"));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        setLocationRelativeTo(null);
    }

    public GUI() {
        // initialization seetings
        init("ocrevaluation", 400, 300);

        // Add content
        Container pane = getContentPane();

        Option op = new Option<Boolean>("Primera", "http:www.ua.es");
        OptionSelector selector =
                new BooleanOptionSelector(op, getForeground(), getBackground());
        pane.add(selector);

        op = new Option<File>("GT", "ayuda");
        selector = new FileOptionSelector(op, getForeground(), getBackground());
        pane.add(selector);

        op = new Option<File>("OCR", "ayuda");
        selector = new FileOptionSelector(op, getForeground(), getBackground());
        pane.add(selector);
        // Show
        setVisible(true);
    }

    public static void main(String[] args) {
        new GUI();
    }
}
