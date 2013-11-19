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
package eu.digitisation.ocr;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * Encode a file as an array of Integers. Every word is encoded as an integer.
 * Identical words have identical encodings and different words have different
 * codes. Consistency (between encodings and files) is only guaranteed if the
 * same FileEncoder is used.
 *
 * @version 2012.06.20
 */
public class FileEncoder {

    HashMap<String, Integer> codes;

    public FileEncoder() {
        codes = new HashMap<String, Integer>();
    }

    /**
     *
     * @param word a word
     * @return The integer code assigned to this word
     */
    public Integer getCode(String word) {
        Integer code;
        if (codes.containsKey(word)) {
            code = codes.get(word);
        } else {
            code = codes.size();
            codes.put(word, code);
        }
        return code;
    }

    /**
     * Encode a file
     *
     * @param file the input file
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
                if (array.size() > 10000) {
                    throw new RuntimeException("Online tests limited to 10000 words");
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(FileEncoder.class.getName()).log(Level.SEVERE, null, ex);
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
                //System.out.println("Encoding:'" + word + "'");
                array.add(getCode(word));
            }
        } catch (IOException ex) {
            Logger.getLogger(FileEncoder.class.getName()).log(Level.SEVERE, null, ex);
        }

        return array.toArray(new Integer[array.size()]);
    }
}
