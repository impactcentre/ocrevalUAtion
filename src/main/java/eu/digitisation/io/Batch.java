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
package eu.digitisation.io;

import eu.digitisation.math.Pair;
import java.io.File;
import java.io.InvalidObjectException;

/**
 * A batch of files to be processed
 *
 * @author R.C.C.
 */
public class Batch {

    int size;
    File[] files1;
    File[] files2;

    public Batch(File dir1, File dir2) throws InvalidObjectException {
        if (dir1.isDirectory()) {
            files1 = dir1.listFiles();
        } else {
            throw new java.io.InvalidObjectException(dir1 + " is not a folder");
        }
        if (dir2.isDirectory()) {
            files2 = dir2.listFiles();
        } else {
            throw new java.io.InvalidObjectException(dir2 + " is not a folder");
        }
        if (files1.length != files2.length) {
            throw new java.io.InvalidObjectException(dir1 + " and " + dir2
                    + " contain a different number of files");
        } else {
            size = files1.length;
        }
        if (!consistent()) {
            throw new java.io.InvalidObjectException(dir1 + " and " + dir2
                    + " contain files with inconsistent names");
        }
    }

    private boolean consistent() {
        if (size > 1) {
            int low1 = lcp(files1).length();
            int low2 = lcp(files2).length();
            int high1 = lcs(files1).length();
            int high2 = lcs(files2).length();
            for (int n = 0; n < size; ++n) {
                String name1 = files1[n].getName();
                String name2 = files2[n].getName();
                String id1 = name1.substring(low1, name1.length() - high1);
                String id2 = name2.substring(low2, name2.length() - high2);
                if (!id1.equals(id2)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     *
     * @return he number of file to be processed
     */
    public int size() {
        return size;
    }

    /**
     *
     * @param n the file number
     * @return the next File pair
     */
    public Pair<File, File> pair(int n) {
        return new Pair<File, File>(files1[n], files2[n]);
    }

    /**
     * Common prefix
     *
     * @param s1 a string
     * @param s2 another string
     * @return the common prefix of s1 and s2
     */
    static String prefix(String s1, String s2) {
        int limit = Math.min(s1.length(), s2.length());
        int len = 0;

        while (len < limit && s1.charAt(len) == s2.charAt(len)) {
            ++len;
        }
        return s1.substring(0, len);
    }

    /**
     * Longest common prefix
     *
     * @param words an array of files
     * @return the longest common prefix to all filenames
     */
    private String lcp(File[] files) {
        if (files.length > 0) {
            String result = files[0].getName();
            for (int n = 1; n < files.length; ++n) {
                result = prefix(result, files[n].getName());
            }
            return result;
        } else {
            return null;
        }
    }

    /**
     * Common suffix
     *
     * @param s1 one word
     * @param s2 another word
     * @return the common suffix to s1 and s2
     */
    static String suffix(String s1, String s2) {
        int limit = Math.min(s1.length(), s2.length());
        int len = 0;

        while (len < limit
                && s1.charAt(s1.length() - len - 1) == s2.charAt(s2.length() - len - 1)) {
            ++len;
        }
        return s1.substring(s1.length() - len);
    }

    /**
     * Longest common suffix
     *
     * @param files an array of files
     * @return the longest common suffix to all files
     */
    public static String lcs(File[] files) {
        if (files.length > 0) {
            String result = files[0].getName();
            for (int n = 1; n < files.length; ++n) {
                result = suffix(result, files[n].getName());
            }
            return result;
        } else {
            return null;
        }
    }

    public static void main(String[] args) throws InvalidObjectException {
        Batch batch = new Batch(new File(args[0]), new File(args[1]));
    }
}
