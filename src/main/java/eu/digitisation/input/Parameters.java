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
package eu.digitisation.input;

import java.io.File;

/**
 * Stores all the input parameters used by the program
 *
 * @author R.C.C.
 */
public class Parameters {

    private static final long serialVersionUID = 1L;
    // Define program parameters: input files 
    final Parameter<File> gtfile;
    final Parameter<File> ocrfile;
    final Parameter<File> eqfile;
    // Define program parameters: boolean options 
    final Parameter<Boolean> ignoreCase;
    final Parameter<Boolean> ignoreDiacritics;
    final Parameter<Boolean> ignorePunctuation;
    final Parameter<Boolean> compatibility;

    public Parameters() {
        this.gtfile = new Parameter<File>("ground-truth file");
        this.ocrfile = new Parameter<File>("OCR file");
        this.eqfile = new Parameter<File>("Unicode equivalences file");
        this.ignoreCase = new Parameter<Boolean>("Ignore case", false, "");
        this.ignoreDiacritics = new Parameter<Boolean>("Ignore diacritics", false, "");
        this.ignorePunctuation = new Parameter<Boolean>("Ignore punctuation", false, "");
        this.compatibility = new Parameter<Boolean>("Unicode compatibilty of characters", false,
                "http://unicode.org/reports/tr15/#Canon_Compat_Equivalence");
    }
}
