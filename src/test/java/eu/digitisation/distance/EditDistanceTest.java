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
     * Test of levenshtein method, of class AproxStringEditDistance.
     */
    @Test
    public void testLevenshtein() {
        System.out.println("levenshtein");
        String s1 = "bluevelvet";
        String s2 = "luevert";
        int expResult = StringEditDistance.levenshtein(s1, s2);
        int result = EditDistance.charDistance(s1, s2, 5);
        assertEquals(expResult, result);
    }

 
    
}
