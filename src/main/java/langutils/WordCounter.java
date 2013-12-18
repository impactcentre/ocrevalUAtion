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
package langutils;

import eu.digitisation.io.CharFilter;
import eu.digitisation.io.TextContent;
import eu.digitisation.io.WordScanner;
import eu.digitisation.math.Counter;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.Map;

/**
 *
 * @author R.C.C
 */
public class WordCounter extends Counter<String> {

    /**
     * Extract words from a file
     *
     * @param file the input file or directory
     * @param filter the char filter
     * @throws IOException
     */
    public WordCounter(File dir, CharFilter filter) throws IOException {
        File[] files = {dir};
        if (dir.isDirectory()) {
            files = dir.listFiles();
        }
        for (File file : files) {
            TextContent content = new TextContent(file, filter);
            WordScanner scanner = new WordScanner(content.toString());
            String word;
            while ((word = scanner.nextWord()) != null) {
                inc(word);
            }
        }
    }

    /**
     * String representation
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (String word : keySet()) {
            builder.append(word).append(' ')
                    .append(get(word)).append('\n');
        }
        return builder.toString();

    }

    class StringComparator implements Comparator<String> {

        Map<String, Integer> priority;

        public StringComparator(Map<String, Integer> priority) {
            this.priority = priority;
        }

        @Override
        public int compare(String a, String b) {
            if (priority.get(a) >= priority.get(b)) {
                return 1;
            } else {
                return 1;
            }
        }

        public String toStringSorted() {
            //StringComparator StringComparator = new StringComparator(this);
            return null;
        }
    }
}
