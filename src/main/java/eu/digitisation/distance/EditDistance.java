/*
 * Copyright (C) 2014 Universidad de Alicante
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

import eu.digitisation.io.TextContent;
import eu.digitisation.io.WarningException;
import eu.digitisation.math.MinimalPerfectHash;
import java.io.File;

/**
 * Provides linear time implementations of some popular edit distance methods
 * operating on strings
 *
 * @version 2014.01.25
 */
public class EditDistance {

    /**
     * @param s1 the first string.
     * @param s2 the second string.
     * @param w weights for basic edit operations
     * @param chunkLen the length of the chunks analyzed at every step (must be
     * strictly greater than 1)
     * @return the approximate (linear time) Levenshtein distance between first
     * and second.
     */
    public static int charDistance(String s1, String s2, EdOpWeight w, int chunkLen) {
        EditSequence seq = new EditSequence();
        int len1 = s1.length();
        int len2 = s2.length();

        if (chunkLen < 2) {
            throw new IllegalArgumentException("chunkLen mut be greater than 1");
        }
        while (seq.shift1() < len1 || seq.shift2() < len2) {
            int high1 = Math.min(seq.shift1() + chunkLen, len1);
            int high2 = Math.min(seq.shift2() + chunkLen, len2);
            String sub1 = s1.substring(seq.shift1(), high1);
            String sub2 = s2.substring(seq.shift2(), high2);
            EditSequence subseq = new EditSequence(sub1, sub2, w);
            EditSequence head = (high1 < len1 || high2 < len2)
                    ? subseq.head(subseq.size() / 2)
                    : subseq;

            seq.append(head);
        }

        return seq.cost();
    }
    
       /**
     * @param s1 the first string.
     * @param s2 the second string.
     * @param chunkLen the length of the chunks analyzed at every step (must be
     * strictly greater than 1)
     * @return the approximate (linear time) Levenshtein distance between first
     * and second.
     */
    public static int wordDistance(String s1, String s2, int chunkLen) {
        EditSequence seq = new EditSequence();
        MinimalPerfectHash mph = new MinimalPerfectHash(true); // case sensitive
        TokenArray a1 = new TokenArray(mph, s1);
        TokenArray a2 = new TokenArray(mph, s2);
        int len1 = a1.length();
        int len2 = a2.length();

        if (chunkLen < 2) {
            throw new IllegalArgumentException("chunkLen mut be greater than 1");
        }
        while (seq.shift1() < len1 || seq.shift2() < len2) {
            int high1 = Math.min(seq.shift1() + chunkLen, len1);
            int high2 = Math.min(seq.shift2() + chunkLen, len2);
            TokenArray sub1 = a1.subArray(seq.shift1(), high1);
            TokenArray sub2 = a2.subArray(seq.shift2(), high2);
            EditSequence subseq = new EditSequence(sub1, sub2);

            EditSequence head = (high1 < len1 || high2 < len2)
                    ? subseq.head(subseq.size() / 2)
                    : subseq;

            seq.append(head);
        }

        return seq.cost();
    }

    /**
     *
     * @param first the first string.
     * @param second the second string.
     * @param chunkLen the length of the chunks analyzed at every step
     * @param type the type of distance to be computed
     * @return the distance between first and second (defaults to Levenshtein)
     * @throws java.lang.NoSuchMethodException
     */
    public static int distance(String first, String second, int chunkLen, EditDistanceType type) throws NoSuchMethodException {
        switch (type) {
            case OCR_CHAR:
                EdOpWeight w =new OcrOpWeight();
                return charDistance(first, second, w, chunkLen);
            case OCR_WORD:
                return wordDistance(first, second, chunkLen);             
            default:
                throw new java.lang.NoSuchMethodException(type + " distance still to be implemented");

        }
    }

    public static void main(String[] args) throws WarningException, NoSuchMethodException {
        File f1 = new File(args[0]);
        File f2 = new File(args[1]);
        int len = Integer.parseInt(args[2]);
        String s1 = new TextContent(f1, null, null).toString();
        String s2 = new TextContent(f2, null, null).toString();
        int d = EditDistance.distance(s1, s2, len, EditDistanceType.OCR_CHAR);
        System.out.println(d);
        d = EditDistance.distance(s1, s2, len, EditDistanceType.OCR_WORD);
        System.out.println(d);
    }

}
