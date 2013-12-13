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
import eu.digitisation.io.Batch;
import eu.digitisation.ocrevaluation.Report;
import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.border.Border;

public class MainGUI2 extends JFrame implements ActionListener {

    static final long serialVersionUID = 1L;
    static final Color bgcolor = Color.decode("#FAFAFA");
    static final Color forecolor = Color.decode("#4C501E");
    static final Border border = BorderFactory.createLineBorder(forecolor, 4);
    
    Container pane;         // top panel
    JPanel actions; 
    JButton trigger;        // Go button
    JCheckBox advanced;     // Checkbox for advanced options
    File[] files;           // input/output files

    public MainGUI2() {

        pane = getContentPane();
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
        
        actions = new JPanel();
        actions.setLayout(new BoxLayout(actions, BoxLayout.X_AXIS));
        //  Switch for advanced advanced
        advanced = new JCheckBox("Advanced options");
        advanced.setForeground(forecolor);
        //options.setForeground(bgcolor);
        advanced.addActionListener(this);
        actions.add(advanced, BorderLayout.WEST);
        
        // Button with inverted colors
        trigger.setForeground(bgcolor);
        trigger.setBackground(forecolor);
        trigger.addActionListener(this);
        actions.add(trigger);

        pane.add(actions, BorderLayout.EAST);
        repaint();
    }

    /**
     *
     * @return true if all required files have been selected
     */
    private boolean checkInputFiles() {
        boolean ready = true;
        Component[] components = pane.getComponents();
        boolean[] required = {true, true, false};

        for (int n = 0; n < 3; ++n) {
            InputFileSelector ifs = (InputFileSelector) components[n];
            if (ifs.ready()) {
                files[n] = ifs.getFile();
            } else if (required[n]) {
                ifs.shade(Color.decode("#fffacd"));
                ifs.repaint();
                ready = false;
            }
        }
        return ready;
    }

    /**
     * Show a warning message
     *
     * @param text the text to be displayed
     */
    private void warning(String text) {
        InputFileSelector ifs = (InputFileSelector) pane.getComponent(0);
        ifs.setForeground(Color.RED);
        ifs.shade(Color.decode("#fffacd"));
        ifs.setText(text);
        ifs.repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
         if (e.getSource() == trigger) {
            boolean checked = checkInputFiles();
            if (checked) {
                File dir = files[1].getParentFile();
                String name = files[1].getName().replaceAll("\\.\\w+", "")
                        + "_report.html";
                File preselected = new File(name);
                OutputFileSelector selector = new OutputFileSelector();

                files[3] = selector.choose(dir, preselected);
                if (files[3] != null) {
                    try {
                        /*
                         Report report = new Report(files[0], null,
                         files[1], null,
                         files[2]);
                         */
                        Batch batch = new Batch(files[0], files[1]);
                        Report report = new Report(batch, null, null, files[2]);
                        report.write(files[3]);
                        if (Desktop.isDesktopSupported()) {
                            URI uri = new URI("file://" + files[3].getCanonicalPath());
                            System.out.println(uri);
                            Desktop.getDesktop().browse(uri);
                        }
                    } catch (InvalidObjectException ex) {
                        warning(ex.getMessage());
                    } catch (URISyntaxException ex) {
                        Logger.getLogger(MainGUI2.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(MainGUI2.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        } else if (e.getSource() == advanced) { 
            boolean marked = advanced.isSelected();
            if (marked) {
                setSize(400, 300);
            } else {
                setSize(400, 200);
            }
            pane.getComponent(2).setVisible(marked);
            
        }
    }

    static public void main(String args[]) {
        new MainGUI2();
    }
}
