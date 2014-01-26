/*
 * Copyright (C) 2014 IMPACT Centre of Competence
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

import eu.digitisation.math.BiCounter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An arbitrary length sequence of basic edit operations (keep, insert,
 * substitute, delete)
 *
 * @author R.C.C.
 */
public class EditSequence {

    List<EdOp> ops;  // the list of edit operations
    int numOps; // the number of non-trivial (KEEP) operations 
    int shift1; // the number of operations involving the first string (all but INSERT)
    int shift2; // the number of operations involving the second string (all but DELETE)

    /**
     * Constructs an empty list with an initial capacity of ten.
     */
    public EditSequence() {
        ops = new ArrayList<EdOp>();
    }

    /**
     * Create an EditPAth with the specified initial capacity
     *
     * @param initialCapacity
     */
    public EditSequence(int initialCapacity) {
        ops = new ArrayList<EdOp>(initialCapacity);
    }

    /**
     *
     * @param pos a position in the sequence
     * @return the basic edit operation in the sequence which is at the
     * specified position
     */
    public EdOp get(int pos) {
        return ops.get(pos);
    }

    /**
     * Add an operation to the sequence
     *
     * @param op an edit operation
     */
    public void add(EdOp op) {
        ops.add(op);
        switch (op) {
            case KEEP:
                ++shift1;
                ++shift2;
                break;
            case INSERT:
                ++shift2;
                ++numOps;
                break;
            case SUBSTITUTE:
                ++shift1;
                ++shift2;
                ++numOps;
                break;
            case DELETE:
                ++shift1;
                ++numOps;
                break;
        }
    }

    /**
     * Add an operation to the sequence
     *
     * @param other another sequence of edit operations
     */
    public void append(EditSequence other) {
        for (EdOp op : other.ops) {
            this.add(op);
        }
    }

    /**
     * Build a new path containing only a prefix of the sequence
     *
     * @param len the length of the new sequence
     * @return the path truncated to the required length
     */
    public EditSequence head(int len) {
        EditSequence path = new EditSequence();
        for (int n = 0; n < len; ++n) {
            path.add(ops.get(n));
        }
        return path;
    }

    /**
     * The size of the list
     *
     * @return the number of basic edit operations in the sequence
     */
    public int size() {
        return ops.size();
    }

    /**
     *
     * @return the number of non-trivial (KEEP) edit operations in this sequence
     */
    public int cost() {
        return numOps;
    }

    /**
     * The shift in the first string
     *
     * @return the number of edit operations in the sequence involving the first
     * string (all but DELETE)
     */
    public int shift1() {
        return shift1;
    }

    /**
     * The shift in the second string
     *
     * @return the number of edit operations in the sequence involving the
     * second string (all but INSERT)
     */
    public int shift2() {
        return shift2;
    }

    /**
     * String representation
     *
     * @return a string representing the EditSequence
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (EdOp op : ops) {
            builder.append(op);
        }
        return builder.toString();
    }

    /**
     * Build the EditTable for a pair of strings
     *
     * @param first the first string
     * @param second the second string
     */
    public EditSequence(String first, String second) {
        int l1;      // length of first 
        int l2;      // length of second
        int[][] A;   // distance table
        EditTable B; // edit operations

        // intialize 
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
            char c1 = first.charAt(i - 1);
            A[i % 2][0] = A[(i - 1) % 2][0] + 1;
            B.set(i, 0, EdOp.DELETE);
            for (int j = 1; j <= second.length(); ++j) {
                char c2 = second.charAt(j - 1);

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
                    A[i % 2][j] = Math.min(A[(i - 1) % 2][j] + 1,
                            Math.min(A[i % 2][j - 1] + 1,
                                    A[(i - 1) % 2][j - 1] + 1));
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

        // extract sequence of edit operations
        int i = B.width - 1;
        int j = B.height - 1;

        ops = new ArrayList<EdOp>();

        while (i > 0 || j > 0) {
            EdOp e = B.get(i, j);
            switch (e) {
                case INSERT:
                    --j;
                    break;
                case DELETE:
                    --i;
                    break;
                default:
                    --i;
                    --j;
                    break;
            }
            add(e);
        }
        if (i != 0 || j != 0) {
            throw new java.lang.IllegalArgumentException("Unvalid EditTable");
        } else {
            Collections.reverse(ops);
        }
    }

    /**
     * Build the EditTable for a pair of TokenArrays
     *
     * @param first the first TokenArray
     * @param second the second TokenArray
     */
    public EditSequence(TokenArray first, TokenArray second) {
        int l1;      // length of first 
        int l2;      // length of second
        int[][] A;   // distance table
        EditTable B; // edit operations

        // intialize 
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
            int n1 = first.tokenAt(i - 1);
            A[i % 2][0] = A[(i - 1) % 2][0] + 1;
            B.set(i, 0, EdOp.DELETE);
            for (int j = 1; j <= second.length(); ++j) {
                int n2 = second.tokenAt(j - 1);
                if (n1 == n2) {
                    A[i % 2][j] = A[(i - 1) % 2][j - 1];
                    B.set(i, j, EdOp.KEEP);
                } else {
                    A[i % 2][j] = Math.min(A[(i - 1) % 2][j] + 1,
                            Math.min(A[i % 2][j - 1] + 1,
                                    A[(i - 1) % 2][j - 1] + 1));
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

        // extract sequence of edit operations
        int i = B.width - 1;
        int j = B.height - 1;

        ops = new ArrayList<EdOp>();

        while (i > 0 || j > 0) {
            EdOp e = B.get(i, j);
            switch (e) {
                case INSERT:
                    --j;
                    break;
                case DELETE:
                    --i;
                    break;
                default:
                    --i;
                    --j;
                    break;
            }
            add(e);
        }
        if (i != 0 || j != 0) {
            throw new java.lang.IllegalArgumentException("Unvalid EditTable");
        } else {
            Collections.reverse(ops);
        }
    }

    /**
     * Extract alignment statistics
     *
     * @param s1 the source string
     * @param s2 the target string
     * @return the statistics on the number of edit operations (per character
     * and type of operation)
     */
    public BiCounter<Character, EdOp> stats(String s1, String s2) {
        BiCounter<Character, EdOp> stats = new BiCounter<Character, EdOp>();
        int n1 = 0;
        int n2 = 0;
        for (EdOp op : ops) {
            Character c1 = s1.charAt(n1);
            Character c2 = s2.charAt(n2);
            stats.inc(c1, op);
            if (op != EdOp.INSERT) {
                ++n1;
            }
            if (op != EdOp.DELETE) {
                ++n2;
            }
        }
        return stats;
    }

}
