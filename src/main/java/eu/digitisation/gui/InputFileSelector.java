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
package eu.digitisation.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.border.Border;

/**
 * Graphical interface to select input files with both drag and drop or menu
 * selection.
 *
 * @author R.C.C.
 */
public class InputFileSelector extends JPanel implements ActionListener {

    private static final long serialVersionUID = 1L;
    JTextPane area;  // The area to display the filename
    boolean accepted; // True if a successful drop took place
    JButton choose; // Optional file chooser

    public InputFileSelector(Color color, Color bgcolor,
            Border border, String desc) {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBackground(bgcolor);
        setBorder(border);
        area = new JTextPane();
        add(area);
        area.setFont(new Font("Verdana", Font.PLAIN, 12));
        area.setForeground(color);
        area.setText("Drop here your " + desc);
        enableDragAndDrop(area);
        accepted = false;
        choose = new JButton("Or select the file");
        //choose.setPreferredSize(new Dimension(40, 10));
        choose.setForeground(color);
        choose.setBackground(bgcolor);
        choose.setFont(new Font("Verdana", Font.PLAIN, 10));
        choose.addActionListener(this);
        add(choose, BorderLayout.EAST);
    }

    public String getFileName() {
        return area.getText();
    }

    public boolean accepted() {
        return accepted;
    }

    private void enableDragAndDrop(final JTextPane area) {
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
                    java.util.List<File> list;

                    list = (java.util.List<File>) e.getTransferable()
                            .getTransferData(DataFlavor.javaFileListFlavor);
                    File file = (File) list.get(0);
                    area.setText(file.getCanonicalPath());
                    accepted = true;
                } catch (IOException ex) {
                    Logger.getLogger(InputFileSelector.class.getName()).log(Level.SEVERE, null, ex);
                } catch (UnsupportedFlavorException ex) {
                    Logger.getLogger(InputFileSelector.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton pressed = (JButton) e.getSource();
        if (pressed == choose) {
            File file = choose("input_file");
            if (file != null) {
                try {
                    area.setText(file.getCanonicalPath());
                } catch (IOException ex) {
                    Logger.getLogger(InputFileSelector.class.getName()).log(Level.SEVERE, null, ex);
                }
                accepted = true;
            }
        }
    }

    /**
     * Select file with a file selector (menu)
     *
     * @param defaultName
     * @return
     */
    private File choose(String defaultName) {
        JFileChooser chooser = new JFileChooser();

        chooser.setDialogTitle("Select input file");
        chooser.setSelectedFile(new File(defaultName));
        int returnVal = chooser.showOpenDialog(InputFileSelector.this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile();
        } else {
            return null;
        }
    }
}
