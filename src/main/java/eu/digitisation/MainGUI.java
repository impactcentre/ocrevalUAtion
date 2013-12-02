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

/**
 *
 * @author R.C.C.
 */
public class MainGUI extends JFrame implements ActionListener {

    static final long serialVersionUID = 1L;
    File gtfile;
    File ocrfile;
    File repfile;
    File ofile;
    JButton button;
    JPanel upper;
    JPanel middle;
    JPanel lower;
    JTextArea gt;
    JTextArea ocr;
    JTextArea rep;
    JFileChooser fc;

    public MainGUI() {

        setTitle("Input files");
        setSize(400, 250);
        setVisible(true);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        createAndShowGUI();
        fc = new JFileChooser();
    }

    private void createAndShowGUI() {
        Container pane = getContentPane();
        Color bgcolor = new Color(200, 240, 255);

        //pane.setBackground((Color.BLUE));
        // Button
        button = new JButton("Go");
        button.addActionListener(this);
        pane.add(button, BorderLayout.EAST);

        // Create JPanels 
        upper = new JPanel();
        upper.setBackground(bgcolor);
        middle = new JPanel();
        middle.setBackground(bgcolor);
        lower = new JPanel();
        lower.setBackground(bgcolor);
        pane.add(upper, BorderLayout.NORTH);
        pane.add(middle, BorderLayout.CENTER);
        pane.add(lower, BorderLayout.SOUTH);

        // Create JTextAreas
        gt = new JTextArea(4, 10);
        upper.add(gt);
        gt.setText("Drop here your ground truth file");
        enableDragAndDrop(gt);

        ocr = new JTextArea(4, 10);
        middle.add(ocr);
        ocr.setText("Drop here your ocr file");
        enableDragAndDrop(ocr);

        rep = new JTextArea(4, 10);
        lower.add(rep);
        rep.setText("Drop here your replacements file");
        enableDragAndDrop(rep);

        //setLocationRelativeTo(null);
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
                } catch (UnsupportedFlavorException | IOException ex) {
                }
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        button = (JButton) e.getSource();
        dispose();
        gtfile = new File(gt.getText());
        ocrfile = new File(ocr.getText());
        repfile = new File(rep.getText());
        System.out.println("Frame Closed." + gtfile);

        int returnVal = fc.showOpenDialog(MainGUI.this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            ofile = fc.getSelectedFile();
            //This is where a real application would open the file.
           Report.report(gtfile, "utf8", ocrfile, "utf8", repfile, ofile);
            try {
                System.out.println("Opening: " + ofile.getCanonicalPath()+ ".");
            } catch (IOException ex) {
                Logger.getLogger(MainGUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            System.out.println("Open command cancelled by user.");
        }
    }

    static public void main(String args[]) {
        MainGUI gui;
        gui = new MainGUI();
    }
}
