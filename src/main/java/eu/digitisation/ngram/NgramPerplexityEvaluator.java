package eu.digitisation.ngram;

import eu.digitisation.text.Encoding;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Perplexity evaluator based on an n-gram model
 *
 */
public class NgramPerplexityEvaluator implements PerplexityEvaluator {

    NgramModel ngram;

    public NgramPerplexityEvaluator(NgramModel ngram) {
        this.ngram = ngram;
    }

    public NgramPerplexityEvaluator(File file) {
        ngram = new NgramModel(file);
    }

    public void addWords(File file) {
        Charset encoding = Encoding.detect(file);
        ngram.addWords(file, null, true);
    }

    /**
     * Calculates perplexity for each character of a given text.
     *
     * @param textToEvaluate perplexity of characters contained in this text is
     * calculated
     * @param contextLength the length of character context that is considered
     * when calculating perplexity
     * @return array of perplexity values, each item in the array is a
     * perplexity of corresponding character in the given text.
     */
    @Override
    public double[] calculatePerplexity(String textToEvaluate, int contextLength) {
        int textLen = textToEvaluate.length();
        double[] logprobs = new double[textLen];
        for (int pos = 0; pos < textLen; ++pos) {
            int beg = Math.max(0, pos - contextLength);
            String context = textToEvaluate.substring(beg, pos);
            logprobs[pos] = ngram.logProb(context, textToEvaluate.charAt(pos));
        }
        return logprobs;
    }

    private static String getText(File file) throws FileNotFoundException, IOException {
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        while (reader.ready()) {
            builder.append(reader.readLine()).append(" ");
        }
        return builder.toString();
    }

    public static void main(String[] args) throws FileNotFoundException, IOException {
        NgramModel ngram = new NgramModel(3);
        File fin = null;
        File fout = null;
        int len = 1;

        if (args.length == 0) {
            System.err.println("Usage:");
        } else {
            for (int k = 0; k < args.length; ++k) {
                String arg = args[k];

                if (arg.equals("-n")) {
                    ngram = new NgramModel(new Integer(args[++k]));
                } else if (arg.equals("-l")) {
                    len = Integer.parseInt(args[++k]);
                } else if (arg.equals("-o")) {
                    fout = new File(args[++k]);
                } else if (arg.equals("-i")) {
                    fin = new File(args[++k]);
                } else {
                    String text = NgramPerplexityEvaluator.getText(new File(arg));
                    ngram.addWord(text);
                    System.err.println("\r" + arg);
                }
            }
            if (fin != null) {
                NgramPerplexityEvaluator evaluator
                        = new NgramPerplexityEvaluator(ngram);
                String text = NgramPerplexityEvaluator.getText(fin);
                double[] logprobs = evaluator.calculatePerplexity(text, len);
                for (int n = 0; n < text.length(); ++n) {
                    System.out.println(text.charAt(n) + " " + logprobs[n]);
                }
            } else if (fout != null) {
                ngram.save(fout);
            }
        }
    }
}
