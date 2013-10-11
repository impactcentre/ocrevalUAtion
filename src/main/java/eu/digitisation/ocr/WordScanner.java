/**
 * Copyright (C) 2012 Rafael C. Carrasco (carrasco@ua.es)
 * This code can be distributed or modified
 * under the terms of the GNU General Public License V3.
 */
package eu.digitisation.ocr;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple and fast text scanner that reads words from a file.
 * @version 2012.06.20
 */
public class WordScanner {

    static Pattern pattern;
    static String defaultEncoding;
    Matcher matcher;
    BufferedReader reader;

    static {
        String reg1 = "(\\p{L}+([-\\x26'+/@_~·.]\\p{L}+)*)";
        String reg2 = "([\\p{Nd}\\p{Nl}\\p{No}]+([-',./][\\p{Nd}\\p{Nl}\\p{No}]+)*[%]?)";
        String regexp = "(" + reg1 + "|" + reg2 + ")";
        pattern = Pattern.compile(regexp);
        defaultEncoding = System.getProperty("file.encoding");
    }

    /**
     * Scan InputStream
     *
     * @param is input stream.
     * @param encoding the encoding (e.g., UTF-8).
     */
    private void open(InputStream is, String encoding)
            throws IOException {
        InputStreamReader isr = null;
        try {
            isr = new InputStreamReader(is, encoding);
        } catch (UnsupportedEncodingException x) {
            System.err.println("Unsuported encoding " + encoding);
        }
        reader = new BufferedReader(isr);
        if (reader.ready()) {
            matcher = pattern.matcher(reader.readLine());
        } else {
            matcher = pattern.matcher("");
        }
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
        open(is, encoding);
    }

    /**
     * Open a file for scanning.
     *
     * @param file the input file.
     */
    public WordScanner(File file)
            throws IOException {
        open(new FileInputStream(file), defaultEncoding);
    }

    /**
     * Open file with specific encoding for scanning.
     *
     * @param file the input file.
     * @param encoding the encoding (e.g., UTF-8).
     */
    public WordScanner(File file, String encoding)
            throws IOException {
        try {
            open(new FileInputStream(file), encoding);
        } catch (FileNotFoundException x) {
            System.err.println("Cannot open " + file);
        }
    }

    /**
     * Open a string for scanning
     * @param s the input string to be tokenized
     * @throws IOException 
     */
    public WordScanner(String s)
            throws IOException {
        InputStream is = null;
        String encoding = "UTF-8";
        try {
            is = new ByteArrayInputStream(s.getBytes(encoding));
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(WordScanner.class.getName()).log(Level.SEVERE, null, ex);
        }
        open(is, encoding);
    }

    /**
     *
     * @param file the input file to be processed
     * @return
     */
    public static StringBuilder scanToBuffer(File file)
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
            } catch (IOException x) {
                System.err.println(x);
            }
        }
    }
}