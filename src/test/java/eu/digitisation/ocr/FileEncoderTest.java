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
package eu.digitisation.ocr;

import java.io.File;
import static junit.framework.TestCase.assertEquals;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author rafa
 */
public class FileEncoderTest {

    public FileEncoderTest() {
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
     * Test of getCode method, of class FileEncoder.
     */
    @Test
    public void testGetCode() {
        System.out.println("getCode");
        String word = "uno";
        FileEncoder instance = new FileEncoder();
        Integer expResult = 0;
        Integer result = instance.getCode(word);
        assertEquals(expResult, result);
    }

    /**
     * Test of encode method, of class FileEncoder.
     */
    /*
     @Test
     public void testEncode_File_String() {
     System.out.println("encode");
     File file = null;
     String encoding = "hola migo hola";
     FileEncoder instance = new FileEncoder();
     Integer[] expResult = {1, 2, 1};
     Integer[] result = instance.encode(file, encoding);
     assertArrayEquals(expResult, result);
     }
     */
    /**
     * Test of encode method, of class FileEncoder.
     */
    @Test
    public void testEncode_String() {
        System.out.println("encode");
        String s = "one two one";
        FileEncoder instance = new FileEncoder();
        Integer[] expResult = {0, 1, 0};
        Integer[] result = instance.encode(s);
        assertArrayEquals(expResult, result);
        // Another test
        String input = "hola&amigo2\n3.14 mi casa, todos los días\n"
                + "mesa-camilla java4you i.b.m. i+d Dª María 3+100%";
        FileEncoder encoder = new FileEncoder();
        int size = encoder.encode(input).length;

        assertEquals(18, size);
    }

}
