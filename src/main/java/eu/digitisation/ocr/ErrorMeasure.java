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

import eu.ditisation.distance.TextFileEncoder;
import eu.ditisation.distance.StringEditDistance;
import eu.ditisation.distance.ArrayEditDistance;
import eu.digitisation.util.Counter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Properties;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Compute character error rate
 *
 * @version 2012.06.20
 */
public class ErrorMeasure {

    static int maxlen;

    static {
        Properties prop = new Properties();
        try {
            prop.load(new FileReader("/ErrorMeasure.properties"));
            maxlen = Integer.parseInt(prop.getProperty("maxlen"));
        } catch (IOException ex) {
            Logger.getLogger(ErrorMeasure.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Collapse whitespace: contiguous spaces are considered a single one
     *
     * @param file the input file
     * @param encoding the text encoding
     * @return String as StringBuilder
     * @throws IOException
     */
    protected static StringBuilder trim(File file, String encoding)
            throws IOException {
        StringBuilder builder = new StringBuilder();
        FileInputStream fis = new FileInputStream(file);
        InputStreamReader isr = new InputStreamReader(fis, encoding);
        BufferedReader reader = new BufferedReader(isr);
        int size = 0;

        while (reader.ready()) {
            String line = reader.readLine();
            if (size > 0) {
                builder.append(' ');
            }
            size += line.length();
            if (size > maxlen) {
                throw new RuntimeException("On-line test limited to "
                        + maxlen + " characters");
            }
            builder.append(line.replaceAll("\\p{Space}+", " ").trim());
        }
        return builder;
    }

    /**
     * Compute character error rate
     *
     * @param fileName1 file containing the reference text
     * @param encoding1 first file encoding
     * @param fileName2 file containing the fuzzy text
     * @param encoding2 second file encoding
     * @return character error rate with respect to the reference file
     */
    public static double cer(String fileName1, String encoding1,
            String fileName2, String encoding2) {
        return cer(new File(fileName1), encoding1, new File(fileName2), encoding2);
    }

    /**
     * Compute character error rate
     *
     * @param file1 containing the reference text
     * @param encoding1 first file encoding
     * @param file2 file containing the fuzzy text
     * @param encoding2 second file encoding
     * @return character error rate with respect to the reference file
     */
    public static double cer(File file1, String encoding1,
            File file2, String encoding2) {

        try {
            StringBuilder b1 = trim(file1, encoding1);
            StringBuilder b2 = trim(file2, encoding2);
            int l1 = b1.length();
            int l2 = b2.length();

            double delta = (100.00 * Math.abs(l1 - l2)) / (l1 + l2);

            if (delta > 20) {
                System.err.println("Warning: files differ a "
                        + delta + " % in character length");
            }


            return StringEditDistance.levenshtein(b1.toString(), b2.toString())
                    / (double) l1;
            /*
             int indel = StringEditDistance.indel(b1.toString(), b2.toString());
             return (l1 - l2 + indel) / l1;
             */
        } catch (IOException ex) {
            Logger.getLogger(ErrorMeasure.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }

    /**
     * Compute word error rate
     *
     * @param fileName1 file containing the reference text
     * @param encoding1 first file encoding
     * @param fileName2 file containing the fuzzy text
     * @param encoding2 second file encoding
     * @return word error rate with respect to first file (recall)
     */
    public static double wer(String fileName1, String encoding1,
            String fileName2, String encoding2) {
        return wer(new File(fileName1), encoding1, new File(fileName2), encoding2);
    }

    /**
     * Compute word error rate
     *
     * @param file1 containing the reference text
     * @param encoding1 first file encoding
     * @param file2 file containing the fuzzy text
     * @param encoding2 second file encoding
     * @return word error rate with respect to first file
     */
    public static double wer(File file1, String encoding1,
            File file2, String encoding2) {
        TextFileEncoder encoder = new TextFileEncoder(false); // case folding
        Integer[] a1 = encoder.encode(file1, encoding1);
        Integer[] a2 = encoder.encode(file2, encoding2);

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
        return (l1 - l2 + indel) / (double) (2 * l1);
    }

    /**
     * Computes separate statistics for every character error rate
     *
     * @param file1
     * @param encoding1
     * @param file2
     * @param encoding2
     * @return
     */
    public static TreeMap<Character, Double> stats(File file1, String encoding1,
            File file2, String encoding2) {
        TreeMap<Character, Double> map = new TreeMap<Character, Double>();
        Counter<Character> total = new Counter<Character>();
        Counter<Character> wrong = new Counter<Character>();
        try {
            String s1 = trim(file1, encoding1).toString();
            String s2 = trim(file2, encoding2).toString();
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
        for (Character c : total.keySet()) {
            double rate = wrong.value(c) / (double) total.value(c);
            map.put(c, rate);
        }
        return map;
    }

    /**
     * Computes separate statistics of errors for every character
     *
     * @param file1 the reference file
     * @param encoding1 the encoding of reference file
     * @param file2 the sample file
     * @param encoding2 the encoding of the sample file
     * @return a map with the number of insertions, substitutions and deletions
     * for every character
     */
    public static Counter<Character>[] errors(File file1, String encoding1,
            File file2, String encoding2) {
        Counter<Character> dummy = new Counter<>();
        Counter<Character>[] map;
        map = (Counter<Character>[]) java.lang.reflect.Array.newInstance(dummy.getClass(), 4);
        try {
            String s1 = trim(file1, encoding1).toString();
            String s2 = trim(file2, encoding2).toString();
            int[] alignments = StringEditDistance.align(s1, s2);
            int m = 0; // target character
            for (int n = 0; n < alignments.length; ++n) {
                char c1 = s1.charAt(n);
                map[0].inc(c1);  // total
                if (alignments[n] < 0) {
                    map[3].inc(c1);  // must be deleted
                } else {
                    char c2 = s2.charAt(alignments[n]);
                    if (c1 != c2) {
                        map[2].inc(c1); // replaced
                    }
                    // spurious charcters
                    while (m < alignments[n]) {
                        map[1].inc(s2.charAt(m));
                        ++m;
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ErrorMeasure.class.getName()).log(Level.SEVERE, null, ex);
        }
        return map;
    }
}
