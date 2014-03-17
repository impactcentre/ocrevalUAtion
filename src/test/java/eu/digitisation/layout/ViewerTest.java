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
package eu.digitisation.layout;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author R.C.C
 */
public class ViewerTest {

    /**
     * Test of main method, of class WordScanner.
     *
     * @throws java.io.IOException
     * @throws java.net.URISyntaxException
     */
    @Test
    public void createTiffPage() throws IOException, URISyntaxException {
        URL resourceUrl1 = getClass().getResource("/00439040.tif");
        File file1 = new File(resourceUrl1.toURI());
        URL resourceUrl2 = getClass().getResource("/00439040_gt_PAGE.xml");
        File file2 = new File(resourceUrl2.toURI());
        String[] args = {file1.getCanonicalPath(), file2.getCanonicalPath()};

        //Viewer.main(args);

        URL resourceUrl3 = getClass().getResource("/00439040_marked.tif");
        File file3 = new File(resourceUrl3.toURI());
        long size = file3.length();

        assertTrue(size > 0);
    }

}
