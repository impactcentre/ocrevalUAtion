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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.border.Border;

/**
 *
 * @author R.C.C.
 */
public class Pulldown extends JPanel implements ActionListener {

    private static final long serialVersionUID = 1L;
    JComboBox<String> menu;
    String choice;

    public Pulldown(Color forecolor, Color bgcolor,
            Border border, String[] choices) {
        setLayout(new BorderLayout());
        setBackground(bgcolor);
        setBorder(border);
        setPreferredSize(new Dimension(20, 10));
        setVisible(true);
        menu = new JComboBox(choices);
        menu.setFont(new Font("Verdana", Font.PLAIN, 11));
        menu.addActionListener(this);
        add(menu);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        @SuppressWarnings("unchecked")
        JComboBox<String> cb = (JComboBox<String>) e.getSource();
        choice = (String) cb.getSelectedItem();
    }

    /**
     *
     * @return the option chosen
     */
    public String choice() {
        return choice;
    }
}
