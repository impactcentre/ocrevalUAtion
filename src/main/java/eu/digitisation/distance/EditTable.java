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

/**
 * A compact structure storing the table of edit operations obtained during the
 * computation of the edit distance between two sequences a1a2...am and
 * b1b2...bn: each cell (i,j)in the table contains the last edit operation in
 * the optimal sequence of editions transforming prefix a1a2...ai into prefix
 * b1b2...bj. This table supports the retrieval of the full optimal edit
 * sequence (equivalent to a shortest path problem).
 *
 * @author R.C.C.
 */
public class EditTable {

    int width;  // table width
    int height;  // table height
    byte[] bytes;  // table content

    public EditTable(int width, int height) {
        this.width = width;
        this.height = height;
        bytes = new byte[1 + (width * height) / 4];  // two bits per operation 
    }

    /**
     * Get a specific bit in a byte
     *
     * @param b a byte
     * @param position the bit position
     * @return the byte with that bit set to the specified value
     */
    public static boolean getBit(byte b, int position) {
        return ((b >> position) & 1) == 1;
    }

    /**
     * Return a new byte with one bit set to a specific value
     *
     * @param b a byte
     * @param position the bit position
     * @param value the value for that bit
     * @return a new byte with that bit set to the specified value
     */
    public static byte setBit(byte b, int position, boolean value) {
        if (value) {
            return b |= 1 << position;
        } else {
            return b |= 0 << position;
        }
    }

    /**
     * Get the bit at that position in the byte array
     *
     * @param position a position in the array
     * @return the bit at that position in the byte array
     */
    private boolean getBit(int position) {
        return getBit(bytes[position / 8], position % 8);
    }

    /**
     * Set the bit at that position in the byte array to the specified value
     *
     * @param position a position in the array
     */
    private void setBit(int position, boolean value) {
        bytes[position / 8] = setBit(bytes[position / 8], position % 8, value);
    }

    /**
     *
     * @param i x-ccordinate
     * @param j y-coordinate
     * @return the edit operation stored at cell (i,j)
     */
    public EdOp get(int i, int j) {
        int position = 2 * (i * height + j);
        boolean low = getBit(position);
        boolean high = getBit(position + 1);
        if (low) {
            if (high) {
                return EdOp.SUBSTITUTE;
            } else {
                return EdOp.DELETE;
            }
        } else {
            if (high) {
                return EdOp.INSERT;
            } else {
                return EdOp.KEEP;
            }
        }
    }

    /**
     * Store an edit operation at cell (i, j)
     * @param i x-coordinate
     * @param j y-coordinate
     * @param op the edit operation to be stored
     */
    public void set(int i, int j, EdOp op) {
        int position = 2 * (i * height + j);
        boolean low;
        boolean high;

        switch (op) {
            case SUBSTITUTE:
                low = true;
                high = true;
                break;
            case DELETE:
                low = true;
                high = false;
                break;
            case INSERT:
                low = false;
                high = true;
                break;
            case KEEP:
                low = false;
                high = false;
                break;
            default:
                low = false;
                high = false;
        }
        setBit(position, low);
        setBit(position + 1, high);
    }

    /**
     * 
     * @return a string representation of the EditTable
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                EdOp e = get(i, j);
                switch (e) {
                    case KEEP:
                        builder.append('K');
                        break;
                    case SUBSTITUTE:
                        builder.append('S');
                        break;
                    case INSERT:
                        builder.append('I');
                        break;
                    case DELETE:
                        builder.append('D');
                        break;
                }
            }
            builder.append('\n');
        }

        return builder.toString();
    }
}
