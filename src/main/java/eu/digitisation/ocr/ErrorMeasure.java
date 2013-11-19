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

import eu.digitisation.util.Counter;
import java.io.*;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Compute character error rate
 *
 * @version 2012.06.20
 */
public class ErrorMeasure {

    /**
     * Collapse whitespace
     *
     * @return String as StringBuilder
     * @throws IOException
     */
    public static StringBuilder merge(File file, String encoding)
            throws IOException {
        StringBuilder builder = new StringBuilder();
        FileInputStream fis = new FileInputStream(file);
        BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
        int size = 0;

        while (reader.ready()) {
            String line = reader.readLine();
            size += line.length();
            if (size > 10000) {
                throw new RuntimeException("Online test limited to 10000 characters");
            }
            builder.append(' ').append(line.replaceAll("\\p{Space}+", " "));
        }
        return builder;
    }

    /**
     * Compute character error rate
     *
     * @param fileName1 (target)
     * @param fileName2 (output)
     * @return character error rate with respect to first file
     */
    public static double cer(String fileName1, String encoding1,
            String fileName2, String encoding2) {
        return cer(new File(fileName1), encoding1, new File(fileName2), encoding2);
    }

    /**
     * Compute character error rate
     *
     * @param file1 (target)
     * @param file2 (output)
     * @return character error rate with respect to first file
     */
    public static double cer(File file1, String encoding1,
            File file2, String encoding2) {
        try {
            StringBuilder b1 = merge(file1, encoding1);
            StringBuilder b2 = merge(file2, encoding2);
            int l1 = b1.length();
            int l2 = b2.length();
            double delta = (100.00 * Math.abs(l1 - l2)) / (l1 + l2);

            if (delta > 20) {
                System.err.println("Warning: files differ a " + delta + " % in character length");
            }
            return StringEditDistance.levenshteinDistance(b1.toString(), b2.toString())
                    / (double) l1;
        } catch (IOException ex) {
            Logger.getLogger(ErrorMeasure.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }

    /**
     * Compute word error rate
     *
     * @param fileName1 (target)
     * @param fileName2 (output)
     * @return word error rate with respect to first file
     */
    public static double wer(String fileName1, String encoding1,
            String fileName2, String encoding2) {
        return wer(new File(fileName1), encoding1, new File(fileName2), encoding2);
    }

    /**
     * Compute word error rate
     *
     * @param file1 (target)
     * @param file2 (output)
     * @return word error rate with respect to first file
     */
    public static double wer(File file1, String encoding1,
            File file2, String encoding2) {
        FileEncoder encoder = new FileEncoder();
        Integer[] a1 = encoder.encode(file1, encoding1);
        Integer[] a2 = encoder.encode(file2, encoding2);
        int l1 = a1.length;
        int l2 = a2.length;
        double delta = (100.00 * Math.abs(l1 - l2)) / (l1 + l2);

        if (delta > 20) {
            System.err.println("Warning: files differ a " + delta + " % in word length");
        }
        return ArrayEditDistance.levenshteinDistance(a1, a2)
                / (double) l1;
    }

    public static TreeMap<Character, Double> stats(File file1, String encoding1,
            File file2, String encoding2) {
        TreeMap<Character, Double> map = new TreeMap<Character, Double>();
        Counter<Character> total = new Counter<Character>();
        Counter<Character> wrong = new Counter<Character>();
        try {
            String s1 = merge(file1, encoding1).toString();
            String s2 = merge(file2, encoding2).toString();
            int[] alignments = StringEditDistance.align(s1, s2);
            for (int n = 0; n < alignments.length; ++n) {
                char c1 = s1.charAt(n);
                total.inc(c1);
                if (alignments[n] < 0) {
                    wrong.inc(c1);
                } else {
                    char c2 = s2.charAt(alignments[n]);
                    if (c1 != c2) {
                        wrong.inc(c1);
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ErrorMeasure.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (Character c: total.keySet()) {
            double rate = wrong.value(c) / (double) total.value(c);
            map.put(c, rate);
        }
        return map;
    }
}
