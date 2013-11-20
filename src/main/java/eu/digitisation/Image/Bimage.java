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
package eu.digitisation.Image;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.color.ColorSpace;
import java.awt.image.ColorConvertOp;
import java.io.IOException;

/**
 * Extends BufferedImage with some useful operations
 *
 * @author R.C.C.
 */
public class Bimage extends BufferedImage {

    /**
     * Default constructor
     *
     * @param width
     * @param height
     * @param imageType
     */
    public Bimage(int width, int height, int imageType) {
        super(width, height, imageType);
    }

    /**
     * Create a BufferedImage from an Image
     *
     * @param image the source image
     */
    public Bimage(BufferedImage image) {
        super(image.getWidth(null), image.getHeight(null),
                image.getType());
        Graphics2D g = createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
    }

    /**
     * Crete image from file content Supported formats are bmp, jpg, wbmp, jpeg,
     * png, gif
     *
     * @param file the file storing the image
     * @throws IOException
     * @throws NullPointerException if the file format is unsupported
     */
    public Bimage(java.io.File file) throws IOException {
        this(javax.imageio.ImageIO.read(file));
    }

    /**
     * Create a scaled image
     *
     * @param img the source image
     * @param scale the scale factor
     */
    public Bimage(BufferedImage img, double scale) {
        super((int) (scale * img.getWidth()),
                (int) (scale * img.getHeight()),
                img.getType());
        int hints = java.awt.Image.SCALE_SMOOTH; //scaling algorithm
        Image scaled = img.getScaledInstance(this.getWidth(),
                this.getHeight(),
                hints);
        Graphics2D g = createGraphics();
        g.drawImage(scaled, 0, 0, null);
        g.dispose();
    }

    /**
     * Create a new image form two layers (with the type of first)
     *
     * @param first the first source image
     * @param second the second source image
     */
    public Bimage(BufferedImage first, BufferedImage second) {
        super(Math.max(first.getWidth(), second.getWidth()),
                Math.max(first.getHeight(), second.getHeight()),
                first.getType());
        BufferedImage combined = new BufferedImage(this.getWidth(),
                this.getHeight(),
                this.getType());
        Graphics2D g = combined.createGraphics();
        g.drawImage(first, 0, 0, null);
        g.drawImage(second, 0, 0, null);
        g.dispose();
    }

    /**
     * Transform image to gray-scale
     *
     * @return this image as gray-scale image
     */
    public Bimage toGrayScale() {
        ColorSpace space = ColorSpace.getInstance(ColorSpace.CS_GRAY);
        ColorConvertOp operation = new ColorConvertOp(space, null);
        return new Bimage(operation.filter(this, null));
    }

    /**
     * Transform image to RGB
     *
     * @return this image as gray-scale image
     */
    public Bimage toRGB() {
        /*
         ColorSpace space
         = ColorSpace.getInstance(ColorSpace.CS_sRGB);
         ColorConvertOp operation = new ColorConvertOp(space, null);
         return new Bimage(operation.filter(this, null));
         */
        Bimage bim = 
                new Bimage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bim.createGraphics();
        g.drawImage(this, 0, 0, null);
        g.dispose();
        return bim;
    }

    /**
     * Clear the image to white
     */
    public void clear() {
        Graphics2D g = createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.dispose();
    }

    /**
     * Add a polygonal frontier to the image
     *
     * @param p a polygon
     * @param color the color of the polygon
     * @param stroke the line width
     */
    public void add(Polygon p, Color color, int stroke) {
        Graphics2D g = createGraphics();
        g.setColor(color);
        g.setStroke(new BasicStroke(stroke));
        g.drawPolygon(p);
        g.dispose();
    }

    public void add(TextRegion region, Color color, int stroke) {
        Graphics2D g = createGraphics();
        g.setColor(color);
        g.setStroke(new BasicStroke(stroke));
        g.drawPolygon(region);
        g.dispose();
    }

    /**
     * Add a polygonal frontier to the image
     *
     * @param regions an array of polygonal regions
     * @param color he color of the polygons
     * @param stroke the line width
     */
    public void add(Polygon[] regions, Color color, int stroke) {
        for (Polygon p : regions) {
            add(p, color, stroke);
        }
    }

    /**
     * @param file the output file
     * @param format the format (PNG, JPG, GIF, BMP)
     * @throws java.io.IOException
     */
    public void write(java.io.File file, String format)
            throws IOException {
        javax.imageio.ImageIO.write(this, format, file);
    }
}
