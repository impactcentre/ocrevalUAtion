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
package eu.digitisation.text;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple and fast text scanner which reads words from a file and performs the
 * tokenization oriented by information-retrieval requirements.
 *
 * @version 2012.06.20
 */
public class WordScanner {

    static Pattern pattern;
    Matcher matcher;
    BufferedReader reader;

    static {
        StringBuilder builder = new StringBuilder();
        builder.append("(");
        builder.append("(\\p{L}+([-\\x26'+/@_~·.]\\p{L}+)*)");
        builder.append("|");
        builder.append("([\\p{Nd}\\p{Nl}\\p{No}]+([-',./][\\p{Nd}\\p{Nl}\\p{No}]+)*[%]?)");
        builder.append(")");
        pattern = Pattern.compile(builder.toString());
    }

    /**
     * Open an InputStream for scanning
     *
     * @param is the InputStream
     * @param encoding the character encoding (e.g., UTF-8).
     * @throws IOException
     */
    public WordScanner(InputStream is, String encoding)
            throws IOException {
        InputStreamReader isr = new InputStreamReader(is, encoding);

        reader = new BufferedReader(isr);
        if (reader.ready()) {
            matcher = pattern.matcher(reader.readLine());
        } else {
            matcher = pattern.matcher("");
        }
    }

    /**
     * Open file with specific encoding for scanning.
     *
     * @param file the input file.
     * @param encoding the encoding (e.g., UTF-8).
     * @throws java.io.IOException
     */
    public WordScanner(File file, String encoding)
            throws IOException {
        this(new FileInputStream(file), encoding);
    }

    /**
     * Open a file for scanning.
     *
     * @param file the input file.
     * @throws java.io.IOException
     */
    public WordScanner(File file)
            throws IOException {
        this(file, Encoding.detect(file));
    }

    /**
     * Open a string for scanning
     *
     * @param s the input string to be tokenized
     * @throws IOException
     */
    public WordScanner(String s)
            throws IOException {
        this(new ByteArrayInputStream(s.getBytes("UTF-8")), "UTF-8");
    }

    /**
     *
     * @param file the input file to be processed
     * @return a StringBuilder with the file content
     * @throws java.io.IOException
     */
    public static StringBuilder scanToStringBuilder(File file)
            throws IOException {
        StringBuilder builder = new StringBuilder();
        WordScanner scanner = new WordScanner(file);
        String word;

        while ((word = scanner.nextWord()) != null) {
            builder.append(' ').append(word);
        }
        return builder;
    }

    /**
     * Returns the next word in file.
     *
     * @return the next word in the scanned file
     * @throws java.io.IOException
     */
    public String nextWord()
            throws IOException {
        String res = null;
        while (res == null) {
            if (matcher.find()) {
                res = matcher.group(1);
            } else if (reader.ready()) {
                matcher = pattern.matcher(reader.readLine());
            } else {
                break;
            }
        }
        return res;
    }

    /**
     * Sample main.
     * @param args
     */
    public static void main(String[] args) {
        WordScanner scanner;

        for (String arg : args) {
            try {
                String word;
                File file = new File(arg);
                scanner = new WordScanner(file);
                while ((word = scanner.nextWord()) != null) {
                    System.out.println(word);
                }
            } catch (IOException ex) {
                System.err.println(ex);
            }
        }
    }
}
