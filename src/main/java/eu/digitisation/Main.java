package eu.digitisation;

import eu.digitisation.distance.BagOfWords;
import eu.digitisation.distance.Aligner;
import eu.digitisation.io.CharFilter;
import eu.digitisation.io.TextContent;
import eu.digitisation.ocr.ErrorMeasure;
import eu.digitisation.xml.DocumentBuilder;
import java.io.File;
import org.w3c.dom.Element;

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
            String prefix = workingDirectory + File.separator
                    + gtfile.getName().replaceFirst("[.][^.]+$", "");
            File ofile = new File(prefix + "_out.html");
            CharFilter filter = (repfile == null) ? null : new CharFilter(repfile);
 
            // Prepare inputs
            TextContent gt = new TextContent(gtfile, gtencoding, filter);
            TextContent ocr = new TextContent(ocrfile, ocrencoding, filter);

            // Compute error rates
            String gts = gt.toString();
            String ocrs = ocr.toString();
            double cer = ErrorMeasure.cer(gts, ocrs);
            double cerDL = ErrorMeasure.cerDL(gts, ocrs);
            double wer = ErrorMeasure.wer(gts, ocrs);
            double bwer = BagOfWords.wer(gts, ocrs);

            // HTML output
            DocumentBuilder builder = new DocumentBuilder("html");
            Element head = builder.addElement("head");
            Element meta = builder.addElement(head, "meta");
            Element body = builder.addElement("body");

            Element table = builder.addElement(body, "table");

            // head content 
            meta.setAttribute("http-equiv", "content-type");
            meta.setAttribute("content", "text/html; charset=UTF-8");

            // body 
            builder.addTextElement(body, "h2", "General results");

            table.setAttribute("border", "1");
            Element row = builder.addElement(table, "tr");
            builder.addTextElement(row, "td", "CER");
            builder.addTextElement(row, "td",
                    String.format("%.2f", cer * 100));
            row = builder.addElement(table, "tr");
            builder.addTextElement(row, "td", "CER-DL");
            builder.addTextElement(row, "td",
                    String.format("%.2f", cerDL * 100));
            row = builder.addElement(table, "tr");
            builder.addTextElement(row, "td", "WER");
            builder.addTextElement(row, "td", String.format("%.2f", wer * 100));
            row = builder.addElement(table, "tr");
            builder.addTextElement(row, "td", "WER (bag of words)");
            builder.addTextElement(row, "td", String.format("%.2f", bwer * 100));
            // Alignments
            builder.addTextElement(body, "h2", "Difference spotting");
            builder.addElement(body,
                    Aligner.alignmentTable(gts, ocrs));
            // Stats
            builder.addTextElement(body, "h2",
                    "Error rate per character ant type");
            builder.addElement(body, ErrorMeasure.stats(gts, ocrs));
            builder.write(ofile);

        }
    }
}
