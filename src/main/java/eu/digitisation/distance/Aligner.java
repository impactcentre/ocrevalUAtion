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

    private static String font(String color, char c) {
        return "<font color=\"" + color + "\">" + c + "</font>";
    }

    private static String span(String color, char text, char alt) {
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
                boolean notSpaces = !(Character.isSpaceChar(c1)
                        || Character.isSpaceChar(c2));
                if (c1 == c2 && notSpaces) {
                    A[i % 2][j] = A[(i - 1) % 2][j - 1];
                    B.set(i, j, EdOp.KEEP);
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
        builder1.append("<html>")
                .append("<meta http-equiv=\"content-type\"")
                .append("content=\"text/html; charset=UTF-8\"><body>");
        while (i > 0 && j > 0) {
            switch (B.get(i, j)) {
                case KEEP:
                    builder1.append(first.charAt(l1 - i));
                    builder2.append(first.charAt(l1 - i));
                    --i;
                    --j;
                    break;
                case DELETE:
                    builder1.append(font("red", first.charAt(l1 - i)));
                    builder2.append(span("red", '#', first.charAt(l1 - i)));
                    --i;
                    break;
                case INSERT:
                    builder1.append(span("blue", '#', second.charAt(l2 - j)));
                    builder2.append(font("blue", second.charAt(l2 - j)));
                    --j;
                    break;
                case SUBSTITUTE:
                    builder1.append(span("green",
                            first.charAt(l1 - i), second.charAt(l2 - j)));
                    builder2.append(span("green",
                            second.charAt(l2 - j), first.charAt(l1 - i)));
                    --i;
                    --j;
                    break;
            }
        }
        while (i > 0) {
            builder1.append(font("red", first.charAt(l1 - i)));
            builder2.append(span("red", '#', first.charAt(l1 - i)));
            --i;
        }
        while (j > 0) {
            builder1.append(span("blue", '#', second.charAt(l2 - j)));
            builder2.append(font("blue", second.charAt(l2 - j)));
            --j;
        }
        builder1.append("<br/><br/>");
        builder2.append("</body></html>");

        return builder1.toString() + builder2.toString();

    }
}
