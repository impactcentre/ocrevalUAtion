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

import eu.digitisation.distance.TextFileEncoder;
import eu.digitisation.distance.StringEditDistance;
import eu.digitisation.distance.ArrayEditDistance;

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

        return StringEditDistance.levenshtein(s1, s2)
                / (double) l1;
        /*
         int indel = StringEditDistance.indel(b1.toString(), b2.toString());
         return (l1 - l2 + indel) / l1;
         */
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

        return StringEditDistance.DL(s1, s2)
                / (double) l1;
        /*
         int indel = StringEditDistance.indel(b1.toString(), b2.toString());
         return (l1 - l2 + indel) / l1;
         */
    }

    /**
     * Compute word error rate (words represented as integer codes)
     *
     * @param a1 array of integers
     * @param a2 array of integers
     * @return error rate
     */
    private static double wer(Integer[] a1, Integer[] a2) {
        int l1 = a1.length;
        int l2 = a2.length;
        double delta = (100.00 * Math.abs(l1 - l2)) / (l1 + l2);

        if (delta > 20) {
            System.err.println("Warning: files differ a "
                    + String.format("%.2f", delta) + " % in word length");
        }
     
        int indel = ArrayEditDistance.indel(a1, a2);
        return (Math.abs(l1 - l2) + indel) / (double) (2 * l1);
    }

    /**
     * Compute word error rate
     *
     * @param s1 reference text
     * @param s2 fuzzy text
     * @return word error rate with respect to first file
     */
    public static double wer(String s1, String s2) {
        TextFileEncoder encoder = new TextFileEncoder(false); // case folding
        Integer[] a1 = encoder.encode(s1);
        Integer[] a2 = encoder.encode(s2);
        return wer(a1, a2);
    }
}
