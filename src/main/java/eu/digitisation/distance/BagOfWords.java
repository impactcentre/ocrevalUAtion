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
import eu.digitisation.math.Counter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A bag of words is a text where the ordering of words is irrelevant.
 *
 * @author R.C.C.
 * @deprecated
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
     * Compute the distance between two bag of words (order independent
     * distance)
     *
     * @param other another bag of words
     * @return the number of differences between this and the other bag of words
     */
    public int distance(BagOfWords other) {
        int dplus = 0;    // excess
        int dminus = 0;   // fault
        for (String word : this.words.keySet()) {
            int delta = this.words.value(word) - other.words.value(word);
            if (delta > 0) {
                dplus += delta;
            } else {
                dminus += delta;
            }
        }
        for (String word : other.words.keySet()) {
            if (!this.words.containsKey(word)) {
                int delta = this.words.value(word) - other.words.value(word);
                if (delta > 0) {
                    dplus += delta;
                } else {
                    dminus += delta;
                }
            }
        }
        return Math.max(dplus, dminus);
    }

    /**
     * The total number of words
     *
     * @return the total number of words
     */
    public int total() {
        return words.total();
    }
}
