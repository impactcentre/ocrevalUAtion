package eu.digitisation;

import eu.digitisation.io.TextBuilder;
import eu.digitisation.io.CharFilter;
import eu.digitisation.math.Counter;
import eu.digitisation.ocr.ErrorMeasure;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main class for ocrevalUAtion
 */
public class Main {

    static final String helpMsg = "Usage:\t"
            + "ocrevalUAtion -gt file1 [encoding] "
            + "-ocr file2 [encoding] "
            + "-o output [-f replacements_file]";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println(helpMsg);
        } else {
            File outfile = null;
            File gtfile = null;
            File ocrfile = null;
            File repfile = null;
            String outencoding = System.getProperty("file.encoding");
            String gtencoding = outencoding;
            String ocrencoding = outencoding;



            // Read parameters
            outencoding = System.getProperty("file.encoding");
            for (int n = 0; n < args.length; ++n) {
                String arg = args[n];
                switch (arg) {
                    case "-h":
                        System.err.println(helpMsg);
                        break;
                    case "-gt":
                        gtfile = new File(args[++n]);
                        if (!args[n].startsWith("-")) {
                            gtencoding = args[n++];
                        }
                        break;
                    case "-ocr":
                        ocrfile = new File(args[++n]);
                         if (!args[n].startsWith("-")) {
                            ocrencoding = args[n++];
                        }
                        break;
                    case "-o":
                        outfile = new File(args[++n]);
                        break;
                    case "-f":
                        repfile = new File(args[++n]);
                        break;
                    default:
                        System.err.println(helpMsg);
                        break;
                }
            }

            // input text 



            CharFilter filter =
                    (repfile == null) ? null : new CharFilter(repfile);
            TextBuilder builder = new TextBuilder(filter);

            try {
                StringBuilder gt = builder.trimmed(gtfile, gtencoding);
                StringBuilder ocr = builder.trimmed(ocrfile, ocrencoding);
                // Compute and print error rates
                double cer = ErrorMeasure.cer(gt.toString(), ocr.toString());
                double wer = ErrorMeasure.wer(gt.toString(), ocr.toString());      
                // Output
                System.out.println("Accuracy per char=" + (1 - cer) * 100);
                System.out.println("Accuracy per word=" + (1 - wer) * 100);
                try (PrintWriter writer = new PrintWriter(outfile)) {
                    writer.println("CER=" + cer * 100);
                    writer.println("WER=" + wer * 100);
                    writer.println("");
                    // Statistics per character
                    Counter<Character>[] stats =
                            ErrorMeasure.errors(gt.toString(), ocr.toString());
                    for (Character c : stats[0].keySet()) {
                        writer.println(c + ": " + 100 * stats[0].get(c));
                    }
                    writer.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
