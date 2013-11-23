/*
 * Copyright (C) 2013 IMPACT Universidad de Alicante
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
import eu.digitisation.math.Counter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author R.C.C.
 */
public class BagOfWords {

    Counter<String> words;

    /**
     * Create a BagOfWords for String
     *
     * @param s
     */
    public BagOfWords(String s) {
        words = new Counter<String>();
        try {
            WordScanner scanner = new WordScanner(s);
            String word;

            while ((word = scanner.nextWord()) != null) {
                words.inc(word);
            }
        } catch (IOException ex) {
            Logger.getLogger(BagOfWords.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Compute the distance between two bag of words
     * @param other another bag of words
     * @return the number of differences between this and the other bag of words
     */
    public int distance(BagOfWords other) {
        int d = 0;
        for (String word : this.words.keySet()) {
            int delta = this.words.value(word) - other.words.value(word);
            d += Math.abs(delta);
        }
        for (String word : other.words.keySet()) {
            if (!this.words.containsKey(word)) {
                int delta = this.words.value(word) - other.words.value(word);
                d += Math.abs(delta);
            }
        }
        return d;
    }
    
    /**
     * The total number of words
     * @return the total number of words
     */
    public int total() {
        return words.total();
    }
    
    /**
     * 
     * @param s1 reference string
     * @param s2  fuzzy string string
     * @return the word error rate between the (unsorted) strings 
     */
    public static double wer (String s1, String s2) {
        BagOfWords bow1 = new BagOfWords(s1);
        BagOfWords bow2 = new BagOfWords(s2);
        int tot1 = bow1.total();
        int tot2 = bow2.total();
        int d = bow1.distance(bow2); 
        return  (Math.abs(tot1 - tot2) + d) / (double)(2 * bow1.total());
    }
}
