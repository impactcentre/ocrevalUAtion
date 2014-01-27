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
public class EditSequenceTest {

    /**
     * Test of cost method, of class EditSequence.
     */
    @Test
    public void testCost() {
        System.out.println("cost");
        EditSequence instance = new EditSequence("acb", "a b", new OcrOpWeight());
        int expResult = 2;
        int result = instance.cost();
        assertEquals(expResult, result);
    }

    /**
     * Test of shift1 method, of class EditSequence.
     */
    //@Test
    public void testShift1() {
        System.out.println("shift1");
        EditSequence instance = new EditSequence("acb", "a b", new OcrOpWeight());
        int expResult = 1;
        int result = instance.shift1();
        assertEquals(expResult, result);

    }

}
