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
import eu.digitisation.io.CharFilter;
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

public class MainGUI extends JFrame implements ActionListener {

    static final long serialVersionUID = 1L;
    static final Color bgcolor = Color.decode("#FAFAFA");
    static final Color forecolor = Color.decode("#4C501E");
    static final Border border = BorderFactory.createLineBorder(forecolor, 2);

    Container pane;            // main panel
    JPanel basic;              // basic inputs
    JPanel advanced;           // more options panel
    JPanel actions;            // actions panel

    InputFileSelector gtinput; // GT file
    InputFileSelector ocrinput;// OCR file
    InputFileSelector eqinput; // equivalences file
    JCheckBox compatibility;   //  Unicode comaptiblity mode
    JButton trigger;           // Go button
    JCheckBox more;        // Checkbox for more options

    public MainGUI() {

        pane = getContentPane();
        trigger = new JButton("Generate report");

        // JFrame attributes
        setTitle("Input files");
        setBackground(bgcolor);
        setSize(400, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
        setLocationRelativeTo(null);

        // Basic input subpanel
        basic = new JPanel();
        basic.setLayout(new GridLayout(0, 1));
        gtinput = new InputFileSelector(forecolor, bgcolor,
                border, "ground-truth file");
        ocrinput = new InputFileSelector(forecolor, bgcolor,
                border, "ocr file");
        basic.add(gtinput);
        basic.add(ocrinput);

        // Advanced options subpanel
        advanced = new JPanel();
        advanced.setLayout(new GridLayout(0, 1));
        advanced.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        eqinput = new InputFileSelector(forecolor, bgcolor,
                border, "Unicode character equivalences file (if available)");

        compatibility = new JCheckBox();
        compatibility.setText("Unicode compatibility of characters");
        compatibility.setForeground(forecolor);
        compatibility.setBackground(bgcolor);
        compatibility.setAlignmentX(Component.LEFT_ALIGNMENT);
        /*
         String[] options = {"unknown", "utf8", "iso8859-1", "windows-1252"};
         Pulldown encoding = new Pulldown(forecolor, bgcolor, null,
         "Text encoding:", options);
         */
        advanced.add(eqinput);
        advanced.add(compatibility);
        //advanced.add(encoding);
        advanced.setVisible(false);

        // Actions subpanel
        actions = new JPanel();
        actions.setLayout(new BoxLayout(actions, BoxLayout.X_AXIS));
        actions.setBackground(Color.LIGHT_GRAY);
        //  Switch for more more
        more = new JCheckBox("Advanced options");
        more.setForeground(forecolor);
        more.setBackground(Color.LIGHT_GRAY);
        more.addActionListener(this);
        actions.add(more, BorderLayout.WEST);
        // Space between checkbox and button
        actions.add(Box.createHorizontalGlue());
        // Button with inverted colors
        trigger.setForeground(bgcolor);
        trigger.setBackground(forecolor);
        trigger.addActionListener(this);
        actions.add(trigger);

        // Fianlly, put everything together
        pane.add(basic);
        pane.add(advanced);
        pane.add(actions);
        setVisible(true);
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
            if (gtinput.ready() && ocrinput.ready()) {
                File gtfile = gtinput.getFile();
                File ocrfile = ocrinput.getFile();
                File eqfile = eqinput.getFile();
                File dir = ocrfile.getParentFile();
                String name = ocrfile.getName().replaceAll("\\.\\w+", "")
                        + "_report.html";
                File preselected = new File(name);
                OutputFileSelector selector = new OutputFileSelector();
                File outfile = selector.choose(dir, preselected);

                if (outfile != null) {
                    Report report;
                    try {
                        /*
                         Report report = new Report(files[0], null,
                         files[1], null,
                         files[2]);
                         */
                        Batch batch = new Batch(gtfile, ocrfile);
                        CharFilter filter = (eqfile == null)
                                ? new CharFilter()
                                : new CharFilter(eqfile);
                        filter.setCompatibility(compatibility.isSelected());
                        report = new Report(batch, null, null, filter);
                        report.write(outfile);
                        if (Desktop.isDesktopSupported()) {
                            URI uri = new URI("file://" + outfile.getCanonicalPath());
                            System.out.println(uri);
                            Desktop.getDesktop().browse(uri);
                        }
                    } catch (InvalidObjectException ex) {
                        warning(ex.getMessage());
                    } catch (URISyntaxException ex) {
                        Logger.getLogger(MainGUI.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(MainGUI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } else {
                gtinput.checkout();
                ocrinput.checkout();
            }
        } else if (e.getSource() == more) {
            boolean marked = more.isSelected();
            if (marked) {
                setSize(400, 300);
            } else {
                setSize(400, 200);
            }
            advanced.setVisible(marked);
        }
    }

    static public void main(String args[]) {
        new MainGUI();
    }
}
