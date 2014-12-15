/*
 * Copyright (C) 2013 Universidad de Alicante
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package eu.digitisation.ngram;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

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

    public static void main(String[] args) throws FileNotFoundException {
        NgramModel model = new NgramModel(new File(args[0]));
        int contextLenght = Integer.parseInt(args[1]);
        InputStream is = (args.length == 3)
                ? new FileInputStream(new File(args[2]))
                : System.in;

        TextPerplexity result
                = new TextPerplexity(model, is, contextLenght);

        String text = result.getText();
        double[] perps = result.getPerplexities();

        for (int n = 0; n < text.length(); ++n) {
            System.out.println(text.charAt(n) + " " + perps[n]);
        }
    }
}
