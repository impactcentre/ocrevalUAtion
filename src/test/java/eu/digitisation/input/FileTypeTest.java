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
package eu.digitisation.input;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author rafa
 */
public class FileTypeTest {

    public FileTypeTest() {
    }

    /**
     * Test of valueOf method, of class FileType.
     *
     * @throws eu.digitisation.input.SchemaLocationException
     */
    @Test
    public void testValueOf()
            throws SchemaLocationException, IOException {
        System.out.println("valueOf");
        URL url = getClass().getResource("/OfTheSciences_ocr_PAGE.xml");
        File file = new File(url.getPath());
        FileType expResult = FileType.PAGE;
        FileType result = FileType.valueOf(file);
        assertEquals(expResult, result);
    }

}
