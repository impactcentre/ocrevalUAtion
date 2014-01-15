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
package eu.digitisation.ocrevaluation;

import eu.digitisation.distance.ArrayEditDistance;
import eu.digitisation.distance.BagOfWords;
import eu.digitisation.distance.EditDistanceType;
import eu.digitisation.distance.StringEditDistance;
import eu.digitisation.distance.TokenArray;
import eu.digitisation.distance.TokenArrayFactory;

/**
 * Computes character and word error rates by comparing two texts
 *
 * @version 2012.06.20
 */
public class ErrorMeasure {

    /**
     * Compute character error rate using Levenshtein distance
     *
     * @param s1 the reference text
     * @param s2 fuzzy text
     * @return character error rate with respect to the reference file
     */
    public static double cer(String s1, String s2) {
        int l1 = s1.length();
        int l2 = s2.length();
        double delta = (100.00 * Math.abs(l1 - l2)) / (l1 + l2);

        if (delta > 20) {
            System.err.println("Warning: files differ a "
                    + String.format("%.2f", delta) + " % in character length");
        }

        return StringEditDistance.distance(s1, s2, EditDistanceType.LEVENSHTEIN)
                / (double) l1;
    }

    /**
     * Compute character error rate using Damerau-Levenshtein distance
     *
     * @param s1 the reference text
     * @param s2 fuzzy text
     * @return character error rate with respect to the reference file
     */
    public static double cerDL(String s1, String s2) {
        int l1 = s1.length();
        int l2 = s2.length();
        double delta = (100.00 * Math.abs(l1 - l2)) / (l1 + l2);

        if (delta > 20) {
            System.err.println("Warning: files differ a "
                    + String.format("%.2f", delta) + " % in character length");
        }

        return StringEditDistance.distance(s1, s2, EditDistanceType.DAMERAU_LEVENSHTEIN)
                / (double) l1;
    }

    /**
     * Compute word error rate
     *
     * @param a1 array of integers
     * @param a2 array of integers
     * @return error rate
     */
    private static double wer(TokenArray a1, TokenArray a2) {
        int l1 = a1.length();
        int l2 = a2.length();
        double delta = (100.00 * Math.abs(l1 - l2)) / (l1 + l2);

        if (delta > 20) {
            System.err.println("Warning: files differ a "
                    + String.format("%.2f", delta) + " % in word length");
        }

        return ArrayEditDistance.distance(a1.tokens(), a2.tokens(),
                EditDistanceType.LEVENSHTEIN) / (double) l1;
    }

    /**
     * Compute word recall rate
     *
     * @param a1 first TokenArray
     * @param a2 second TokenArray
     * @return word recall (fraction of words in a1 also in a2)
     */
    public static double wordRecall(TokenArray a1, TokenArray a2) {
        int l1 = a1.length();
        int l2 = a2.length();
        double delta = (100.00 * Math.abs(l1 - l2)) / (l1 + l2);

        if (delta > 20) {
            System.err.println("Warning: files differ a "
                    + String.format("%.2f", delta) + " % in word length");
        }

        int indel = ArrayEditDistance.distance(a1.tokens(), a2.tokens(),
                EditDistanceType.INDEL);
        return (l1 + l2 - indel) / (double) (2 * l1);
    }

    /**
     * Compute word error rate
     *
     * @param s1 reference text
     * @param s2 fuzzy text
     * @return word error rate with respect to first file
     */
    public static double wer(String s1, String s2) {
        TokenArrayFactory factory = new TokenArrayFactory(false); // case unsensitive  
        TokenArray a1 = factory.newTokenArray(s1);
        TokenArray a2 = factory.newTokenArray(s2);
       
        return wer(a1, a2);
    }
    
    /**
     * Compute bag-of-word error rate
     * @param s1 reference string
     * @param s2 fuzzy string string
     * @return the word error rate between the (unsorted) strings
     */
    public static double ber(String s1, String s2) {
        BagOfWords bow1 = new BagOfWords(s1);
        BagOfWords bow2 = new BagOfWords(s2);
        
        return bow1.distance(bow2) / (double) bow1.total();
    }
}
