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
package eu.digitisation.distance;

import eu.digitisation.io.WordScanner;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A TokenArray is a tokenized string: every word is internally stored as an
 * integer. The mapping between words and integer codes is specific for every
 * TokenArray.
 *
 * @version 2013.12.10
 */
public class TokenArray {

    HashMap<String, Integer> codes;
    Integer[] tokens;
    boolean caseSensitive;   // Case sensitive encoding

    /**
     *
     * @param sensitive True if the encoder preserves case, False if the encoder
     * folds uppercase into lowercase.
     */
    public TokenArray(boolean sensitive) {
        codes = new HashMap<String, Integer>();
        caseSensitive = sensitive;
    }

    /**
     *
     * @param word a word
     * @return The integer code assigned to this word
     */
    private Integer getCode(String word) {
        Integer code;
        String key = caseSensitive ? word : word.toLowerCase();

        if (codes.containsKey(key)) {
            code = codes.get(key);
        } else {
            code = codes.size();
            codes.put(key, code);
        }
        return code;
    }

    /**
     * Build a TokenArray form the file content
     *
     * @param file the input file
     * @param encoding the text encoding
     */
    public TokenArray(File file, String encoding)
            throws RuntimeException {

        ArrayList<Integer> array = new ArrayList<Integer>();
        WordScanner scanner;
        String word;

        try {
            scanner = new WordScanner(file, encoding);
            while ((word = scanner.nextWord()) != null) {
                array.add(getCode(word));
            }
        } catch (IOException ex) {
            Logger.getLogger(TokenArray.class.getName()).log(Level.SEVERE, null, ex);
        }
        tokens = array.toArray(new Integer[array.size()]);
    }

    /**
     * Build a TokenArray from a String
     *
     * @param s the input string
     */
    public TokenArray(String s) {
        ArrayList<Integer> array = new ArrayList<Integer>();

        try {
            WordScanner scanner = new WordScanner(s);
            String word;

            while ((word = scanner.nextWord()) != null) {
                array.add(getCode(word));
            }
        } catch (IOException ex) {
            Logger.getLogger(TokenArray.class.getName()).log(Level.SEVERE, null, ex);
        }

        tokens = array.toArray(new Integer[array.size()]);
    }
    
    /**
     * The length of the token array
     * @return the length of the token array
     */
    public int getLength() {
        return tokens.length;
    }
    
    /**
     * 
     * @return the internal representation as an array of integer codes
     */
    public Integer[] getTokens() {
        return tokens;
    }
}
