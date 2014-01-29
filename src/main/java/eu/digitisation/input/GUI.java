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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author R.C.C
 */
public class GUI extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final Color green = Color.decode("#4C501E");
    private static final Color white = Color.decode("#FAFAFA");
    private static final Color gray = Color.decode("#EEEEEE");

    // Frame components
    FileSelector gtselector;
    FileSelector ocrselector;
    JPanel advanced;
    Link info;
    JPanel actions;

    /**
     * Show a warning message
     *
     * @param text the text to be displayed
     */
    public void warn(String text) {
        JOptionPane.showMessageDialog(super.getRootPane(), text, "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    // The unique constructor
    public GUI() {
        // Main container
        Container pane = getContentPane();
        // Initialization settings
        setForeground(green);
        setBackground(gray);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
        setLocationRelativeTo(null);

        // Define program parameters: input files 
        Parameter<File> gtfile = new Parameter<File>("ground-truth file");
        Parameter<File> ocrfile = new Parameter<File>("OCR file");
        Parameter<File> eqfile = new Parameter<File>("Unicode equivalences file");
        // Define program parameters: boolean options 
        Parameter<Boolean> ignoreCase = new Parameter<Boolean>("Ignore case", false, "");
        Parameter<Boolean> ignoreDiacritics = new Parameter<Boolean>("Ignore diacritics", false, "");
        Parameter<Boolean> ignorePunctuation = new Parameter<Boolean>("Ignore punctuation", false, "");
        Parameter<Boolean> compatiblity
                = new Parameter<Boolean>("Unicode compatibilty of characters", false,
                        "http://unicode.org/reports/tr15/#Canon_Compat_Equivalence");

        // Define content 
        gtselector = new FileSelector(gtfile, getForeground(), white);
        ocrselector = new FileSelector(ocrfile, getForeground(), white);
        
//        advanced = advancedOptionsPanel(ignoreCase, ignoreDiacritics, 
//                ignorePunctuation, compatibilty, eqfile);
        advanced = new JPanel(new GridLayout(0,1));
//        advanced.add();
        advanced.add(new FileSelector(eqfile, getForeground(), white));
        info = new Link("Info:",
                "https://sites.google.com/site/textdigitisation/ocrevaluation",
                getForeground());
        actions = actionsPanel(this);

        // Put all content together
        pane.add(gtselector);
        pane.add(ocrselector);
        pane.add(advanced);
        //pane.add(Box.createVerticalStrut(8));
        pane.add(info);

        pane.add(actions);

        // Show
        pack();
        setVisible(true);
    }

    /**
     * Build advanced options panel
     * @param ignoreCase
     * @param ignoreDiacritics
     * @param ignorePunctuation
     * @param compatibilty
     * @param eqfile
     * @return 
     */
    private JPanel advancedOptionsPanel(Parameter<Boolean> ignoreCase,
            Parameter<Boolean> ignoreDiacritics, 
            Parameter<Boolean> ignorePunctuation, 
            Parameter<Boolean> compatibility, Parameter<File> eqfile) {
        JPanel panel = new JPanel(new GridLayout(0, 1));
        JPanel subpanel = new JPanel(new GridLayout(0, 2));

        panel.setForeground(getForeground());
        panel.setBackground(getBackground());
        panel.setVisible(false);
        subpanel.setForeground(getForeground());
        subpanel.setBackground(getBackground());
        //subpanel.setVisible(false);

        subpanel.add(new BooleanSelector(ignoreCase, getForeground(), getBackground()));
        subpanel.add(new BooleanSelector(ignoreDiacritics, getForeground(), getBackground()));
        subpanel.add(new BooleanSelector(ignorePunctuation, getForeground(), getBackground()));
        subpanel.add(new BooleanSelector(compatibility, getForeground(), getBackground()));

        panel.add(subpanel);

        return panel;
    }

    /**
     * Creates a subpanel with two actions: "show advanced options" & "generate
     * report"
     *
     * @param gui
     * @return
     */
    private JPanel actionsPanel(final GUI gui) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        final JCheckBox more = new JCheckBox("Show advanced options");
        more.setForeground(getForeground());
        more.setBackground(Color.LIGHT_GRAY);
        more.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Dimension d = gui.getSize();
                if (more.isSelected()) {
                    gui.setSize(new Dimension(d.width, d.height + 150));
                } else {
                    gui.setSize(new Dimension(d.width, d.height - 150));
                }
                gui.advanced.setVisible(more.isSelected());
            }
        });

        // Go for it! button with inverted colors 
        JButton trigger = new JButton("Generate report");
        trigger.setForeground(getBackground());
        trigger.setBackground(getForeground());
        trigger.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //  System.out.println(gui.gtfile.getValue().getName());
                //  System.out.println(gui.ocrfile.getValue().getName());
                //  System.out.println(gui.ignoreCase.getValue());
            }
        });

        panel.add(more, BorderLayout.WEST);
        panel.add(Box.createHorizontalGlue());
        panel.add(trigger, BorderLayout.EAST);
        return panel;
    }

    public static void main(String[] args) {
        new GUI();
    }

}
