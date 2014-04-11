package eu.digitisation.ngram;

import java.io.File;

/**
 * Interface for perplexity evaluator. It calculates perplexity of characters in a given text.
 * @author tparkola
 *
 */
public class NgramPerplexityEvaluator implements PerplexityEvaluator {
    NgramModel ngram;

    NgramPerplexityEvaluator(File file) {
        ngram = new NgramModel(file);
    }
	/**
	 * Calculates perplexity for each character of a given text.
	 * @param textToEvaluate perplexity of characters contained in this text is calculated 
	 * @param contextLength the length of character context that is considered when calculating perplexity
	 * @return array of perplexity values, each item in the array is a perplexity of corresponding character in the given text.
	 */
    @Override
    public double[] calculatePerplexity(String textToEvaluate, int contextLength) {
        int textLen = textToEvaluate.length();
        double[] logprobs = new double[textLen];
        for (int pos = 0; pos < textLen; ++pos) {
            ngram.logProb(textToEvaluate.substring(0, pos - 1), textToEvaluate.charAt(pos));
        }
        return logprobs;
    }
}
