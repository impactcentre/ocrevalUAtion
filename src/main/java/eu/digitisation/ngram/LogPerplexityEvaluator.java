package eu.digitisation.ngram;

public class LogPerplexityEvaluator implements PerplexityEvaluator {
	private double[] perplexities = null;

	@Override
	public double[] calculatePerplexity(String textToEvaluate, int contextLength) {
		perplexities = new double[textToEvaluate.length()];
		for (int i = 0; i < perplexities.length; i++) {
			perplexities[i] = Math.log(Math.random());
		}
		return perplexities;
	}
}
