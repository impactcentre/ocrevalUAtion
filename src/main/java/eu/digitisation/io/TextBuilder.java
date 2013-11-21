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

import eu.digitisation.ocr.ErrorMeasure;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Makes text (either as StringBuilder or as array of strings) from file content
 * and optionally applies a CharFilter
 *
 * @author R.C.C
 */
public class TextBuilder {

    static int maxlen;
    CharFilter filter;

    static {
        Properties prop = new Properties();
        try {
            FileReader reader = new FileReader("target/classes/General.properties");
            prop.load(reader);
            maxlen = Integer.parseInt(prop.getProperty("maxlen"));
        } catch (IOException ex) {
            Logger.getLogger(ErrorMeasure.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Basic constructor
     *
     * @param encoding the file encoding
     * @param filter the optional char filter
     */
    public TextBuilder(CharFilter filter) {
        this.filter = filter;
    }

    /**
     * Read a file as an array of lines
     */
    private static String[] toArray(File file, String encoding) {
        ArrayList<String> list = new ArrayList<String>();
        try {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis, encoding);
            BufferedReader reader = new BufferedReader(isr);
            int size = 0;

            while (reader.ready()) {
                String line = reader.readLine();
                size += line.length();
                if (size > maxlen) {
                    throw new RuntimeException("Input file length is limited to "
                            + maxlen + " characters");
                } else {
                    list.add(line);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ErrorMeasure.class.getName()).log(Level.SEVERE, null, ex);
        }

        return list.toArray(new String[list.size()]);
    }

    /**
     * Collapse whitespace: contiguous spaces are considered a single one
     *
     * @param file the input file
     * @param encoding the text encoding
     * @return String as StringBuilder
     * @throws IOException
     */
    public StringBuilder trimmed(File file, String encoding)
            throws IOException {
        StringBuilder builder = new StringBuilder();
        FileInputStream fis = new FileInputStream(file);
        InputStreamReader isr = new InputStreamReader(fis, encoding);
        BufferedReader reader = new BufferedReader(isr);
        int size = 0;

        while (reader.ready()) {
            String line = reader.readLine();
            String s = filter == null ? line : filter.translate(line);
            if (size > 0) {
                builder.append(' ');
            }
            size += line.length();
            if (size > maxlen) {
                throw new RuntimeException("On-line test limited to "
                        + maxlen + " characters");
            }

            builder.append(line.replaceAll("\\p{Space}+", " ").trim());
        }
        return builder;
    }
}
