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

import eu.digitisation.deprecated.TextBuilder;
import eu.digitisation.io.StringNormalizer;
import eu.digitisation.io.TextContent;
import eu.digitisation.math.Counter;
import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
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
public class ErrorMeasureTest {

    public ErrorMeasureTest() {
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
     * Test of trim method, of class ErrorMeasure.
     *
     * @throws java.io.IOException
     */
    @Test
    public void testMerge() throws Exception {
        System.out.println("merge");
        URL resourceUrl = getClass().getResource("/text2.txt");
        File file = Paths.get(resourceUrl.toURI()).toFile();
        String encoding = "utf8";
        String expResult = "mi en hora buena";
        String result = TextBuilder.trimmed(file, encoding).toString();

        //System.out.println(result.replaceAll(" ", "*"));
        assertEquals(expResult, result);
    }

    /**
     * Test of cer method, of class ErrorMeasure.
     */
    @Test
    public void testCer() throws Exception {
        System.out.println("cer");
        URL resourceUrl1 = getClass().getResource("/text1.txt");
        File file1 = Paths.get(resourceUrl1.toURI()).toFile();
        String encoding1 = "utf8";
        URL resourceUrl2 = getClass().getResource("/text2.txt");
        File file2 = Paths.get(resourceUrl2.toURI()).toFile();
        String encoding2 = "utf8";
        TextContent c1 = new TextContent(file1, encoding1, null);
        TextContent c2 = new TextContent(file2, encoding2, null);
        String s1 = StringNormalizer.reduceWS(c1.toString());
        String s2 = StringNormalizer.reduceWS(c2.toString());
        double expResult = 3.0/ 14;
        double result = ErrorMeasure.cer(s1, s2);
        assertEquals(expResult, result, 0.001);
    }

    /**
     * Test of wer method, of class ErrorMeasure.
     * @throws java.lang.Exception
     */
    @Test
    public void testWer() throws Exception {
        System.out.println("wer");
        System.out.println("cer");
        URL resourceUrl1 = getClass().getResource("/text1.txt");
        File file1 = Paths.get(resourceUrl1.toURI()).toFile();
        String encoding1 = "utf8";
        URL resourceUrl2 = getClass().getResource("/text2.txt");
        File file2 = Paths.get(resourceUrl2.toURI()).toFile();
        String encoding2 = "utf8";
        TextContent c1 = new TextContent(file1, encoding1, null);
        TextContent c2 = new TextContent(file2, encoding2, null);
        double expResult = 0.5;
        double result = ErrorMeasure.wer(c1.toString(), c2.toString());
        assertEquals(expResult, result, 0.001);
    }
    
    @Test
    public void testErrors() {
        String s1 = "alabama";
        String s2 = "ladamass";
        Counter<Character>[] stats = ErrorMeasure.errors(s1, s2);
        int expResult = 4;
        int result = stats[0].value('a');
//         assertEquals(expResult, result);
//         assertEquals(stats[1].value('d'), 1);
    }
    
}
