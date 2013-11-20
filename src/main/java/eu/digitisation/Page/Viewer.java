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
import eu.digitisation.Image.Bimage;
import eu.digitisation.Image.GroundTruth;

/**
 *
 * @author R.C.C
 */
public class Viewer {

    public static void main(String[] args) throws IOException {
        File xmlfile = new File("00445310.xml");
        File ifile = new File("00445310.jpg");
        File ofile = new File("00445310_page.jpg");
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
        scaled = new Bimage(page, 0.25);
        //Display.draw(scaled);
        scaled.write(ofile, "jpg");
        System.err.println("output=" + ofile);
    }
}
