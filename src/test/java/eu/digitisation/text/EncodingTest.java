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
package eu.digitisation.text;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author R.C.C
 */
public class EncodingTest {



    /**
     * Test of detect method, of class Encoding.
     * @throws java.net.URISyntaxException
     */
    @Test
    public void testDetect() throws URISyntaxException {
        System.out.println("detect");
        URL resourceUrl = getClass().getResource("/OfTheSciences_gt_TXT.txt");
        File file = new File(resourceUrl.toURI());
        Charset expResult = Charset.forName("UTF-8");
        Charset result = Encoding.detect(file);
        assertEquals(expResult, result);
    }

}
