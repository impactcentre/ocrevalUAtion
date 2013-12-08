package eu.digitisation;

import eu.digitisation.ocrevaluation.Report;
import java.io.File;

/**
 * Main class for ocrevalUAtion: version 0.92
 */
public class Main {

    static final String helpMsg = "Usage:\t"
            + "ocrevalUAtion -gt file1 [encoding] "
            + "-ocr file2 [encoding] "
            + "-d output_dir [-r replacements_file]";

    private static void exit_gracefully() {
        System.err.println(helpMsg);
        System.exit(0);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        File repfile = null;     // char filter
        File gtfile = null;      // ground-truth
        File ocrfile = null;     // ocr-output
        File ofile = null;       // this program output
        String gtencoding = null;
        String ocrencoding = null;
        File workingDirectory = null; // working directory 

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
            if (ofile == null) {
                String prefix = workingDirectory + File.separator
                        + gtfile.getName().replaceFirst("[.][^.]+$", "");
                ofile = new File(prefix + "_out.html");
            }
            Report report = new Report(gtfile, gtencoding, ocrfile, ocrencoding, repfile);
            report.write(ofile);

        }
    }
}
