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

import eu.digitisation.ocr.Report;
import java.awt.dnd.*;
import java.awt.*;
import javax.swing.*;
import java.awt.datatransfer.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.border.Border;

/**
 *
 * @author R.C.C.
 */
class FileDropArea extends JPanel {

    private static final long serialVersionUID = 1L;

    JTextArea area;  // The area to display the filename
    boolean accepted; // True if a successful drop took place
    JButton choose; // Optional file chooser

    public FileDropArea(Color color, Color bgcolor,
            Border border, String desc) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(bgcolor);
        setBorder(border);
        area = new JTextArea();
        add(area);
        area.setText("Drop here your " + desc);
        enableDragAndDrop(area);
        accepted = false;
        choose = new JButton("Or select the file");
        //choose.setPreferredSize(new Dimension(40, 10));
        choose.setForeground(color);
        choose.setBackground(bgcolor);
        choose.setFont(new Font("Verdana", Font.PLAIN, 10));  
        add(choose, BorderLayout.EAST);
    }

    public String getFileName() {
        return area.getText();
    }

    private void enableDragAndDrop(final JTextArea area) {
        DropTarget target;
        target = new DropTarget(area, new DropTargetListener() {
            @Override
            public void dragEnter(DropTargetDragEvent e) {
            }

            @Override
            public void dragExit(DropTargetEvent e) {
            }

            @Override
            public void dragOver(DropTargetDragEvent e) {
            }

            @Override
            public void dropActionChanged(DropTargetDragEvent e) {
            }

            @Override
            public void drop(DropTargetDropEvent e) {
                try {
                    // Accept the drop first!
                    e.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                    // Get files as java.util.List
                    java.util.List<File> list = (java.util.List<File>) e.getTransferable()
                            .getTransferData(DataFlavor.javaFileListFlavor);

                    File file = (File) list.get(0);
                    area.setText(file.getCanonicalPath());
                    accepted = true;
                } catch (UnsupportedFlavorException | IOException ex) {
                }
            }
        });
    }
}

public class MainGUI extends JFrame implements ActionListener {

    static final long serialVersionUID = 1L;
    static final Color bgcolor = Color.decode("#FAFAFA");
    static final Color forecolor = Color.decode("#4C501E");
    static final Border border = BorderFactory.createLineBorder(forecolor, 4);

    Container pane;  // top panel
    JFileChooser fc; // output file selector
    File[] files;  // input/output files

    public MainGUI() {
        pane = getContentPane();
        fc = new JFileChooser();
        fc.setSelectedFile(new File("output.html"));

        // frame attributes
        setTitle("Input files");
        setBackground(Color.decode("#FAFAFA"));
        setSize(400, 250);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
        //setLocationRelativeTo(null);

        // Create drop areas
        pane.add(new FileDropArea(forecolor, bgcolor,
                border, "ground-truth file"));
        pane.add(new FileDropArea(forecolor, bgcolor,
                border, "ocr file"));
        pane.add(new FileDropArea(forecolor, bgcolor,
                border, "equivalences file (if available)"));

        // Button with inverted colors
        JButton button = new JButton("Generate report");
        button.setForeground(bgcolor);
        button.setBackground(forecolor);
        button.addActionListener(this);

        pane.add(button, BorderLayout.SOUTH);
        files = new File[4];

    }

    private boolean checkInputFiles() {
        boolean ready = true;
        Component[] components = pane.getComponents();

        for (int n = 0; n < 3; ++n) {
            FileDropArea area = (FileDropArea) components[n];
            if (area.accepted) {
                files[n] = new File(area.getFileName());
            }
            if (!(area.accepted && files[n].exists())) {
                area.setBackground(Color.decode("#fffacd"));
                area.repaint();
                ready = false;
            }
        }
        return ready;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton button = (JButton) e.getSource();
        System.out.println("Frame Closed." + pane.getComponent(1));
        //dispose();

        boolean checked = checkInputFiles();
        if (checked) {
            int returnVal = fc.showOpenDialog(MainGUI.this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                files[3] = fc.getSelectedFile();
                //This is where a real application would open the file.
                Report.report(files[0], "utf8", files[1], "utf8", files[2], files[3]);
                try {
                    System.out.println("Opening: " + files[3].getCanonicalPath() + ".");
                } catch (IOException ex) {
                    Logger.getLogger(MainGUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                System.out.println("Open command cancelled by user.");
            }
        }
    }

    static public void main(String args[]) {
        MainGUI gui;
        gui = new MainGUI();
    }
}
