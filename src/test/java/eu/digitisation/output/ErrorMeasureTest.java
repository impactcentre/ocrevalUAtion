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
package eu.digitisation.output;

import eu.digitisation.output.ErrorMeasure;
import eu.digitisation.distance.EdOp;
import eu.digitisation.distance.StringEditDistance;
import eu.digitisation.text.StringNormalizer;
import eu.digitisation.text.TextContent;
import eu.digitisation.math.BiCounter;
import java.io.File;
import java.net.URL;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author R.C.C
 */
public class ErrorMeasureTest {

    /**
     * Test of trim method, of class ErrorMeasure.
     *
     * @throws java.io.IOException
     */
    @Test
    public void testMerge() throws Exception {
        System.out.println("merge");
        URL resourceUrl = getClass().getResource("/text2.txt");
        File file = new File(resourceUrl.toURI());
        String encoding = "utf8";
        String expResult = "mi en hora buena";
        String result = new TextContent(file, null, encoding).toString();
        assertEquals(expResult, result);
    }

    /**
     * Test of cer method, of class ErrorMeasure.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testCer() throws Exception {
        System.out.println("cer");
        URL resourceUrl1 = getClass().getResource("/text1.txt");
        File file1 = new File(resourceUrl1.toURI());
        //String encoding1 = "utf8";
        URL resourceUrl2 = getClass().getResource("/text2.txt");
        File file2 = new File(resourceUrl2.toURI());
        //String encoding2 = "utf8";
        TextContent c1 = new TextContent(file1, null, null);
        TextContent c2 = new TextContent(file2, null, null);
        String s1 = StringNormalizer.reduceWS(c1.toString());
        String s2 = StringNormalizer.reduceWS(c2.toString());
        double expResult = 3.0 / 14;
        double result = ErrorMeasure.cer(s1, s2);
        assertEquals(expResult, result, 0.001);
    }

    /**
     * Test of wer method, of class ErrorMeasure.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testWer() throws Exception {
        System.out.println("wer");
        System.out.println("cer");
        URL resourceUrl1 = getClass().getResource("/text1.txt");
        File file1 = new File(resourceUrl1.toURI());
        //String encoding1 = "utf8";
        URL resourceUrl2 = getClass().getResource("/text2.txt");
        File file2 = new File(resourceUrl2.toURI());
        //String encoding2 = "utf8";
        TextContent c1 = new TextContent(file1, null);
        TextContent c2 = new TextContent(file2, null);
        double expResult = 1.5;
        double result = ErrorMeasure.wer(c1.toString(), c2.toString());
        assertEquals(expResult, result, 0.001);

        String s1 = "one two three one two one";
        String s2 = "one two three four";
        expResult = 0.5;  // 1 replaced + 2 deleted out of 6 words
        result = ErrorMeasure.wer(s1, s2);
        assertEquals(expResult, result, 0.01);
    }

      /**
     * Test of distance method, of class BagOfWords.
     */
    @Test
    public void testBwer() {
        System.out.println("wer");
        String s1 = "one two three one two one";
        String s2 = "one four two three";
        double expResult = 0.5; // 1 replaced + 2 deleted out of 6 words
        double result = ErrorMeasure.ber(s1, s2);
        assertEquals(expResult, result, 0.01);
    }
    
    @Test
    public void testStats() {
        String s1 = "alabama";
        String s2 = "ladamass";
        BiCounter<Character, EdOp> stats = StringEditDistance.operations(s1, s2);
        int expResult = 4;
        int result = stats.value('a', null);

        assertEquals(expResult, result);
        assertEquals(1, stats.value('b', EdOp.SUBSTITUTE));
    } 
    
}
