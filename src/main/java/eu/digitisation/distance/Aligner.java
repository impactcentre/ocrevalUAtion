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

import static eu.digitisation.distance.EdOp.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Aligner alignments as HTML text
 *
 * @author R.C.C
 */
public class Aligner {

    /**
     * @return 3-wise minimum.
     */
    private static int min(int x, int y, int z) {
        return Math.min(x, Math.min(y, z));
    }

    /**
     * Shows text alignment based on a pseudo-Levenshtein distance where
     * white-spaces are not allowed to be confused with text or vice-versa
     *
     * @param first reference string
     * @param second fuzzy string
     * @param file the output file
     */
    public static void asHTML(String first, String second, File file) {
        try (PrintWriter writer = new PrintWriter(file)) {
            writer.write(toHTML(first, second));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Aligner.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Auxiliary functions: some will be removed in the end
    private static String font(String color, char c) {
        return "<font color=\"" + color + "\">" + c + "</font>";
    }

    private static String span(String color, char text, char alt) {
        return "<span title=\"" + alt + "\">"
                + font(color, text) + "</span>";
    }

    private static String font(String color, String text) {
        return "<font color=\"" + color + "\">" + text + "</font>";
    }

    private static String span(String color, String text, String alt) {
        return "<span title=\"" + alt + "\">"
                + font(color, text) + "</span>";
    }

    /**
     * Shows text alignment based on a pseudo-Levenshtein distance where
     * white-spaces are not allowed to be confused with text or vice-versa
     *
     * @param first
     * @param second
     * @return HTML representation of the alignment
     */
    private static String toHTML(String first, String second) {
        int l1 = first.length();
        int l2 = second.length();
        int[][] A;
        EditTable B;
        StringBuilder builder1 = new StringBuilder();
        StringBuilder builder2 = new StringBuilder();

        // intialize
        A = new int[2][second.length() + 1];
        B = new EditTable(first.length() + 1, second.length() + 1);
        // Compute first row
        A[0][0] = 0;
        B.set(0, 0, EdOp.KEEP);
        for (int j = 1; j <= second.length(); ++j) {
            A[0][j] = A[0][j - 1] + 1;
            B.set(0, j, EdOp.INSERT);
        }

        // Compute other rows
        for (int i = 1; i <= first.length(); ++i) {
            char c1 = first.charAt(l1 - i);
            A[i % 2][0] = A[(i - 1) % 2][0] + 1;
            B.set(i, 0, EdOp.DELETE);
            for (int j = 1; j <= second.length(); ++j) {
                char c2 = second.charAt(l2 - j);

                if (c1 == c2) {
                    A[i % 2][j] = A[(i - 1) % 2][j - 1];
                    B.set(i, j, EdOp.KEEP);
                } else if (Character.isSpaceChar(c1) ^ Character.isSpaceChar(c2)) {
                    // No alignment between blank and not-blank
                    if (A[(i - 1) % 2][j] < A[i % 2][j - 1]) {
                        A[i % 2][j] = A[(i - 1) % 2][j] + 1;
                        B.set(i, j, EdOp.DELETE);
                    } else {
                        A[i % 2][j] = A[i % 2][j - 1] + 1;
                        B.set(i, j, EdOp.INSERT);
                    }
                } else {
                    A[i % 2][j] = min(A[(i - 1) % 2][j] + 1,
                            A[i % 2][j - 1] + 1,
                            A[(i - 1) % 2][j - 1] + 1);
                    if (A[i % 2][j] == A[(i - 1) % 2][j] + 1) {
                        B.set(i, j, EdOp.DELETE);
                    } else if (A[i % 2][j] == A[i % 2][j - 1] + 1) {
                        B.set(i, j, EdOp.INSERT);
                    } else {
                        B.set(i, j, EdOp.SUBSTITUTE);
                    }
                }
            }
        }

        // Output
        int i = first.length();
        int j = second.length();
        int len;
        String s1;
        String s2;

        builder1.append("<html>")
                .append("<meta http-equiv=\"content-type\"")
                .append("content=\"text/html; charset=UTF-8\"><body>")
                .append("<h2>Reference</h2>");
        while (i > 0 && j > 0) {
            switch (B.get(i, j)) {
                case KEEP:
                    builder1.append(first.charAt(l1 - i));
                    builder2.append(first.charAt(l1 - i));
                    --i;
                    --j;
                    break;
                case DELETE:
                    len = 1;
                    while (len < i && B.get(i - len, j) == EdOp.DELETE) {
                        ++len;
                    }
                    s1 = first.substring(l1 - i, l1 - i + len);
                    s2 = s1.replaceAll(".", "#");
                    builder1.append(font("red", s1));
                    builder2.append(span("red", s2, s1));
                    i -= len;
                    break;
                case INSERT:
                    len = 1;
                    
                    while (len < j && B.get(i, j - len) == EdOp.INSERT) {
                        ++len;
                    }
                    s2 = second.substring(l2 - j, l2 - j + len);
                    s1 = s2.replaceAll(".", "#");
                    builder2.append(font("magenta", s2));
                    builder1.append(span("magenta", s1, s2));
                    j -= len;
                    break;
                case SUBSTITUTE:
                    len = 1;
                    while (len < i && len < j 
                            && B.get(i - len, j - len) == EdOp.SUBSTITUTE) {
                        ++len;
                    }
                    s1 = first.substring(l1 - i, l1 - i + len);
                    s2 = second.substring(l2 - j, l2 - j + len);
                    builder1.append(span("blue", s1, s2));
                    builder2.append(span("blue",s2, s1));
                    i -= len;
                    j -= len;
                    break;
            }
        }
        if (i > 0) {
            s1 = first.substring(l1 - i, l1);
            s2 = s1.replaceAll(".", "#");
            builder1.append(font("red", s1));
            builder2.append(span("red", s2, s1));
        }
        if (j > 0) {
            s2 = second.substring(l2 - j, l2);
            s1 = s2.replaceAll(".", "#");
            builder2.append(font("magenta", s2));
            builder1.append(span("magenta", s1, s2));
        }

        builder1.append("<br/><br/><h2>OCR</h2>");
        builder2.append("</body></html>");

        return builder1.toString() + builder2.toString();

    }
}
