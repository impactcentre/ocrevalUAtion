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
package eu.digitisation.ngram;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author carrasco@ua.es
 */
public class NgramModelTest {

    /**
     * Test of size method, of class NgramModel.
     */
    @Test
    public void testSize() {
        System.out.println("size");
        NgramModel instance = new NgramModel(2);
        instance.addWord("lava");
        // "" l a v $ #l la av va a$ -> 10
        int expResult = 10;
        int result = instance.size();
        assertEquals(expResult, result);
    }

    /**
     * Test of logH method, of class NgramModel.
     */
    @Test
    public void testlogH() {
        System.out.println("logH");
        NgramModel instance = new NgramModel(2);
        instance.addWord("lava");
        // "" l a v $ #l la av va a$ -> 10 
        double expResult = 2.0;
        double result = instance.entropy();
        assertEquals(expResult, result, 0.01);
    }

    @Test
    public void testWordLogProb() {
        System.out.println("wordLogProb");
        NgramModel instance = new NgramModel(1);
        instance.addWord("lava");
        double expResult = (3 * Math.log(0.2) + 2 * Math.log(0.4));
        double result = instance.wordLogProb("lava");
        assertEquals(expResult, result, 0.01);
    }
}