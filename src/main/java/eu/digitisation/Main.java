package eu.digitisation;

import eu.digitisation.distance.BagOfWords;
import eu.digitisation.distance.Aligner;
import eu.digitisation.io.CharFilter;
import eu.digitisation.io.TextContent;
import eu.digitisation.ocr.ErrorMeasure;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main class for ocrevalUAtion: version 0.92
 */
public class Main {

    static final String helpMsg = "Usage:\t"
            + "ocrevalUAtion -gt file1 [encoding] "
            + "-ocr file2 [encoding] "
            + "-d output_directory [-r replacements_file]";

    private static void exit_gracefully() {
        System.err.println(helpMsg);
        System.exit(0);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        File repfile = null;     // char filter
        File gtfile = null;      // ground-truth))
        File ocrfile = null;     // ocr-output
        String gtencoding = null;
        String ocrencoding = null;
        File workingDirectory = null; // output directory 

        // Read parameters (String switch needs Java 1.7 or later)
        for (int n = 0; n < args.length; ++n) {
            String arg = args[n];
            switch (arg) {
                case "-h":
                    exit_gracefully();
                    break;  // avoids warning
                case "-gt":
                    gtfile = new File(args[++n]);
                    if (n + 1 < args.length && !args[n + 1].startsWith("-")) {
                        gtencoding = args[++n];
                    }
                    break;
                case "-ocr":
                    ocrfile = new File(args[++n]);
                    if (n + 1 < args.length && !args[n + 1].startsWith("-")) {
                        ocrencoding = args[++n];
                    }
                    break;
                case "-d":
                    workingDirectory = new File(args[++n]);
                    break;
                case "-r":
                    repfile = new File(args[++n]);
                    break;
                default:
                    System.err.println("Unrecognized option " + arg);
                    exit_gracefully();
            }
        }

        if (gtfile == null || ocrfile == null) {
            System.err.println("Not enough arguments");
            exit_gracefully();
        } else if (workingDirectory == null) {
            String dir = ocrfile.getAbsolutePath()
                    .replaceAll(File.separator + "(\\.|\\w)+$", "");
            workingDirectory = new File(dir);
        }

        System.out.println("Working directory set to " + workingDirectory);

        if (workingDirectory == null
                || !workingDirectory.isDirectory()) {
            System.out.println(workingDirectory + " is not a valid directory");
        } else {
            String prefix;
            prefix = workingDirectory + File.separator
                    + gtfile.getName().replaceFirst("[.][^.]+$", "");

            try (PrintWriter writer = new PrintWriter(prefix + "_out.html")) {

                // input text       
                CharFilter filter = (repfile == null) ? null : new CharFilter(repfile);
                TextContent gt = new TextContent(gtfile, gtencoding, filter);
                TextContent ocr = new TextContent(ocrfile, ocrencoding, filter);
                // Compute error rates
                String gts = gt.toString();
                String ocrs = ocr.toString();
                double cer = ErrorMeasure.cer(gts, ocrs);
                double cerDL = ErrorMeasure.cerDL(gts, ocrs);
                double wer = ErrorMeasure.wer(gts, ocrs);
                double bwer = BagOfWords.wer(gts, ocrs);

                // Output 
                writer.println("<html>");
                writer.println(" <head>");
                writer.println("  <meta http-equiv=\"content-type\""
                        + "content=\"text/html; charset=UTF-8\">");
                writer.println(" </head>");
                writer.println(" <body>");
                writer.println("   <h2>General results</h2>");
                writer.println("  <ul>");
                writer.append("<li>CER=")
                        .append(String.format("%.2f", cer * 100))
                        .append("%</li>");
                writer.append("<li>CER(DL)=")
                        .append(String.format("%.2f", cerDL * 100))
                        .append("%</li>");
                writer.append("<li>WER=")
                        .append(String.format("%.2f", wer * 100))
                        .append("%</li>");
                writer.append("<li>WER (bag of words)=")
                        .append(String.format("%.2f", bwer * 100)).
                        append("%</li>");
                writer.println("  </ul>");
                // Detailed statistics
                writer.println(" <h2>Error rate per character ant type</h2>");
                writer.println("<table border=\"1\" align=\"left\">\n<tl><td>");
                writer.append(ErrorMeasure.stats(gts, ocrs,
                        "</td></tr>\n<tr><td align=\"right\">", 
                        "</td><td align=\"right\">"));
                writer.println("</td></tr>\n</table>");
                // Graphical presentation of differences
                writer.append(Aligner.toHTML(gts, ocrs));
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
