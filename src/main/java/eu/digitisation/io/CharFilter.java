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

import java.io.*;
import java.util.HashMap;
import java.nio.channels.FileChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Transform text according to a mapping between (source, target) Unicode
 * character sequences. This can be useful, for example, to replace Unicode
 * characters which are not supported by the browser or editor with printable
 * ones.
 *
 * @version 2012.06.20
 */
public class CharFilter extends HashMap<String, String> {
    private static final long serialVersionUID = 1L;

    /**
     * Load the transformation map from a CVS file (one transformation per
     * line): each line contains two Unicode hex values (plus comments)
     * separated with whitespace or separators
     *
     * @param file the file with the equivalent sequences
     */
    public CharFilter(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            while (reader.ready()) {
                String line = reader.readLine();
                String[] tokens = line.split("(\\p{Space}|[,;])+");
                if (tokens.length > 1) {  // allow comments in line
                    String left = UnicodeReader.codepointsToString(tokens[0]);
                    String right = UnicodeReader.codepointsToString(tokens[1]);
                    put(left, right);
                } else {
                    throw new IOException("Wrong line" + line
                            + " at file " + file);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(CharFilter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Find all occurrences of characters in a sequence and substitute them with
     * the replacement specified by the transformation map. Remark: No
     * replacement priority is guaranteed in case of overlapping matches.
     *
     * @param s the string to be transformed
     * @return a new string with all the transformations performed
     */
    public String translate(String s) {
        for (String pattern : keySet()) {
            s = s.replaceAll(pattern, get(pattern));
        }
        return s;
    }

    /**
     * Converts the contents of a file into a CharSequence
     */
    private CharSequence toCharSequence(File file) {

        try (FileInputStream input = new FileInputStream(file)) {
            FileChannel channel = input.getChannel();
            java.nio.ByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
            return java.nio.charset.Charset.forName("utf-8").newDecoder()
                    .decode(buffer);
        } catch (IOException ex) {
            Logger.getLogger(CharFilter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Translate all characters according to the transformation map
     *
     * @param infile the input file
     * @param outfile the file where the output must be written
     */
    public void translate(File infile, File outfile) {
        try (FileWriter writer = new FileWriter(outfile)) {
            String input = toCharSequence(infile).toString();
            String output = translate(input);

            writer.write(output);
            writer.flush();
        } catch (IOException ex) {
            Logger.getLogger(CharFilter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Translate (in place) all characters according to the transformation map
     *
     * @param file the input file
     *
     */
    public void translate(File file) {
        try (FileWriter writer = new FileWriter(file)) {
            String input = toCharSequence(file).toString();
            String output = translate(input);

            writer.write(output);
            writer.flush();
        } catch (IOException ex) {
            Logger.getLogger(CharFilter.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
