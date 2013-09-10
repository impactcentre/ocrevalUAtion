/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.digitisation.ocr;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * Encode a file as an array of Integers.
 * Every word is encoded as an integer. 
 * Identical words have identical encodings and different words 
 * have different codes.
 * Consistency (between runs and files) is only guaranteed 
 * if the same FileEncoder is used.
 */
public class FileEncoder {

    HashMap<String, Integer> codes;

    
    public FileEncoder() {
        codes = new HashMap<String, Integer>();
    }
    /**
     * 
     * @param word a word
     * @return The integer code assigned to this word
     */
    public Integer getCode(String word) {
        Integer code;
        if (codes.containsKey(word)) {
            code = codes.get(word);
        } else {
            code = new Integer(codes.size());
            codes.put(word, code);
        }
        return code;
    }

    /**
     * Encode a file
     * @param file the input file
     * @return the file as an array of integer codes (one per word)
     */
    public Integer[] encode(File file, String encoding) {
        ArrayList<Integer> array = new ArrayList<Integer>();
        try {
            WordScanner scanner = new WordScanner(file, encoding);
            String word;

            while ((word = scanner.nextWord()) != null) {
                array.add(getCode(word));
                if (array.size() > 10000) {
                    System.err.println("Online tests limited to 10000 words");
                    System.exit(-1);
                }
            }
        } catch (IOException ex) {
            System.err.println(ex);
        }
        return array.toArray(new Integer[array.size()]);
    }
    
     /**
     * Encode a string
     * @param s the input string
     * @return the string as an array of integer codes (one per word)
     */
    public Integer[] encode(String s) {
        ArrayList<Integer> array = new ArrayList<Integer>();
        try {
            WordScanner scanner = new WordScanner(s);
            String word;

            while ((word = scanner.nextWord()) != null) {
                System.out.println("Encoding:'"+word+"'");
                array.add(getCode(word));
            }
        } catch (IOException ex) {
            System.err.println(ex);
        }
        return array.toArray(new Integer[array.size()]);
    }
    
}
