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

package eu.digitisation.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        URL resourceUrl = getClass().getResource("/UnicodeCharEquivalences.txt");
        File file = Paths.get(resourceUrl.toURI()).toFile();
        CharFilter filter = new CharFilter(file);
        String s = "a\u0133";  // ij
        String expResult = "aij";
        String result = filter.translate(s);
        assertEquals(expResult.length(), result.length());
        assertEquals(expResult, result);
    }
    
    /**
     * Test of translate method, of class CharFilter.
     * This test creates new files with substituted listed on UnicodeCharEquivalences characters in the specified files
     * @throws java.net.URISyntaxException
     * @throws IOException 
     */
    @Test
    public void testTranslateTexts() throws URISyntaxException, IOException {
    	char maxChar = 0xa759;
    	char maxUnicodeChar = 0x7F;
    	List<String> paths =  new ArrayList<String>();
    	paths.add("src/test/resources/testToReplaceChars/example/");
    	
    	URL resourceUrl = getClass().getResource("/UnicodeCharEquivalences.txt");
        File fileEquivalences = Paths.get(resourceUrl.toURI()).toFile();
        CharFilter filter = new CharFilter(fileEquivalences);
        
    	HashMap<String, String[]> codes = new HashMap<String, String[]>();
    	HashMap<String, String[]> nonUnicodes = new HashMap<String, String[]>();
        FilenameFilter filterName = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".txt");
            }
        };

		System.out.println("Initial Filter Size: " + filter.size());
        System.out.println("Character, HexCode, UniCode");
        for ( String path:paths )
        {
	    	File folder = new File(path);
	        
			File[] listOfFiles = folder.listFiles(filterName);
	        
	        for (int i = 0; i < listOfFiles.length; i++) 
	        {
	         if (listOfFiles[i].isFile()) 
	         {
		         File newFile = new File(listOfFiles[i].getPath() + ".out");
		         
		         CharSequence caracters = filter.toCharSequence(newFile);
		         
		         if (caracters != null)
		         for ( int i1 = 0; i1< caracters.length(); i1++ )
		         {
		        	 if ( caracters.charAt(i1) > maxChar)
		        	 {
		        		 String hexCode = String.valueOf(java.util.Arrays.toString(UnicodeReader.toHexCodepoints(String.valueOf(caracters.charAt(i1)))));
		        		 String uniCode = java.util.Arrays.toString(UnicodeReader.toCodepoints(String.valueOf(caracters.charAt(i1))));
		        		 String caracter = String.valueOf(caracters.charAt(i1));
		        		 String equivalence[] = {hexCode, uniCode};
		        		 
		        		 //System.out.print(  "(" + caracters.charAt(i1) + ", " + hexCode + " )" );	        		 
		        		 codes.put(caracter, equivalence);
		        	 }
		        	 else
		        	 {
			        	 if ( caracters.charAt(i1) > maxUnicodeChar)
			        	 {
			        		 String hexCode = String.valueOf(java.util.Arrays.toString(UnicodeReader.toHexCodepoints(String.valueOf(caracters.charAt(i1)))));
			        		 String uniCode = java.util.Arrays.toString(UnicodeReader.toCodepoints(String.valueOf(caracters.charAt(i1))));
			        		 String caracter = String.valueOf(caracters.charAt(i1));
			        		 String equivalence[] = {hexCode, uniCode};
			        		 
			        		 nonUnicodes.put(caracter, equivalence);
			        	 }
		        	 }
		         }
		         filter.translate(listOfFiles[i], newFile);
	         }
	        }
        }
        File resultFile = new File("src/test/resources/testToReplaceChars/nonPrintableCharacters.txt");
        FileWriter w = new FileWriter(resultFile);
        BufferedWriter bw = new BufferedWriter(w);
        PrintWriter wr = new PrintWriter(bw);  

        wr.append("Characters > " + maxChar + " \n");
        for (Map.Entry entry : codes.entrySet()) 
        {
        		System.out.print(entry.getKey() + ", ");
        		System.out.println( Arrays.deepToString((Object[]) entry.getValue()) );
        		wr.append(entry.getKey() + ", ");
        		wr.append(Arrays.deepToString((Object[]) entry.getValue()) + "\n");
        }
        wr.append("\nNon unicode characters\n");
        for (Map.Entry entryNonUnicodes : nonUnicodes.entrySet()) 
        {
        		System.out.print(entryNonUnicodes.getKey() + ", ");
        		System.out.println( Arrays.deepToString((Object[]) entryNonUnicodes.getValue()) );
        		wr.append(entryNonUnicodes.getKey() + ", ");
        		wr.append(Arrays.deepToString((Object[]) entryNonUnicodes.getValue()) + "\n");
        }
        
        wr.close();
        bw.close();        
    } 
}
