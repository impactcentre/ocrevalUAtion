package eu.digitisation;

import eu.digitisation.distance.BagOfWords;
import eu.digitisation.io.CharFilter;
import eu.digitisation.io.TextContent;
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
            + "-o output [-r replacements_file]";

    private static void exit_gracefully() {
        System.err.println(helpMsg);
        System.exit(0);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        File outfile = null;
        File gtfile = null;
        File ocrfile = null;
        File repfile = null;
        String outencoding = System.getProperty("file.encoding");
        String gtencoding = outencoding;
        String ocrencoding = outencoding;

        // Read parameters (String switch needs Java 1.7 or later)
        for (int n = 0; n < args.length; ++n) {
            String arg = args[n];
            switch (arg) {
                case "-h":
                    exit_gracefully();
                case "-gt":
                    gtfile = new File(args[++n]);
                    if (!args[n + 1].startsWith("-")) {
                        gtencoding = args[++n];
                    }
                    break;
                case "-ocr":
                    ocrfile = new File(args[++n]);
                    if (!args[n + 1].startsWith("-")) {
                        ocrencoding = args[++n];
                    }
                    break;
                case "-o":
                    outfile = new File(args[++n]);
                    break;
                case "-r":
                    repfile = new File(args[++n]);
                    break;
                default:
                    System.err.println("Unrecognized option " + arg);
                    exit_gracefully();
            }
        }

        if (gtfile == null || ocrfile == null || outfile == null) {
            System.err.println("Not enough arguments");
            exit_gracefully();
        }

        try {
            // input text       
            CharFilter filter
                    = (repfile == null) ? null : new CharFilter(repfile);
            TextContent gt = new TextContent(gtfile, gtencoding, filter);
            TextContent ocr = new TextContent(ocrfile, ocrencoding, filter);
            // Compute error rates
            String gts = gt.toString();
            String ocrs = ocr.toString();
            double cer = ErrorMeasure.cer(gts, ocrs);
            double wer = ErrorMeasure.wer(gts, ocrs);
            double bwer = BagOfWords.wer(gts, ocrs);
            // Output
            System.out.println("CER=" + String.format("%.2f", cer * 100));
            System.out.println("WER=" + String.format("%.2f", wer * 100));
            System.out.println("WER (bag of words)=" + String.format("%.2f", bwer * 100));

            ErrorMeasure.stats2CSV(gts, ocrs, outfile, ';');
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
