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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContextLengthRange {
	private int start;

	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}

	private int end;

	public ContextLengthRange(int start, int end) {
		this.start = start;
		this.end = end;
	}

	/**
	 * Creates ContextLengthRange objects based on the provided string
	 * 
	 * @param contextLengthRange
	 *            context length range string which needs to follow the
	 *            following regex: [1-9]-[1-9] where the first digit is a start
	 *            and the second is end
	 * @return ContextLengthRange object created from the given string
	 */
	public static ContextLengthRange parseContextLengthRange(
			String contextLengthRange) {
		Pattern clPattern = Pattern.compile("([1-9])-([1-9])");
		Matcher clMatcher = clPattern.matcher(contextLengthRange);
		if (clMatcher.find()) {
			return new ContextLengthRange(Integer.parseInt(clMatcher.group(1)),
					Integer.parseInt(clMatcher.group(2)));
		} else {
			throw new IllegalArgumentException(
					"Context length needs to be in format of [1-9]-[1-9]!");
		}
	}
}
