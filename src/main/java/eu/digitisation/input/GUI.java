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
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author R.C.C
 */
public class GUI extends JFrame {

    public GUI() {
        Container pane = getContentPane();

        setTitle("ocrevalUAtion");
        setBackground(Color.BLUE);
        setSize(200,200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
     //   setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
        setLocationRelativeTo(null);
        
        Option op = new Option<Boolean>("Primera");
        OptionSelector selector = new BooleanOptionSelector(op);
        JPanel panel = selector.getPanel();
        pane.add(panel);
        setVisible(true);
    }
    public static void main(String[] args) {
        new GUI();
    }
}
