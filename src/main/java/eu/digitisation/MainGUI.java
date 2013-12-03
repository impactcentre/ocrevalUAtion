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
package eu.digitisation;

import eu.digitisation.gui.InputFileSelector;
import eu.digitisation.gui.OutputFileSelector;
import eu.digitisation.gui.Pulldown;
import eu.digitisation.ocr.Report;
import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import javax.swing.border.Border;

public class MainGUI extends JFrame implements ActionListener {

    static final long serialVersionUID = 1L;
    static final Color bgcolor = Color.decode("#FAFAFA");
    static final Color forecolor = Color.decode("#4C501E");
    static final Border border = BorderFactory.createLineBorder(forecolor, 4);
    Container pane;         // top panel
    Pulldown encodingsMenu; // a pulldowwn menu for dteh deafult encoding
    JButton trigger;        // Go button
    File[] files;           // input/output files

    public MainGUI() {
        String[] encodings = {"If you know the encoding of .txt files, select it here",
            "UTF8", "ISO-8859-1", "Windows-1252"};

        pane = getContentPane();
        encodingsMenu = new Pulldown(forecolor, bgcolor, border, encodings);
        trigger = new JButton("Generate report");
        files = new File[4];

        // frame attributes
        setTitle("Input files");
        setBackground(Color.decode("#FAFAFA"));
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BoxLayout(pane, BoxLayout.PAGE_AXIS));
        setLocationRelativeTo(null);
        setVisible(true);

        // Create drop areas
        pane.add(new InputFileSelector(forecolor, bgcolor,
                border, "ground-truth file"));
        pane.add(new InputFileSelector(forecolor, bgcolor,
                border, "ocr file"));
        pane.add(new InputFileSelector(forecolor, bgcolor,
                border, "Unicode character equivalences file (if available)"));


        // Default encoding selector    
        pane.add(encodingsMenu);

        // Button with inverted colors
        trigger.setForeground(bgcolor);
        trigger.setBackground(forecolor);
        trigger.addActionListener(this);
        pane.add(trigger);

        repaint();
    }

    private boolean checkInputFiles() {
        boolean ready = true;
        Component[] components = pane.getComponents();

        for (int n = 0; n < 2; ++n) {
            InputFileSelector area = (InputFileSelector) components[n];
            if (area.accepted()) {
                files[n] = new File(area.getFileName());
            }
            if (!(area.accepted() && files[n].exists())) {
                area.setBackground(Color.decode("#fffacd"));
                area.repaint();
                ready = false;
            }
        }
        return ready;
    }

    private File choose(String defaultName) {
        JFileChooser chooser = new JFileChooser();

        chooser.setSelectedFile(new File(defaultName));
        int returnVal = chooser.showOpenDialog(MainGUI.this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile();
        } else {
            return null;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton pressed = (JButton) e.getSource();

        if (pressed == trigger) {
            String encoding = encodingsMenu.choice() == null
                    ? System.getProperty("file.encoding")
                    : encodingsMenu.choice();
            boolean checked = checkInputFiles();
            if (checked) {          
                    File dir = files[1].getParentFile();
                    String name = files[1].getName().replaceAll("\\.\\w+","")
                            + "_report.html";
                    File prefile = new File(name);
                    OutputFileSelector selector = new OutputFileSelector();
                    
                    files[3] = selector.choose(dir, prefile);
                    if (files[3] != null) {
                        Report.report(files[0], encoding,
                                files[1], encoding,
                                files[2], files[3]);
                    }
               
            }
        }
    }

    static public void main(String args[]) {
        MainGUI gui;
        gui = new MainGUI();
    }
}
