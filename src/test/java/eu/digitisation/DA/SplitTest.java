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
package eu.digitisation.DA;

import java.io.File;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import org.w3c.dom.Document;

/**
 *
 * @author carrasco@ua.es
 */
public class SplitTest {

    public SplitTest() {
    }

    @Test
    public void testHeader() throws Exception {
        System.out.println("header");
        String text = "APOSTAL (Apoál.)";
        String expResult = "APOSTAL";
        String result = Split.header(text);
        assertEquals(expResult, result);
//        fail("The test case is a prototype.");
    }

    @Test
    public void testisParticiple() throws Exception {
        System.out.println("isParticiple");
        String lemma = "COMER";
        String pp = "COMIDO";
        assert (Split.isParticiple(pp, lemma));
    }

    @Test
    public void testSplit() throws Exception {
        System.out.println("split");
        File ifile = null;
        //Split.split(ifile);
        //fail("The test case is a prototype.");
    }
}