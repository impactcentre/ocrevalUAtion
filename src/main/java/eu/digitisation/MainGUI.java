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

import eu.digitisation.gui.Browser;
import eu.digitisation.gui.HelpButton;
import eu.digitisation.gui.InputFileSelector;
import eu.digitisation.gui.JLink;
import eu.digitisation.gui.OutputFileSelector;
import eu.digitisation.io.Batch;
import eu.digitisation.io.CharFilter;
import eu.digitisation.io.WarningException;
import eu.digitisation.ocrevaluation.Report;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;

/**
 * Public interface for ocrevaluation tool
 */
public class MainGUI extends JFrame implements ActionListener {

    static final long serialVersionUID = 1L;
    static final Color bgcolor = Color.decode("#FAFAFA");
    static final Color forecolor = Color.decode("#4C501E");
    static final Border border = BorderFactory.createLineBorder(forecolor, 2);
    static final int width = 500;
    static final int height = 250;
    Container pane;            // main panel
    JPanel basic;              // basic inputs
    JPanel info;               // link to info
    JPanel advanced;           // more options panel
    JPanel actions;            // actions panel
    InputFileSelector gtinput; // GT file
    InputFileSelector ocrinput;// OCR file
    InputFileSelector eqinput; // equivalences file
    JCheckBox compatibility;   //  Unicode comaptiblity mode
    JButton help;              // help button
    JButton trigger;           // Go button
    JCheckBox more;            // Checkbox for more options

    public MainGUI() {
        // Main panel
        pane = getContentPane();

        // JFrame attributes
        setTitle("ocrevalUAtion");
        setBackground(bgcolor);
        setSize(width, height);
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

        // Link to on-line help
        info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.X_AXIS));
        info.add(new JLink("Info:",
                "https://sites.google.com/site/textdigitisation/ocrevaluation",
                forecolor));

        // Advanced options subpanel
        advanced = new JPanel();
        advanced.setLayout(new GridLayout(0, 1));
        advanced.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        // eqfile selector
        eqinput = new InputFileSelector(forecolor, bgcolor,
                border, "Unicode character equivalences file (if available)");
        // Compatibility option
        compatibility = new JCheckBox();
        compatibility.setText("Unicode compatibility of characters");
        compatibility.setForeground(forecolor);
        compatibility.setBackground(bgcolor);
        compatibility.setAlignmentX(Component.LEFT_ALIGNMENT);
        help = new HelpButton(null,
                "http://unicode.org/reports/tr15/#Canon_Compat_Equivalence", forecolor, bgcolor);
        JPanel cpanel = new JPanel();
        cpanel.setLayout(new BoxLayout(cpanel, BoxLayout.X_AXIS));
        cpanel.add(compatibility);
        cpanel.add(help);

        advanced.add(eqinput);
        advanced.add(cpanel);
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
        trigger = new JButton("Generate report");
        trigger.setForeground(bgcolor);
        trigger.setBackground(forecolor);
        trigger.addActionListener(this);
        actions.add(trigger);

        // Finally, put everything together
        pane.add(basic);
        pane.add(advanced);
        pane.add(Box.createVerticalStrut(8));
        pane.add(info);
        pane.add(Box.createVerticalStrut(8));
        pane.add(actions);
        setVisible(true);
    }

    /**
     * Show a warning message
     *
     * @param text the text to be displayed
     */
    private void warning(String text) {
        JOptionPane.showMessageDialog(rootPane, text, "Error",
                JOptionPane.ERROR_MESSAGE);
        /*
         InputFileSelector ifs = (InputFileSelector) basic.getComponent(0);
         ifs.setForeground(Color.RED);
         ifs.shade(Color.decode("#fffacd"));
         ifs.setText(text);
         ifs.repaint();
         */
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == trigger) {
            try {
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
                            Batch batch = new Batch(gtfile, ocrfile);
                            CharFilter filter = (eqfile == null)
                                    ? new CharFilter()
                                    : new CharFilter(eqfile);
                            String url = "file://" + outfile.getCanonicalPath();
                            filter.setCompatibility(compatibility.isSelected());
                            report = new Report(batch, null, null, filter);
                            report.write(outfile);
                            Browser.open(url);
                        } catch (WarningException ex) {
                            warning(ex.getMessage());
                        } catch (IOException ex) {
                            warning("Input/Output Error");
                        }
                    }
                } else {
                    gtinput.checkout();
                    ocrinput.checkout();
                }
            } catch (Exception ex) {
                System.err.println(ex.getMessage());
            }
        } else if (e.getSource() == more) {
            boolean marked = more.isSelected();
            if (marked) {
                setSize(width, height + 80);
            } else {
                setSize(width, height);
            }
            advanced.setVisible(marked);
        }
    }

    static public void main(String args[]) {
        new MainGUI();
    }
}
