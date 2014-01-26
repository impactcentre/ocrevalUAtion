/*
 * Copyright (C) 2014 Uni. de Alicante
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
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author rafa
 */
public class EditDistanceTest {

    public EditDistanceTest() {
    }

    /**
     * Test of charDistance method, of class EditDistance.
     */
    @Test
    public void testCharDistance() {
        System.out.println("charDistance");

        String s1 = "patata";
        String s2 = "apta";
        int expResult = 3;
        int result = EditDistance.charDistance(s1, s2, 10);
        assertEquals(expResult, result);
        // A second test
        s1 = "holanda";
        s2 = "wordland";
        result = EditDistance.charDistance(s1, s2, 10);
        assertEquals(4, result);
        // Test with normalization
        s1 = StringNormalizer.reduceWS("Mi enhorabuena");
        s2 = StringNormalizer.reduceWS("mi en  hora  buena");
        result = EditDistance.charDistance(s1, s2, 10);
        assertEquals(3, result);
    }

    /**
     * Test of wordDistance method, of class EditDistance.
     */
    @Test
    public void testWordDistance() {
        System.out.println("wordDistance");
        String s1 = "p a t a t a";
        String s2 = "a p t a";
        int expResult = 3;
        int result = EditDistance.wordDistance(s1, s2, 10);
        assertEquals(expResult, result);

    }

}
