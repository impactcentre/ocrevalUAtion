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
package eu.digitisation.io;

/**
 * Normalizes strings: collapse whitespace and use composed form (see
 * java.text.Normalizer.Form)
 *
 * @author R.C.C.
 */
public class StringNormalizer {

    final static java.text.Normalizer.Form decomposed
            = java.text.Normalizer.Form.NFD;
    final static java.text.Normalizer.Form composed
            = java.text.Normalizer.Form.NFC;
    static final java.text.Normalizer.Form compatible
            = java.text.Normalizer.Form.NFKC;

    /**
     * Reduce whitespace (including line and paragraph separators)
     *
     * @param s a string.
     * @return The string with simple spaces between words.
     */
    public static String reduceWS(String s) {
        return s.replaceAll("(\\p{Space}|\\p{general_category=Zl}|\\p{general_category=Zp})+", " ")
                .trim();
    }

    /**
     * @param s a string
     * @return the canonical representation of the string.
     */
    public static String canonical(String s) {
        return java.text.Normalizer.normalize(s, composed);
    }
    
 /**
     * @param s a string
     * @return the canonical representation of the string 
     * with normalized compatible characters.
     */
    public static String compatible(String s) {
        return java.text.Normalizer.normalize(s, compatible);
    }
    
    /**
     * @param s a string
     * @return the string with characters <, >, &, " escaped
     */
    public static String encode(String s) {
        StringBuilder result = new StringBuilder();
        for (Character c : s.toCharArray()) {
            if (c.equals('<')) {
                result.append("&lt;");
            } else if (c.equals('>')) {
                result.append("&gt;");
            } else if (c.equals('"')) {
                result.append("&quot;");
            } else if (c.equals('&')) {
                result.append("&amp;");
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
}
