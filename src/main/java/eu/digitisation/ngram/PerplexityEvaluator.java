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

/**
 * Interface for perplexity evaluator. It calculates perplexity of characters in a given text.
 * @author tparkola
 *
 */
public interface PerplexityEvaluator {
	
	/**
	 * Calculates perplexity for each character of a given text.
	 * @param textToEvaluate perplexity of characters contained in this text is calculated 
	 * @param contextLength the length of character context that is considered when calculating perplexity
	 * @return array of perplexity values, each item in the array is a perplexity of corresponding character in the given text.
	 */
	public double[] calculatePerplexity(String textToEvaluate, int contextLength);
}
