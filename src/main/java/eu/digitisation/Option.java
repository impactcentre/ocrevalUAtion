/*
 * Copyright (C) 2014 Universidad de Alicante
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
package eu.digitisation;

/**
 *
 * @author R.C.C.
 */
public enum Option {

    IGNORE_CASE(false),
    IGNORE_DIACRITICS(false),
    IGNORE_PUNCTUATION(false),
    UNICODE_COMPATIBILITY(false);

    private Boolean value;

    /**
     * Create always with default value
     *
     * @param value the default value
     */
    Option(boolean value) {
        this.value = value;
    }

    /**
     *
     * @return the value of this option
     */
    public boolean valueOf() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    /**
     * Textual explanation
     *
     * @return a string with the meaning of the option
     */
    @Override
    public String toString() {
        switch (this) {
            case IGNORE_CASE:
                return "Ignore case";
            case IGNORE_DIACRITICS:
                return "Ignore diacritics";
            case IGNORE_PUNCTUATION:
                return "Ignore punctuation";
            case UNICODE_COMPATIBILITY:
                return "Activate Unicode compatibility";
            default:
                return null;
        }
    }

    /**
     * URL for further information
     *
     * @return the URL or text where further information can be found
     */
    public String getHelp() {
        switch (this) {
            case UNICODE_COMPATIBILITY:
                return "http://unicode.org/reports/tr15/#Canon_Compat_Equivalence";
            default:
                return null;
        }
    }
    
    public static Boolean[] getOptionValues() {
        Option[] ops = Option.values();
        Boolean[] vals = new Boolean[ops.length];
        int n = 0;
        for (Option op : ops) {
            vals[n++] = op.valueOf();
        }
        return vals;
    }
}
