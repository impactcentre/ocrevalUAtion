/*
 * Copyright (C) 2014 Universidad de Alicante
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

import eu.digitisation.image.Bimage;
import eu.digitisation.image.Display;
import eu.digitisation.layout.Projections;
import java.awt.Color;
import java.awt.Graphics2D;
import java.io.File;

/**
 *
 * @author R.C.C
 */
public class Rotate {

    private static Bimage rotate(Bimage bim, double alpha) {
        System.out.println(bim.getWidth() + "," + bim.getHeight());
        double cos = Math.cos(alpha);
        double sin = Math.sin(alpha);
        int w = (int) Math.floor(bim.getWidth() * cos + bim.getHeight() * sin);
        int h = (int) Math.floor(bim.getHeight() * cos + bim.getWidth() * sin);
        Bimage rotated = new Bimage(w, h, bim.getType());
        System.out.println(rotated.getWidth() + "," + rotated.getHeight());
        Graphics2D g = (Graphics2D) rotated.getGraphics();
        g.setBackground(Color.WHITE);
        g.clearRect(0, 0, w, h);
        g.translate(bim.getHeight() * sin, 0);
        g.rotate(alpha);
        g.drawImage(bim, 0, 0, null);
        g.dispose();
        return rotated;
    }

    public static void main(String[] args) throws Exception {
        String ifname = args[0];
        String ofname = args[1];
        File ifile = new File(ifname);
        File ofile = new File(ofname);
        Projections p = new Projections(ifile);
        //double alpha = p.skew();
        //System.out.println("Image rotation="+alpha);
        //p.slice();
        Bimage rotated = p.rotate(Math.PI / 6);
        rotated.write(ofile);
        System.err.println("Output image in " + ofname);
        Display.draw(rotated, rotated.getWidth(), rotated.getHeight());
    }
}
