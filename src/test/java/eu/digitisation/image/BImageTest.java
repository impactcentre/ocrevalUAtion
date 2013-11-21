/*
 * Copyright (C) 2013 IMPACT Centre of Competence
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

package eu.digitisation.image;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * 
 * @author emolla
 */
public class BImageTest {

	public BImageTest() {
	}

	@BeforeClass
	public static void setUpClass() {
	}

	@AfterClass
	public static void tearDownClass() {
	}

	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

	/**
	 * Test support TIF Image
	 * 
	 * @throws IOException
	 */
	@Test
	public void openTiffFile() throws IOException {
		File ifile = new File("src/test/resources/00445310.tif");
		Bimage page = null;

		if (ifile.exists()) {
			try {
				page = new Bimage(ifile).toRGB();
			} catch (NullPointerException ex) {
				throw new IOException("Unsupported format");
			}
		} else {
			throw new java.io.IOException(ifile + " not found");
		}

		BufferedImage image = ImageIO.read(ifile);
		assertNotNull(image);
	}

	/**
	 * Test support TIF Image
	 * 
	 * @throws IOException
	 */
	@Test
	public void getTiffDecoder() throws IOException {

		Iterator<ImageReader> reader = ImageIO
				.getImageReadersByFormatName("TIFF");
		assertNotNull(reader);
		assertTrue("No tiff decoder", reader.hasNext());
	}

}
