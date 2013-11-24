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

import eu.digitisation.ocr.ErrorMeasure;
import eu.digitisation.math.ArrayMath;
import eu.digitisation.math.BiCounter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides basic implementations of some popular edit distance methods
 * operating on strings (currently, Levenshtein and indel)
 *
 * @version 2011.03.10
 */
public class StringEditDistance {

    /**
     * @return 3-wise minimum.
     */
    private static int min(int x, int y, int z) {
        return Math.min(x, Math.min(y, z));
    }

    /**
     * @param first the first string.
     * @param second the second string.
     * @return the indel distance between first and second.
     */
    public static int indel(String first, String second) {
        int i, j;
        int[][] A = new int[2][second.length() + 1];

        // Compute first row
        A[0][0] = 0;
        for (j = 1; j <= second.length(); ++j) {
            A[0][j] = A[0][j - 1] + 1;
        }

        // Compute other rows
        for (i = 1; i <= first.length(); ++i) {
            A[i % 2][0] = A[(i - 1) % 2][0] + 1;
            for (j = 1; j <= second.length(); ++j) {
                if (first.charAt(i - 1) == second.charAt(j - 1)) {
                    A[i % 2][j] = A[(i - 1) % 2][j - 1];
                } else {
                    A[i % 2][j] = Math.min(A[(i - 1) % 2][j] + 1,
                            A[i % 2][j - 1] + 1);
                }
            }
        }
        return A[first.length() % 2][second.length()];
    }

    /**
     * @param first the first string.
     * @param second the second string.
     * @return the Levenshtein distance between first and second.
     */
    public static int levenshtein(String first, String second) {
        int i, j;
        int[][] A;

        // intialize
        A = new int[2][second.length() + 1];

        // Compute first row
        A[0][0] = 0;
        for (j = 1; j <= second.length(); ++j) {
            A[0][j] = A[0][j - 1] + 1;
        }

        // Compute other rows
        for (i = 1; i <= first.length(); ++i) {
            A[i % 2][0] = A[(i - 1) % 2][0] + 1;
            for (j = 1; j <= second.length(); ++j) {
                if (first.charAt(i - 1) == second.charAt(j - 1)) {
                    A[i % 2][j] = A[(i - 1) % 2][j - 1];
                } else {
                    A[i % 2][j] = min(A[(i - 1) % 2][j] + 1,
                            A[i % 2][j - 1] + 1,
                            A[(i - 1) % 2][j - 1] + 1);
                }
            }
        }
        return A[first.length() % 2][second.length()];
    }

    public static BiCounter<Character, EdOp> stats(String first, String second) {
        int i, j;
        int[][] A;
        EditTable B;
        BiCounter<Character, EdOp> stats = new BiCounter<>();

        // intialize
        A = new int[2][second.length() + 1];
        B = new EditTable(first.length() + 1, second.length() + 1);
        // Compute first row
        A[0][0] = 0;
        B.set(0, 0, EdOp.KEEP);
        for (j = 1; j <= second.length(); ++j) {
            A[0][j] = A[0][j - 1] + 1;
            B.set(0, j, EdOp.INSERT);
        }

        // Compute other rows
        for (i = 1; i <= first.length(); ++i) {
            A[i % 2][0] = A[(i - 1) % 2][0] + 1;
            B.set(i, 0, EdOp.DELETE);
            for (j = 1; j <= second.length(); ++j) {
                if (first.charAt(i - 1) == second.charAt(j - 1)) {
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

        i = first.length();
        j = second.length();
        while (i > 0 && j > 0) {
            switch (B.get(i, j)) {
                case KEEP:
                    stats.inc(first.charAt(i - 1), EdOp.KEEP);
                    --i;
                    --j;
                    break;
                case DELETE:
                    stats.inc(first.charAt(i - 1), EdOp.DELETE);
                    --i;
                    break;
                case INSERT:
                    stats.inc(second.charAt(j - 1), EdOp.INSERT);
                    --j;
                    break;
                case SUBSTITUTE:
                    stats.inc(first.charAt(i - 1), EdOp.SUBSTITUTE);

                    --i;
                    --j;
                    break;
            }
        }
        while (i > 0) {
            stats.inc(first.charAt(i - 1), EdOp.DELETE);
            --i;
        }
        while (j > 0) {
            stats.inc(second.charAt(j - 1), EdOp.INSERT);
            --j;

        }

        return stats;
    }

    /**
     * Computes separate statistics of errors for every character
     *
     * @param s1 the reference text
     * @param s2 the fuzzy text
     * @return a counter with the number of insertions, substitutions and
     * deletions for every character
     */
    public static BiCounter<Character, EdOp> stats_old(String s1, String s2) {
        int l1 = s1.length();
        int l2 = s2.length();
        BiCounter<Character, EdOp> stats = new BiCounter<>();

        int[] alignments = StringEditDistance.align(s1, s2);
        int last = -1; // last aligned character in target

        for (int n = 0; n < alignments.length; ++n) {
            char c1 = s1.charAt(n);
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
                while (last + 1 < alignments[n]) {
                    stats.inc(s2.charAt(last + 1), EdOp.INSERT);
                    ++last;
                }
                ++last;
            }
        }
        return stats;
    }

    private static int ldist(BiCounter<Character, EdOp> b) {
        return b.total() - b.value(null, EdOp.KEEP);
    }

    /**
     * Computes separate statistics of errors for every character
     *
     * @param first the first string
     * @param second the second string
     * @return a counter with the number of insertions, substitutions and
     * deletions for every character needed to transform the first string into
     * the second
     */
    public static BiCounter<Character, EdOp> stats_with_bug(String first, String second) {
        int i, j;
        BiCounter<Character, EdOp>[][] A = new BiCounter[2][second.length() + 1];

        // Create table
        A[0] = new BiCounter[second.length() + 1];
        A[1] = new BiCounter[second.length() + 1];
        for (j = 0; j <= second.length(); ++j) {
            A[0][j] = new BiCounter<>();
            A[1][j] = new BiCounter<>();
        }

        // Compute first row
        for (j = 1; j <= second.length(); ++j) {
            A[0][j].add(second.charAt(j - 1), EdOp.DELETE, j);
        }

        // Compute other rows
        for (i = 1; i <= first.length(); ++i) {
            char c1 = first.charAt(i - 1);
            A[i % 2][0].clear();
            A[i % 2][0].add(A[(i - 1) % 2][0])
                    .inc(c1, EdOp.INSERT);
            for (j = 1; j <= second.length(); ++j) {
                char c2 = second.charAt(j - 1);
                A[i % 2][j].clear();
                if (c1 == c2) {
                    A[i % 2][j].add(A[(i - 1) % 2][j - 1])
                            .inc(c1, EdOp.KEEP);
                } else {
                    int del = ldist(A[i % 2][j - 1]);
                    int sub = ldist(A[(i - 1) % 2][j - 1]);
                    int ins = ldist(A[(i - 1) % 2][j]);
                    int res = min(del, sub, ins);
                    if (res == del) {
                        A[i % 2][j].add(A[i % 2][j - 1])
                                .inc(c2, EdOp.DELETE);
                    } else if (res == sub) {
                        A[i % 2][j].add(A[(i - 1) % 2][j - 1])
                                .inc(c1, EdOp.SUBSTITUTE);
                    } else {
                        A[i % 2][j].add(A[(i - 1) % 2][j])
                                .inc(c1, EdOp.INSERT);
                    }
                }

            }
        }
        return A[first.length() % 2][second.length()];
    }

    /**
     * Aligns two strings (one to one alignments with substitutions).
     *
     * @param first the first string.
     * @param second the second string.
     * @return the mapping between positions.
     */
    public static int[] align(String first, String second) {
        int i, j;
        int[][] A;

        // intialize
        A = new int[first.length() + 1][second.length() + 1];

        // Compute first row
        A[0][0] = 0;
        for (j = 1; j <= second.length(); ++j) {
            A[0][j] = A[0][j - 1] + 1;
        }

        // Compute other rows
        for (i = 1; i <= first.length(); ++i) {
            A[i][0] = A[i - 1][0] + 1;
            for (j = 1; j <= second.length(); ++j) {
                if (first.charAt(i - 1) == second.charAt(j - 1)) {
                    A[i][j] = A[i - 1][j - 1];
                } else {
                    A[i][j] = min(A[i - 1][j] + 1, A[i][j - 1] + 1,
                            A[i - 1][j - 1] + 1);
                }
            }
        }

        int[] alignments = new int[first.length()];
        java.util.Arrays.fill(alignments, -1);

        i = first.length();
        j = second.length();
        while (i > 0 && j > 0) {
            if (first.charAt(i - 1) == second.charAt(j - 1)
                    || A[i][j] == A[i - 1][j - 1] + 1) {
                alignments[--i] = --j;
            } else if (A[i][j] == A[i - 1][j] + 1) {
                --i;
            } else if (A[i][j] == A[i][j - 1] + 1) {
                --j;
            } else { // remove after debugging
                Logger.getLogger(ErrorMeasure.class.getName())
                        .log(Level.SEVERE, null,
                                "Wrong code at StringEditDistance.alignments");
            }
        }

        return alignments;
    }
}
