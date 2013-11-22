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
package eu.digitisation.distance;

import eu.digitisation.io.StringNormalizer;
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
public class StringEditDistanceTest {

    public StringEditDistanceTest() {
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
     * Test of indel method, of class StringEditDistance.
     */
    @Test
    public void testIndelDistance() {
        System.out.println("indelDistance");
        String first = "patata";
        String second = "apta";
        int expResult = 4;
        int result = StringEditDistance.indel(first, second);
        assertEquals(expResult, result);

    }

    /**
     * Test of levenshtein method, of class StringEditDistance.
     */
    @Test
    public void testLevenshteinDistance() {
        System.out.println("levenshteinDistance");
        String first = "patata";
        String second = "apta";
        int expResult = 3;
        int result = StringEditDistance.levenshtein(first, second);
        assertEquals(expResult, result);
        // A second test
        first = "holanda";
        second = "wordland";
        result = StringEditDistance.levenshtein(first, second);
        assertEquals(4, result);
        // Test with normalization
        first = StringNormalizer.reduceWS("Mi enhorabuena");
        second = StringNormalizer.reduceWS("mi en  hora  buena");
        result = StringEditDistance.levenshtein(first, second);
        assertEquals(3, result);
    }

    /**
     * Test of align method, of class StringEditDistance.
     */
    @Test
    public void testAlign() {
        System.out.println("align");
        String first = "patata";
        String second = "apta";
        int[] expResult = {-1, 0, -1, -1, 2, 3};
        int[] result = StringEditDistance.align(first, second);
        int s = 0;
        for (int n = 0; n < first.length(); ++n) {
            if (result[n] >= 0) {
                assertEquals(first.charAt(n), second.charAt(result[n]));
                ++s;
                System.out.println(n + " " + s);
            } else {
                System.out.println(result[n]);
            }
        }
        assertEquals(first.length() + second.length(),
                2 * s + StringEditDistance.indel(first, second));

    }

    @Test
    public void testOperations() {
        System.out.println("operations");
        String first = "patata";
        String second = "apta";
        int[] expResult = {0, 1, 2};
        int[] result = StringEditDistance.operations(first, second);
        assertArrayEquals(expResult, result);
    }
}
