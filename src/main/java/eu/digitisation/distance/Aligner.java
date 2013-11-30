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
import eu.digitisation.xml.DocumentBuilder;
import org.w3c.dom.Element;

/**
 * Alignments between 2 texts (output in HTML format)
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
     * Compute the table of minimal basic edit operations needed to transform
     * first into second
     *
     * @param first source string
     * @param second target string
     * @return the table of minimal basic edit operations needed to transform
     * first into second
     */
    private static EditTable align(String first, String second) {
        int l1; // length of first 
        int l2; // length of second
        int[][] A; // distance table
        EditTable B; // edit operations

        // intialize (will be  a separete function returning B)
        l1 = first.length();
        l2 = second.length();
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
        return B;
    }

    /**
     * Shows text alignment based on a pseudo-Levenshtein distance where
     * white-spaces are not allowed to be confused with text or vice-versa
     *
     * @param first
     * @param second
     * @return a table in HTML format showing the alignments
     */
    public static Element alignmentTable(String first, String second) {
        EditTable B = align(first, second);
        DocumentBuilder builder = new DocumentBuilder("table");
        Element table = builder.root();
        Element row;
        Element cell1;
        Element cell2;
        int l1;
        int l2;
        int len;
        int i;
        int j;
        String s1;
        String s2;

        // features
        table.setAttribute("border", "1");
        // content 
        row = builder.addElement("tr");
        cell1 = builder.addElement(row, "td");
        cell2 = builder.addElement(row, "td");
        cell1.setAttribute("align", "center");
        cell2.setAttribute("align", "center");
        builder.addTextElement(cell1, "h3", "Reference");
        builder.addTextElement(cell2, "h3", "OCR");
        row = builder.addElement("tr");
        cell1 = builder.addElement(row, "td");
        cell2 = builder.addElement(row, "td");

        l1 = first.length();
        l2 = second.length();
        i = l1;
        j = l2;
        while (i > 0 && j > 0) {
            switch (B.get(i, j)) {
                case KEEP:
                    len = 1;
                    while (len < i && len < j
                            && B.get(i - len, j - len) == EdOp.KEEP) {
                        ++len;
                    }
                    s1 = first.substring(l1 - i, l1 - i + len);
                    s2 = second.substring(l2 - j, l2 - j + len);
                    builder.addText(cell1, s1);
                    builder.addText(cell2, s2);
                    i -= len;
                    j -= len;
                    break;
                case DELETE:
                    len = 1;
                    while (len < i && B.get(i - len, j) == EdOp.DELETE) {
                        ++len;
                    }
                    s1 = first.substring(l1 - i, l1 - i + len);
                    //s2 = "";//s1.replaceAll(".", "&nbsp");
                    builder.addTextElement(cell1, "font", s1)
                            .setAttribute("style", "background-color:aquamarine");
                    //builder.addText(cell2, s2);
                    i -= len;
                    break;
                case INSERT:
                    len = 1;

                    while (len < j && B.get(i, j - len) == EdOp.INSERT) {
                        ++len;
                    }
                    s2 = second.substring(l2 - j, l2 - j + len);
                    //s1 = "";//s2.replaceAll(".", "&nbsp;");
                    builder.addTextElement(cell2, "font", s2)
                            .setAttribute("style", "background-color:aquamarine");
                    //builder.addText(cell2, s2);
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
                    Element span1 = builder.addElement(cell1, "span");
                    Element span2 = builder.addElement(cell2, "span");
                    span1.setAttribute("title", s2);
                    span2.setAttribute("title", s1);
                    builder.addTextElement(span1, "font", s1)
                            .setAttribute("color", "red");

                    builder.addTextElement(span2, "font", s2)
                            .setAttribute("color", "red");
                    i -= len;
                    j -= len;
                    break;
            }
        }
        if (i > 0) {
            s1 = first.substring(l1 - i, l1);
            //s2 = "";//s1.replaceAll(".", "#");
            builder.addTextElement(cell1, "font", s1)
                    .setAttribute("style", "background-color:aquamarine");

        }
        if (j > 0) {
            s2 = second.substring(l2 - j, l2);
            //s1 = "";//s2.replaceAll(".", "#");
            builder.addTextElement(cell2, "font", s2)
                    .setAttribute("style", "background-color:aquamarine");
        }
        return builder.document().getDocumentElement();
    }
}
