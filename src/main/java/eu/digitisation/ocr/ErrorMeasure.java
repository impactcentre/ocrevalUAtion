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

import eu.digitisation.Main;
import eu.digitisation.distance.TextFileEncoder;
import eu.digitisation.distance.StringEditDistance;
import eu.digitisation.distance.ArrayEditDistance;
import eu.digitisation.math.BiCounter;
import eu.digitisation.math.Counter;
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
     * Compute character error rate
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
                    + delta + " % in character length");
        }

        return StringEditDistance.levenshtein(s1, s2)
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
                    + delta + " % in word length");
        }
        /*
         return ArrayEditDistance.levenshtein(a1, a2)
         / (double) l1;
         */
        int indel = ArrayEditDistance.indel(a1, a2);
        System.out.println(l1+" "+l2+" "+indel);
        return (Math.abs(l1 - l2) + indel)/ (double)(2 * l1);
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
     * Computes separate statistics of errors for every character
     *
     * @param s1 the reference text
     * @param s2 the fuzzy text
     * @return a counter with the number of insertions, substitutions and
     * deletions for every character
     */
    public static BiCounter<Character, EdOp> stats(String s1, String s2) {
        int l1 = s1.length();
        int l2 = s2.length();
        BiCounter<Character, EdOp> stats = new BiCounter<>();

        int[] alignments = StringEditDistance.align(s1, s2);
        int last = -1; // last aligned character in target

        for (int n = 0; n < alignments.length; ++n) {
            char c1 = s1.charAt(n);
//            stats.ap[0].inc(c1);  // total
            if (alignments[n] < 0) {
                stats.inc(c1, EdOp.DELETE);  // must be deleted
            } else {
                char c2 = s2.charAt(alignments[n]);
                if (c1 != c2) {
                    stats.inc(c1, EdOp.SUBSTITUTE); // replaced  
                } else {
                    stats.inc(c1, EdOp.KEEP); // correct
                }

                // spurious characters
                //int jump = alignments[n] - last - 1;
                //System.out.println("jump="+jump);
                while (last + 1 < alignments[n]) {
                    stats.inc(s2.charAt(last + 1), EdOp.INSERT);
                    ++last;
                }
                ++last;
            }
        }
        return stats;
    }

    /**
     * Prints separate statistics of errors for every character in spreadsheet
     * (CSV) format
     *
     * @param s1 the reference text
     * @param s2 the fuzzy text
     * @param file the output file
     * @param fieldSeparator filed separator in CSV
     * @throws java.io.FileNotFoundException
     *
     */
    public static void stats2CSV(String s1, String s2,
            File file, char fieldSeparator)
            throws FileNotFoundException {
        String sep = fieldSeparator + " ";
        StringBuilder line = new StringBuilder();

        try (PrintWriter writer = new PrintWriter(file)) {
            writer.println("Error rate per character ant type");
            // Statistics per character
            line.append("Character")
                    .append(sep).append("Total")
                    .append(sep).append("Spurious")
                    .append(sep).append("Confused")
                    .append(sep).append("Lost")
                    .append(sep).append("Error rate");
            writer.println(line.toString());
            BiCounter<Character, EdOp> stats = ErrorMeasure.stats(s1, s2);
            for (Character c : stats.leftKeySet()) {
                int spu = stats.value(c, EdOp.INSERT);
                int sub = stats.value(c, EdOp.SUBSTITUTE);
                int add = stats.value(c, EdOp.DELETE);
                int tot = stats.value(c, EdOp.KEEP) + sub + add;
                double rate = (spu + sub + add) / (double) tot * 100;

                line.setLength(0);
                line.append(c)
                        .append("[")
                        .append(Integer.toHexString(c))
                        .append("]")
                        .append(sep).append(tot)
                        .append(sep).append(spu)
                        .append(sep).append(sub)
                        .append(sep).append(add)
                        .append(sep).append(String.format("%.2f", rate));
                writer.println(line.toString());
            }
            writer.close();

        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
