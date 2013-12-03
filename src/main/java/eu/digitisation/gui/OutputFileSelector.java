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

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * File chooser with confirmation dialog to avoid accidental overwrite
 *
 * @author R.C.C.
 */
public class OutputFileSelector extends JFileChooser {

    private static final long serialVersionUID = 1L;

    public OutputFileSelector() {
        super();
    }

    /**
     *
     * @param dir the default directory
     * @param file the preselected file
     * @return the selected file
     */
    public File choose(File dir, File file) {
        setCurrentDirectory(dir);
        setSelectedFile(file);
        int returnVal = showOpenDialog(OutputFileSelector.this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = getSelectedFile();
            if (file != null && file.exists()) {
                int response = JOptionPane.showConfirmDialog(new JFrame().getContentPane(),
                        "The file " + file.getName()
                        + " already exists. Do you want to replace the existing file?",
                        "Overwrite file", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

                return (response == JOptionPane.YES_NO_OPTION) ? file : null;
            }
            return file;
        }
        return null;
    }
}
