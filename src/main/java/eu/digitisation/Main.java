package eu.digitisation;

import eu.digitisation.io.Batch;
import eu.digitisation.io.CharFilter;
import eu.digitisation.io.UnsupportedFormatException;
import eu.digitisation.ocrevaluation.Report;
import java.io.File;
import java.io.InvalidObjectException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main class for ocrevalUAtion: version 0.92
 */
public class Main {

    static final String helpMsg = "Usage:\t"
            + "ocrevalUAtion -gt file1 [encoding] "
            + "-ocr file2 [encoding] "
            + "-d output_dir [-r equivalences_file] [-c]";

    private static void exit_gracefully() {
        System.err.println(helpMsg);
        System.exit(0);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws UnsupportedFormatException {

        File repfile = null;     // char filter
        File gtfile = null;      // ground-truth
        File ocrfile = null;     // ocr-output
        File ofile = null;       // this program output
        String gtencoding = null;
        String ocrencoding = null;
        File workingDirectory = null; // working directory 
        boolean compatibility = false; // Unicode comaptibility mode
        
        // Read parameters (String switch needs Java 1.7 or later)
        for (int n = 0; n < args.length; ++n) {
            String arg = args[n];
            if (arg.equals("-h")) {
                exit_gracefully();
            } else if (arg.equals("-gt")) {
                gtfile = new File(args[++n]);
                if (n + 1 < args.length && !args[n + 1].startsWith("-")) {
                    gtencoding = args[++n];
                }
            } else if (arg.equals("-ocr")) {
                ocrfile = new File(args[++n]);
                if (n + 1 < args.length && !args[n + 1].startsWith("-")) {
                    ocrencoding = args[++n];
                }
            } else if (arg.equals("-d")) {
                workingDirectory = new File(args[++n]);
            } else if (arg.equals("-r")) {
                repfile = new File(args[++n]);
            } else if (arg.equals("-o")) {
                ofile = new File(args[++n]);
            } else if (arg.equals("-c")) {
                compatibility = true;
            } else {
                System.err.println("Unrecognized option " + arg);
                exit_gracefully();
            }
        }

        if (gtfile == null || ocrfile == null) {
            System.err.println("Not enough arguments");
            exit_gracefully();
        } else if (workingDirectory == null) {
            String dir = ((ofile == null) ? ocrfile : ofile)
                    .getAbsolutePath()
                    .replaceAll(File.separator + "(\\.|\\w)+$", "");
            workingDirectory = new File(dir);
        }
        System.out.println("Working directory set to " + workingDirectory);

        if (workingDirectory == null
                || !workingDirectory.isDirectory()) {
            System.out.println(workingDirectory + " is not a valid directory");
        } else {
            try {
                if (ofile == null) {
                    String prefix = workingDirectory + File.separator
                            + gtfile.getName().replaceFirst("[.][^.]+$", "");
                    ofile = new File(prefix + "_out.html");
                }
                //           Report report = new Report(gtfile, gtencoding, ocrfile, ocrencoding, repfile);
                Batch batch = new Batch(gtfile, ocrfile); // accepts also directories
                CharFilter filter = (repfile == null) 
                        ? new CharFilter() 
                        : new CharFilter(repfile);
                filter.setCompatibility(compatibility);
                Report report = new Report(batch, gtencoding, ocrencoding, filter);
                report.write(ofile);
            } catch (InvalidObjectException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }
}
