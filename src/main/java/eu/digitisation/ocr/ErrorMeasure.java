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

import eu.digitisation.distance.EdOp;
import eu.digitisation.Main;
import eu.digitisation.distance.TextFileEncoder;
import eu.digitisation.distance.StringEditDistance;
import eu.digitisation.distance.ArrayEditDistance;
import eu.digitisation.math.BiCounter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        /*
         return ArrayEditDistance.levenshtein(a1, a2)
         / (double) l1;
         */
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

    /**
     * Prints separate statistics of errors for every character
     *
     * @param s1 the reference text
     * @param s2 the fuzzy text
     * @param recordSeparator text between data records
     * @param fieldSeparator text between data fields
     * @return text with the statistics: every character separated by a record
     * separator and every type of edit operation separated by field separator.
     *
     */
    public static String stats(String s1, String s2,
            String recordSeparator, String fieldSeparator) {
        StringBuilder builder = new StringBuilder();
        BiCounter<Character, EdOp> stats = StringEditDistance.stats(s1, s2);

        builder.append("Character")
                .append(fieldSeparator).append("Total")
                .append(fieldSeparator).append("Spurious")
                .append(fieldSeparator).append("Confused")
                .append(fieldSeparator).append("Lost")
                .append(fieldSeparator).append("Error rate");

        for (Character c : stats.leftKeySet()) {
            int spu = stats.value(c, EdOp.INSERT);
            int sub = stats.value(c, EdOp.SUBSTITUTE);
            int add = stats.value(c, EdOp.DELETE);
            int tot = stats.value(c, EdOp.KEEP) + sub + add;
            double rate = (spu + sub + add) / (double) tot * 100;
            builder.append(recordSeparator);
            builder.append(c)
                    .append("[")
                    .append(Integer.toHexString(c))
                    .append("]")
                    .append(fieldSeparator).append(tot)
                    .append(fieldSeparator).append(spu)
                    .append(fieldSeparator).append(sub)
                    .append(fieldSeparator).append(add)
                    .append(fieldSeparator).append(String.format("%.2f", rate));
        }
        return builder.toString();
    }
}
