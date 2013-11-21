package eu.digitisation;

import eu.digitisation.ocr.ErrorMeasure;
import java.io.*;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length != 5) {
            System.err.println("Usage: ocrevalUAtion filename1 encoding1 filename2 encoding2 output");
        } else {
            String encoding = System.getProperty("file.encoding");
            String inputText1 = args[0];
            String encoding1 = args[1];
            String inputText2 = args[2];
            String encoding2 = args[3];
            String output = args[4];
            //File replacements = new File("src/resources/replacements.txt");
            //FileFilter map = new FileFilter(replacements);
            PrintWriter writer;

            // Compute and print error rates
            File GTfile = new File(inputText1);
            File OCRfile = new File(inputText2);
            File outfile = new File(output);
            double cer = ErrorMeasure.cer(inputText1, encoding1, inputText2, encoding2);
            double wer = ErrorMeasure.wer(inputText1, encoding1, inputText2, encoding2);
            
//            map.translate(GTfile);            
            
            System.out.println("Accuracy per char=" + (1 - cer) * 100);
            System.out.println("Accuracy per word=" + (1 - wer) * 100);
            try {
                writer = new PrintWriter(outfile);
                writer.println("CER=" + cer * 100);
                writer.println("WER=" + wer * 100);
                writer.println("");
                // Statistics per character
                TreeMap<Character, Double> stats =
                        ErrorMeasure.stats(OCRfile, encoding1, GTfile, encoding2);
                for (Character c : stats.keySet()) {
                    writer.println(c + ": " + 100 * stats.get(c));
                }

                writer.close();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
