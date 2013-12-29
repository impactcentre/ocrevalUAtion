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

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A TokenArray is a tokenized string: every word is internally stored as an
 * integer. The mapping between words and integer codes is shared by all
 * TokenArrays to allow for comparison.
 *
 * @version 2013.12.10
 */
public class TokenArray {

    HashMap<String, Integer> codes;
    Integer[] tokens;

    /**
     * Default constructor
     * @param codes the dictionary of codes
     * @param tokens the integer representation
     */
    TokenArray(HashMap<String, Integer> codes, Integer[] tokens) {
        this.codes = codes;
        this.tokens = tokens;
    }
    
    /**
     * Default constructor
     * @param codes the dictionary of codes
     * @param tokens the integer representation
     */
    TokenArray(HashMap<String, Integer> codes, ArrayList<Integer> tokens) {
        this.codes = codes;
        this.tokens = tokens.toArray(new Integer[tokens.size()]);
    }
    
    /**
     * The length of the token array
     *
     * @return the length of the token array
     */
    public int length() {
        return tokens.length;
    }

    /**
     *
     * @return the internal representation as an array of integer codes
     */
    public Integer[] tokens() {
        return tokens;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        String[] dictionary = new String[codes.size()];
       
        for (String word : codes.keySet()) {
            dictionary[codes.get(word)] = word;
        }
        for (Integer token : tokens) {
            builder.append(dictionary[token]).append(" ");
        }

        return builder.toString().trim();
    }

    /**
     * Distance between TokenArrays
     *
     * @param other another TokenArray
     * @param type the distance type
     * @return the distance between this and the other TokenArray
     */
    public int distance(TokenArray other, EditDistanceType type) {
        return ArrayEditDistance.distance(this.tokens, other.tokens, type);
    }

    /**
     * Return the TokenArray as array
     * @return the array of tokens
     */
    public String array() {
        return java.util.Arrays.toString(tokens);
    }
}
