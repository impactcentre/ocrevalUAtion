package eu.digitisation.ngram;

public interface PerplexityEvaluator {
	public double[] calculatePerplexity(String textToEvaluate, int contextLength);
}
