package eu.digitisation.ngram;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.xmlbeans.SystemProperties;

import eu.digitisation.text.WordScanner;

public class PerplexityEvaluatorAssesmentHelper {
	public static void main(String[] args) throws IOException {
		// langModel, OCRFile, contextLengthRange, resultFile
		File langModelFile = new File(args[0]);
		File OCRFile = new File(args[1]);
		
		String OCRText = extractString(OCRFile);
		
		NgramModel providedModel = new NgramModel(langModelFile);

		ContextLengthRange contextLengthRange = ContextLengthRange.parseContextLengthRange(args[2]);
		
		double[][] perplexities = new double[contextLengthRange.getEnd()-contextLengthRange.getStart() + 1][OCRText.length()];
		
		LogPerplexityEvaluator logPerplexityEvaluator = new LogPerplexityEvaluator(providedModel);
		
		for (int i = contextLengthRange.getStart(); i <=contextLengthRange.getEnd(); i++) {
			perplexities[i] = logPerplexityEvaluator.calculatePerplexity(OCRText, i);
		}
		
		//printPerplexities
	}

	private static String extractString(File OCRFile)
			throws FileNotFoundException, IOException {
		BufferedReader reader = new BufferedReader(new FileReader(OCRFile));
		
		StringBuffer OCRFileText = new StringBuffer();
		
		String line = null;
		while ((line = reader.readLine())!=null) {
			OCRFileText.append(line + "\n");
		}	
		
		reader.close();
		String OCRText = OCRFileText.toString();
		return OCRText;
	}
}
