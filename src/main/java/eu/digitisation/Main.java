package eu.digitisation;

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
            CharFilter filter =
                    (repfile == null) ? null : new CharFilter(repfile);
            TextContent gt = new TextContent(gtfile, gtencoding, filter);
            TextContent ocr = new TextContent(ocrfile, ocrencoding, filter);
            // Compute error rates
            double cer = ErrorMeasure.cer(gt.toString(), ocr.toString());
            double wer = ErrorMeasure.wer(gt.toString(), ocr.toString());
            // Output
            System.out.println("Accuracy per char=" + (1 - cer) * 100);
            System.out.println("Accuracy per word=" + (1 - wer) * 100);
            
            try (PrintWriter writer = new PrintWriter(outfile)) {
                writer.println("Character Error Rate = " + cer * 100 + "%");
                writer.println("Word Error Rate = " + wer * 100 + "%");
                writer.println("\n Error rate per character ant type");
                // Statistics per character
                writer.println("Character: Total; Spurious; Confused; Lost; Error rate");
                Counter<Character>[] stats = ErrorMeasure.errors(gt.toString(), ocr.toString());
                for (Character c : stats[0].keySet()) {
                    int tot = stats[0].value(c);
                    int spu = stats[1].value(c);
                    int sub =  stats[2].value(c);
                    int add = stats[3].value(c);
                    double rate = (spu + sub + add)/ (double)tot * 100;
                  
                    writer.println(c + "[" + Integer.toHexString(c) + "]"
                            + ": " + tot 
                            + "; " + spu
                            + "; " + sub
                            + "; " + add
                            + "; " +   String.format("%.2f", rate) 
                            );
                }
                writer.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
