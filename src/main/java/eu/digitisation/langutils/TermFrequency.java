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
package eu.digitisation.langutils;

import eu.digitisation.io.CharFilter;
import eu.digitisation.io.StringNormalizer;
import eu.digitisation.io.TextContent;
import eu.digitisation.io.WordScanner;
import eu.digitisation.math.Counter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Compute term frequencies in a collection
 *
 * @author R.C.C
 */
public class TermFrequency extends Counter<String> {

    private static final long serialVersionUID = 1L;
    CharFilter filter;

    public TermFrequency() {
        filter = null;
    }

    /**
     * Basic constructor
     *
     * @param filter a CharFilter implementing character equivalences
     */
    public TermFrequency(CharFilter filter) {
        this.filter = filter;
    }

    /* Select CharFilter
     * @param filter a CharFilter implementing character equivalences
     */
    public void addFilter(File file) {
        if (filter == null) {
            filter = new CharFilter(file);
        } else {
            filter.addFilter(file);
        }
    }

    /**
     * Extract words from a file
     *
     * @param dir the input file or directory
     */
    public void add(File dir) {
        if (dir.isDirectory()) {
            addFiles(dir.listFiles());
        } else {
            File[] files = {dir};
            addFiles(files);
        }
    }

    /**
     * Extract words from a file
     *
     * @param file an input files
     */
    public void addFile(File file) {
        try {
            TextContent content = new TextContent(file, filter);
            WordScanner scanner = new WordScanner(content.toString());
            String word;
            while ((word = scanner.nextWord()) != null) {
                String filtered = (filter == null)
                        ? word : filter.translate(word);
                inc(StringNormalizer.canonical(filtered));
            }
        } catch (IOException ex) {
            Logger.getLogger(TermFrequency.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Extract words from a file
     *
     * @param files an array of input files
     */
    private void addFiles(File[] files) {
        for (File file : files) {
            addFile(file);
        }
    }

    /**
     * String representation
     *
     * @param order the criteria to sort words
     * @return String representation
     */
    public String toString(Order order) {
        StringBuilder builder = new StringBuilder();
        for (String word : this.keyList(order)) {
            builder.append(word).append(' ')
                    .append(get(word)).append('\n');
        }
        return builder.toString();
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: WordCounter [-e equivalences_file] [-c] input_files_or_directories");
        } else {
            TermFrequency tf = new TermFrequency();
            List<File> files = new ArrayList<File>();
            CharFilter filter = new CharFilter();
            for (int n = 0; n < args.length; ++n) {
                if (args[n].equals("-e")) {
                    tf.addFilter(new File(args[++n]));
                } else if (args[n].equals("-c")) {
                    filter.setCompatibility(true);
                } else {
                    files.add(new File(args[n]));
                }
            }
            for (File file : files) {
                tf.add(file);
            }
            System.out.println(tf.toString(Order.DESCENDING_VALUE));
        }
    }
}
