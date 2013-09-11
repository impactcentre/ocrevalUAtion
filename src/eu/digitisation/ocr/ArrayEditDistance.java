/**
 * Copyright (C) 2012 Rafael C. Carrasco 
 * This code can be distributed or
 * modified under the terms of the GNU General Public License V3.
 */
package eu.digitisation.ocr;

/**
 * Provides a basic implementations of some popular edit distance methods 
 * generalized to arrays
 *
 * @version 2011.03.10
 */
public class ArrayEditDistance <Type> {
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
        public static <Type> int indelDistance(Type[] first, Type[] second) {
            int i, j;
            int[][] A = new int[first.length + 1][second.length + 1];

            // Compute first row
            A[0][0] = 0;
            for (j = 1; j <= second.length; ++j) {
                A[0][j] = A[0][j - 1] + 1;
            }

            // Compute other rows
            for (i = 1; i <= first.length; ++i) {
                A[i][0] = A[i - 1][0] + 1;
                for (j = 1; j <= second.length; ++j) {
                    if (first[i - 1].equals(second[j - 1])) {
                        A[i][j] = A[i - 1][j - 1];
                    } else {
                        A[i][j] = Math.min(A[i - 1][j] + 1, A[i][j - 1] + 1);
                    }
                }
            }
            return A[first.length][second.length];
        }

        /**
         * @param first the first string.
         * @param second the second string.
         * @return the Levenshtein distance between first and second.
         */
        public static <Type> int levenshteinDistance(Type[] first, Type[] second) {
            int i, j;
            int[][] A;

            // intialize
            A = new int[first.length + 1][second.length + 1];

            // Compute first row
            A[0][0] = 0;
            for (j = 1; j <= second.length; ++j) {
                A[0][j] = A[0][j - 1] + 1;
            }

            // Compute other rows
            for (i = 1; i <= first.length; ++i) {
                A[i][0] = A[i - 1][0] + 1;
                for (j = 1; j <= second.length; ++j) {
                    if (first[i - 1] == second[j - 1]) {
                        A[i][j] = A[i - 1][j - 1];
                    } else {
                        A[i][j] = min(A[i - 1][j] + 1, A[i][j - 1] + 1,
                                A[i - 1][j - 1] + 1);
                    }
                }
            }
            return A[first.length][second.length];
        }
    }
