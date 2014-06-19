/*
 * Copyright (C) 2014 IMPACT Centre of Competence
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
package eu.digitisation.DA;

/**
 * Classifies word according to case.
 * <p>UPPERCASE: all characters uppercase</p>
 * <p>LOWERCASE: all characters lowercase</p>
 * <p>MIXED: upper and lowercase characters</p>
 *
 * @author R.C.C
 */
public enum WordType {

    UPPERCASE, LOWERCASE, MIXED;

    public static WordType typeOf(String word) {
        if (word.matches("\\p{Lu}+")) {
            return UPPERCASE;
        } else if (word.matches("[\\p{L}&&[^\\p{Lu}]]+")) {
            return LOWERCASE;
        } else {
            return MIXED;
        }
    }
    
    /**
     * Test if a word with mixed type can be the initial word in a sentence or
     * paragraph (lowercase with first character uppercase)
     *
     * @param word a string
     * @return true if the word is a sequence of Unicode letters whose first
     * letter is uppercase and all trailing letters are lowercase 
     */
    public static boolean isFirstWord(String word) {
        return word.matches("\\p{Punct}*\\p{Lu}[\\p{L}&&[^\\p{Lu}]]*");
    }

    /**
     *
     * @param word a string
     * @return true if the word is a sequence of Unicode letters with length
     * greater than 2 and it contains a single lowercase character
     */
    public static boolean nearlyUpper(String word) {
        return word.matches("\\p{Lu}+\\p{L}\\p{Lu}+|\\p{L}\\p{Lu}{2,}|\\p{Lu}{2,}\\p{L}");
    }
}
