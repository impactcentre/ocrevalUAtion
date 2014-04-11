package eu.digitisation.ngram;

public class LogPerplexityEvaluator implements PerplexityEvaluator {
	private double[] perplexities = null;
	private NgramModel model;
	
	/**
	 * Creates LogPerplexityEvaluator.
	 * @param model the model to be used for perplexity calculation.
	 */
	public LogPerplexityEvaluator(NgramModel model) {
		this.model = model;
	}

	@Override
	public double[] calculatePerplexity(String textToEvaluate, int contextLength) {
		perplexities = new double[textToEvaluate.length()];
		for (int i = 0; i < perplexities.length; i++) {
			perplexities[i] = Math.log(Math.random());
		}
		return perplexities;
	}
}
