/*
 * Copyright (C) 2014 IMPACT Centre of Competence
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
public class OcrOpWeightTest {

    /**
     * Test of sub method, of class OcrWeights.
     */
    @Test
    public void testSub() {
        System.out.println("sub");
        char[] c1 = {'Á', 'Á', 'Á', 'Á', 'Á'};
        char[] c2 = {'Á', 'A', 'á', 'a', ' '};
        int[] w1 = {0, 1, 1, 1, 4};
        OcrOpWeight W1 = new OcrOpWeight(); // fully-sensitive
        for (int n = 0; n < w1.length; ++n) {
            assertEquals(w1[n], W1.sub(c1[n], c2[n]));
        }
        OcrOpWeight W2 = new OcrOpWeight(true, true, true); //ignore everything
        int[] w2 = {0, 0, 0, 0, 4};
        for (int n = 0; n < w2.length; ++n) {
            assertEquals(w2[n], W2.sub(c1[n], c2[n]));
        }
        OcrOpWeight W3 = new OcrOpWeight(false, true, true); //ignore diacritics
        int[] w3 = {0, 0, 1, 1, 4};
        for (int n = 0; n < w3.length; ++n) {
            assertEquals(w3[n], W3.sub(c1[n], c2[n]));
        }
        
        OcrOpWeight W4 = new OcrOpWeight(true, false, true); //ignore case
        int[] w4 = {0, 1, 0, 1, 4};
        for (int n = 0; n < w4.length; ++n) {
            assertEquals(w4[n], W4.sub(c1[n], c2[n]));
        }
    }

    /**
     * Test of ins method, of class OcrWeights.
     */
    @Test
    public void testIns() {
        System.out.println("ins");
        OcrOpWeight W = new OcrOpWeight(false, false, true); //ignore punct
        assertEquals(1, W.ins('a'));
        assertEquals(0, W.ins('@'));
        assertEquals(0, W.ins('+'));
        W = new OcrOpWeight();
        assertEquals(1, W.ins('a'));
        assertEquals(1, W.ins('@'));
        assertEquals(1, W.ins('+'));

    }

}
