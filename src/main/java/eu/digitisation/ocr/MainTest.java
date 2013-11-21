package eu.digitisation.ocr;

import eu.digitisation.Page.PAGE;
import eu.digitisation.Unicode.FileFilter;
import eu.digitisation.util.MiniBrowser;
import tmp.XML2text;
import java.io.*;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rafa
 */
public class MainTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: OCR filename");
        } else {
            String encoding = System.getProperty("file.encoding");
            String indir = "input/";
            String outdir = "output/";
            String resdir = "resources/";
            String name = args[0];
            String inputPNG = indir + name + ".png";
            String inputPAGE = indir + name + ".xml";
            String inputRepl = resdir + "replacements.txt";
            String flatXML = outdir + name + ".xml";
            String outputOCR = outdir + name + "_ocr.txt";
            String outputGT = outdir + name + "_gt.txt";
            String output = outdir + name + ".out";
            PrintWriter writer;

            // Create OCR output
            try {
                Runtime.getRuntime().exec("/usr/bin/tesseract "
                        + inputPNG + " " + outputOCR
                        + " -l spa_old &");
            } catch (IOException ex) {
                ex.printStackTrace();
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }

            // Create GT text file 
            File PAGEfile = new File(inputPAGE);
            File flatXMLfile = new File(flatXML);
            File GTfile = new File(outputGT);
            File repFile = new File("resources/replacements.txt");
            FileFilter map = new FileFilter(repFile);
            XML2text xml = new XML2text();

            PAGE.transform(PAGEfile, flatXMLfile);
            String text = xml.getText(flatXML);
            //System.out.println(text);
            try {
                writer = new PrintWriter(outputGT);
                writer.write(xml.getText(flatXML));
                writer.flush();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
            map.translate(GTfile);


            // Compute and print error rates
            File OCRfile = new File(outputOCR);
            File outfile = new File(output);
            double cer = ErrorMeasure.cer(GTfile, encoding, OCRfile, encoding);
            double wer = ErrorMeasure.wer(GTfile, encoding, OCRfile, encoding);
            System.out.println("Accuracy per char=" + (1 - cer) * 100);
            System.out.println("Accuracy per word=" + (1 - wer) * 100);
            try {
                writer = new PrintWriter(outfile);
                writer.println("CER=" + cer * 100);
                writer.println("WER=" + wer * 100);
                // Statistics per character
                TreeMap<Character, Double> stats =
                        ErrorMeasure.stats(GTfile, encoding, OCRfile, encoding);
                for (Character c : stats.keySet()) {
                    writer.println(c + ": " + 100 * stats.get(c));
                }

                writer.close();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }

            //Visualization
            String cmd =
                    "wdiff -w '<span title=\"' -x '\">' -y '<font color=\"red\">'"
                    + " -z '</font>' output/lazarillo_gt.txt"
                    + " output/lazarillo_ocr.txt > output/comparison.html";
            MiniBrowser browser = new MiniBrowser();
            try {
                Runtime.getRuntime().exec(cmd);
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
            browser.addPage("file:./output/comparison.html");
            browser.setVisible(true);



            /*
             if (args.length != 5) {
             System.err.println("Usage: Measure filename1 encoding1 filename2 encoding2 outputfile");
             System.err.println(java.util.Arrays.toString(args));
             } else {       
             double cer = Measure.cer(args[0], args[1], args[2], args[3]);
             double wer = Measure.wer(args[0], args[1], args[2], args[3]);
             File file = new File(args[4]);
             try {
             PrintWriter writer = new PrintWriter(file);
             writer.print("CER=" + cer * 100);
             writer.print("WER=" + wer * 100);
             writer.close();
             } catch (FileNotFoundException ex) {
             Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
             }
                
             System.err.println("CER=" + cer * 100); 
             System.err.println("WER=" + wer * 100);
             }
             */
        }
    }
}
