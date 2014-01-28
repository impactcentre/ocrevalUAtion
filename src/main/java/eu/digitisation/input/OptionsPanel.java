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
package eu.digitisation.input;

import java.awt.Color;
import java.io.File;
import java.util.List;
import javax.swing.JPanel;

/**
 *
 * @author R.C.C.
 */
public class OptionsPanel extends JPanel {

    List<Option> options;
    List<OptionSelector> selectors;
    private static final long serialVersionUID = 1L;

    public OptionsPanel(Color forecolor, Color bgcolor, boolean visible) {
        super.setForeground(forecolor);
        super.setBackground(bgcolor);
        super.setVisible(visible);
    }

    public void addOption(Option op) {
        OptionSelector selector = null;
        Class type = op.getType();
        if (type == Boolean.class) {
            selector = new BooleanOptionSelector(op);

        } else if (type == File.class) {
            selector = new FileOptionSelector(op);
        }
         selectors.add(selector);
    }
}
