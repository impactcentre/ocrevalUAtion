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

import eu.digitisation.text.WordScanner;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * Encode a text file as an array of Integers (one code per word). Every word is
 * encoded as an integer. Identical words have identical encodings and different
 * words have different codes. Consistency (between encodings and files) is only
 * guaranteed if the same TextFileEncoder is used.
 *
 * @version 2012.06.20
 * @deprecated Use TokenArray instead
 */
public class TextFileEncoder {

    HashMap<String, Integer> codes;
    boolean caseSensitive;   // Case sensitive encoding

    /**
     *
     * @param sensitive True if the encoder preserves case, False if the encoder
     * folds uppercase into lowercase.
     */
    public TextFileEncoder(boolean sensitive) {
        codes = new HashMap<String, Integer>();
        caseSensitive = sensitive;
    }

    /**
     *
     * @param word a word
     * @return The integer code assigned to this word
     */
    public Integer getCode(String word) {
        Integer code;
        String key = caseSensitive ? word : word.toLowerCase(Locale.ROOT);

        if (codes.containsKey(key)) {
            code = codes.get(key);
        } else {
            code = codes.size();
            codes.put(key, code);
        }
        return code;
    }

    /**
     * Encode a file
     *
     * @param file the input file
     * @param encoding the text encoding 
     * @return the file as an array of integer codes (one per word)
     */
    public Integer[] encode(File file, String encoding)
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
            Logger.getLogger(TextFileEncoder.class.getName()).log(Level.SEVERE, null, ex);
        }
        return array.toArray(new Integer[array.size()]);
    }

    /**
     * Encode a string
     *
     * @param s the input string
     * @return the string as an array of integer codes (one per word)
     */
    public Integer[] encode(String s) {
        ArrayList<Integer> array = new ArrayList<Integer>();
        WordScanner scanner;
        String word;

        try {
            scanner = new WordScanner(s);
            while ((word = scanner.nextWord()) != null) {
                array.add(getCode(word));
            }
        } catch (IOException ex) {
            Logger.getLogger(TextFileEncoder.class.getName()).log(Level.SEVERE, null, ex);
        }

        return array.toArray(new Integer[array.size()]);
    }
}
