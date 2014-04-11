package eu.digitisation.ngram;

import java.io.File;

public class PerplexityEvaluatorsAssesmentHelper {
	public static void main(String[] args) {
		// langModel, OCRFile, resultFile
		File langModelFile = new File(args[0]);
		NgramModel providedModel = new NgramModel(langModelFile);
		
	}
}
