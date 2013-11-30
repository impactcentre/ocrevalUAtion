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
package eu.digitisation.input;

import java.awt.dnd.*;
import java.awt.*;
import javax.swing.*;
import java.awt.datatransfer.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

/**
 *
 * @author R.C.C.
 */
public class DragAndDrop extends JFrame implements ActionListener {

    static final long serialVersionUID = 1L;
    File gtfile;
    File ocrfile;
    File repfile;
    JButton button;
    JPanel upper;
    JPanel middle;
    JPanel lower;

    public DragAndDrop() {
        setTitle("Input files");
        setSize(400, 250);
        setVisible(true);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        createAndShowGUI();
    }

    private void createAndShowGUI() {
        Container pane = getContentPane();

        // Button
        button = new JButton("Go");
        button.addActionListener(this);
        pane.add(button, "East");

        // Create JPanels 
        upper = new JPanel();
        middle = new JPanel();
        lower = new JPanel();

        pane.add(upper, "North");
        pane.add(middle);
        pane.add(lower, "South");

        // Create JTextAreas
        JTextArea gt = new JTextArea("Drop here your ground truth file", 4, 10);
        upper.add(gt);
        gt.setText("Drop here your ground truth file");
        enableDragAndDrop(gt);

        JTextArea ocr = new JTextArea("Drop here your ocr file", 4, 10);
        middle.add(ocr);
        ocr.setText("Drop here your ocr file");
        enableDragAndDrop(ocr);

        JTextArea rep = new JTextArea("Drop your replacements file", 4, 10);
        lower.add(rep);
        rep.setText("Drop here your replacements file");
        enableDragAndDrop(rep);

        //setLocationRelativeTo(null);
    }

    private void enableDragAndDrop(final JTextArea area) {
        DropTarget target = new DropTarget(area, new DropTargetListener() {
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
                    // Accept the drop first, important!
                    e.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                    // Get the files that are dropped as java.util.List
                    java.util.List<File> list
                            = (java.util.List<File>) e.getTransferable()
                            .getTransferData(DataFlavor.javaFileListFlavor);

                    File file = (File) list.get(0);
                    //                    gt.read(new FileReader(file),null);
                    area.setText(file.getName());
                } catch (UnsupportedFlavorException | IOException ex) {
                }
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        button = (JButton) e.getSource();
        dispose();
        System.out.println("Frame Closed.");
    }

    static public void main(String args[]) {
        new DragAndDrop();
    }
}
