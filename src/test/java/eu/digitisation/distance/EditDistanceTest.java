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
import eu.digitisation.io.Text;
import eu.digitisation.io.WarningException;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
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
    public void testCharDistance() throws URISyntaxException, WarningException {
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

        s1 = "bluevelvet";
        s2 = "luevert";
        expResult = StringEditDistance.levenshtein(s1, s2);
        result = EditDistance.charDistance(s1, s2, 5);
        assertEquals(expResult, result);

        s1 = "patatapatata";
        s2 = "gastapasta";
        expResult = 5;//StringEditDistance.levenshtein(s1, s2);  
        result = EditDistance.charDistance(s1, s2, 50);
        assertEquals(expResult, result);

        URL gtUrl = getClass().getResource("/OfTheSciences_gt_TXT.txt");
        File gtfile = new File(gtUrl.toURI());
        String gts = new Text(gtfile).toString();
        URL ocrUrl = getClass().getResource("/OfTheSciences_ocr_PAGE.xml");
        File ocrfile = new File(ocrUrl.toURI());
        String ocrs = new Text(ocrfile).toString();
        expResult = StringEditDistance.levenshtein(gts, ocrs);
        result = EditDistance.charDistance(gts, ocrs, 1000);
        assertEquals(expResult, result);
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
