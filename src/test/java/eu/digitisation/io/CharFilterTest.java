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

package eu.digitisation.io;

import java.io.File;
import java.net.URISyntaxException;
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
public class CharFilterTest {
    
    public CharFilterTest() {
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
     * Test of translate method, of class CharFilter.
     * @throws java.net.URISyntaxException
     */
    @Test
    public void testTranslate_String() throws URISyntaxException {
        System.out.println("translate");
        URL resourceUrl = getClass().getResource("/replacements.txt");
        File file = Paths.get(resourceUrl.toURI()).toFile();
        CharFilter filter = new CharFilter(file);
        String s = "a\u2028";
        String expResult = "a ";
        String result = filter.translate(s);
        assertEquals(expResult.length(), result.length());
        assertEquals(expResult, result);
    }
    
}
