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
package eu.digitisation.gui;

import eu.digitisation.Option;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EnumMap;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.JPanel;

/**
 *
 * @author R.C.C.
 */
public class OptionSelector extends JPanel implements ActionListener {

    private static final long serialVersionUID = 1L;
    EnumMap<Option, JCheckBox> boxes = new EnumMap<Option, JCheckBox>(Option.class);

    public OptionSelector(Color forecolor, Color bgcolor) {
        setLayout(new GridLayout(0, 1));
        setAlignmentX(Component.LEFT_ALIGNMENT);
        for (Option op : Option.values()) {
            JPanel panel = new JPanel();
            JCheckBox cb = new JCheckBox();
            String helpText = op.getHelp();

            boxes.put(op, cb);
            cb.setText(op.toString());
            cb.setForeground(forecolor);
            cb.setBackground(bgcolor);
            cb.setAlignmentX(Component.LEFT_ALIGNMENT);
            cb.addActionListener(this);
            panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
            panel.add(cb);
            if (helpText != null) { // some help available
                HelpButton hb = new HelpButton(helpText, forecolor, bgcolor);
                panel.add(hb);
                
            }
            add(panel);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for (Option op : Option.values()) {
            JCheckBox cb = boxes.get(op);
            if (e.getSource() == cb) {
                op.setValue(cb.isSelected());
            }
        }
    }

    /**
     * Main for testing purposes
     * @param args 
     */
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        Container pane = frame.getContentPane();
        OptionSelector selector = new OptionSelector(Color.GRAY, Color.WHITE);

        frame.setTitle("ocrevalUAtion");

        frame.setSize(500, 150);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
        frame.setLocationRelativeTo(null);
        pane.add(selector);
        frame.setVisible(true);
    }
}
