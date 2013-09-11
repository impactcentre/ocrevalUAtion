/**
 * Copyright (C) 2013 Rafael C. Carrasco
 * This code can be distributed or
 * modified under the terms of the GNU General Public License V3. 
 */

package eu.digitisation.text;

import java.io.*;

/**
 * Transformations between  unicode strings and codepoints 
 * @version 2012.06.20
 */
class UnicodeReader {
    /**
     * Transform a sequence of unicode values (contiguous blocks of four
     * hexadecimal digits) into the string they represent. For example,
     * "00410042" represents "AB"
     *
     * @param s the sequence of one or more unicode values
     * @return the string represented by s
     */
    protected static String codepointsToString(String s) {
        StringBuilder buff = new StringBuilder();
        for (int pos = 0; pos + 3 < s.length(); pos += 4) {
            String sub = s.substring(pos, pos + 4);
            int val = Integer.parseInt(sub, 16);
            buff.append((char) val);
        }
        return buff.toString();
    }

    /**
     * Build a string from the codepoints (unicode values) defining its content
     *
     * @param codes
     * @return
     */
    public static String codepointsToString(int[] codes) {
        StringBuilder buff = new StringBuilder();
        for (int code : codes) {
            buff.append((char) code);
        }
        return buff.toString();
    }

    /**
     * Convert a string into a sequence of unicode values
     *
     * @param s a Java String
     * @return The array of unicode values of the characters in s
     */
    public static int[] toCodepoints(String s) {
        int[] codes = new int[s.length()];
        for (int n = 0; n < s.length(); ++n) {
            codes[n] = (int) s.charAt(n);
        }
        return codes;
    }

    /**
     * Transform an array of integers into their hexadecimal representation
     *
     * @param values an integer array
     * @return the hexadecimal strings representing their value.
     */
    private static String[] toHexString(int[] values) {
        String[] hex = new String[values.length];
        for (int n = 0; n < values.length; ++n) {
            hex[n] = Integer.toHexString(values[n]);
        }
        return hex;
    }

    /**
     * Convert a string into a sequence of unicode hexadecimal values
     *
     * @param s a Java String
     * @return The array of unicode values (hexadecimal representation) of the
     * characters in s
     */
    public static String[] toHexCodepoints(String s) {
        return toHexString(toCodepoints(s));
    }

    /**
     * Read a text file and print the content as codepoints (unicode values) in
     * it
     *
     * @param file the input file
     * @throws Exception
     */
    public static void printHexCodepoints(File file)
            throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        while (reader.ready()) {
            String line = reader.readLine();
            String[] hexcodes = toHexCodepoints(line);
            System.out.println(java.util.Arrays.toString(hexcodes));
        }
    }

    public static void main(String[] args) throws Exception {
        if (args[0].equals("-s")) {
            UnicodeReader.toCodepoints(args[1]);
        } else {
            for (String arg : args) {
                File file = new File(arg);
                UnicodeReader.printHexCodepoints(file);
            }
        }
    }
}