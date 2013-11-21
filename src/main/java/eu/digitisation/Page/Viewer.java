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
package eu.digitisation.Page;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import eu.digitisation.image.Bimage;
import eu.digitisation.image.GroundTruth;

/**
 *
 * @author R.C.C
 */
public class Viewer {

    /**
     * Split a file name in basename and extension
     *
     * @param filename
     * @return basename (before last dot) and extension (after last dot)
     */
    private static String[] getFilenameTokens(String filename) {
        String[] tokens = new String[2];
        int pos = filename.lastIndexOf('.');
        tokens[0] = filename.substring(0, pos);
        tokens[1] = filename.substring(pos + 1);
        return tokens;
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("usage: Viewer image_file");
            System.exit(0);
        }
        String[] tokens = getFilenameTokens(args[0]);
        File ifile =  new File(args[0]);
        File xmlfile =  new File(tokens[0] + ".xml");
        File ofile = new File(tokens[0] + "_page." + tokens[1]);
        Bimage page = null;
        Bimage scaled;
        GroundTruth gt = null;

        if (ifile.exists()) {
            try {
                page = new Bimage(ifile).toRGB();
            } catch (NullPointerException ex) {
                throw new IOException("Unsupported format");
            }
        } else {
            throw new java.io.IOException(ifile + " not found");
        }
        if (xmlfile.exists()) {
            gt = new GroundTruth(xmlfile);
        } else {
            throw new java.io.IOException(xmlfile + " not found");
        }

        page.add(gt.getTextRegions(), Color.RED, 4);
        page.add(gt.getLines(), Color.GREEN, 2);
        page.add(gt.getWords(), Color.BLUE, 1);
        scaled = new Bimage(page, 1.0);
        //Display.draw(scaled);
        scaled.write(ofile, "tiff");
        System.err.println("output=" + ofile);
    }
}