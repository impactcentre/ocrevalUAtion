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

import eu.digitisation.gui.HelpButton;
import java.awt.Color;
import java.awt.event.ActionEvent;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;

/**
 *
 * @author R.C.C
 */
class BooleanOptionSelector extends OptionSelector {

    JCheckBox box;

    public BooleanOptionSelector(Option<Boolean> op, Color forecolor, Color bgcolor) {
        super(op, forecolor, bgcolor);
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        box = new JCheckBox(op.name);
        add(box);
        if (op.help != null) {
            add(new Help(op.help, forecolor, bgcolor));
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        option.setValue(box.isSelected());
    }
}
