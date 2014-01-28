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
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 *
 * @author R.C.C
 */
class BooleanOptionSelector implements OptionSelector {

    Option option;

    public BooleanOptionSelector(Option op) {
        option = op;
    }

    @Override
    public Option getOption() {
        return option;
    }

    @Override
    public JPanel getPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.RED);
        panel.add(new JButton()); //test
        return panel;
    }
}
