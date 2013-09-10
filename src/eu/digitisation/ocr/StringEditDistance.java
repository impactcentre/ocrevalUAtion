package eu.digitisation.ocr;

/**
 * Provides basic implementations of some popular edit distance methods.
 *
 * @author R.C.C.
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
    public static int indelDistance(String first, String second) {
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
                    A[i % 2][j] = Math.min(A[(i - 1) % 2][j] + 1, A[i % 2][j - 1] + 1);
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
    public static int levenshteinDistance(String first, String second) {
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
                    A[i % 2][j] = min(A[(i - 1) % 2][j] + 1, A[i % 2][j - 1] + 1,
                            A[(i - 1) % 2][j - 1] + 1);
                }
            }
        }
        return A[first.length() % 2][second.length()];
    }

    /**
     * Aligns two strings (one to one alignments).
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
            if (A[i][j] == A[i - 1][j - 1]) {
                alignments[--i] = --j;
            } else if (A[i][j] == A[i - 1][j] + 1) {
                --i;
            } else if (A[i][j] == A[i][j - 1] + 1) {
                --j;
            } else if (A[i][j] == A[i - 1][j - 1] + 1) {
                --i; 
                --j;
            } else { // Must raise exception
                System.err.println("Wrong code at StringEditDistance.alignments");
            }
        }
        
        return alignments;
    }
}