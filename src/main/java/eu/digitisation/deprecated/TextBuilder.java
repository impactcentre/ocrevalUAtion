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
package eu.digitisation.deprecated;

import eu.digitisation.io.CharFilter;
import eu.digitisation.ocr.ErrorMeasure;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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

    static {
        Properties prop = new Properties();
        try (InputStream in = TextBuilder.class.getResourceAsStream("/General.properties")) {
            prop.load(in);
        } catch (IOException ex) {
            Logger.getLogger(TextBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
        maxlen = Integer.parseInt(prop.getProperty("maxlen"));
    }

    /**
     * Get extension of name
     *
     * @param filename the name of a file
     * @return extension part of the filename (after last dot)
     */
    private static String getExtension(File file) {
        String filename = file.getName();
        int pos = filename.lastIndexOf('.');
        return filename.substring(pos + 1);
    }

    /**
     * Read a file as an array of lines
     *
     * @param file the input text file
     * @param encoding the text file encoding
     * @param filter optional CharFilter (null if none applied)
     * @return the file content as an array of lines
     */
    public static String[] toArray(File file, String encoding, CharFilter filter) {
        ArrayList<String> list = new ArrayList<String>();
        try {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis, encoding);
            BufferedReader reader = new BufferedReader(isr);
            int size = 0;

            while (reader.ready()) {
                String line = (filter == null) ? reader.readLine()
                        : filter.translate(reader.readLine());
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
     * Read a file as an array of lines
     *
     * @param file the input text file
     * @param encoding the text file encoding
     * @return the file content as an array of lines
     */
    public static String[] toArray(File file, String encoding) {
        return toArray(file, encoding, null);
    }

    /**
     * Return textual content and collapse whitespace: contiguous spaces are
     * considered a single one
     *
     * @param file the input text file
     * @param encoding the text file encoding
     * @param filter optional CharFilter
     * @return String as StringBuilder
     * @throws IOException
     */
    public static StringBuilder trimmed(File file, String encoding, CharFilter filter)
            throws IOException {
        StringBuilder builder = new StringBuilder();
        FileInputStream fis = new FileInputStream(file);
        InputStreamReader isr = new InputStreamReader(fis, encoding);
        BufferedReader reader = new BufferedReader(isr);
        int size = 0;

        while (reader.ready()) {
            String line = (filter == null) ? reader.readLine()
                    : filter.translate(reader.readLine());
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
    
      /**
     * Return textual content and collapse whitespace: contiguous spaces are
     * considered a single one
     *
     * @param file the input text file
     * @param encoding the text file encoding
     * @return String as StringBuilder
     * @throws IOException
     */
    public static StringBuilder trimmed(File file, String encoding)
            throws IOException {
        return trimmed(file, encoding, null);
    }
}
