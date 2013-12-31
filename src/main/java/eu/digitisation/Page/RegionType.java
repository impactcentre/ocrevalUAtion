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
package eu.digitisation.Page;

import eu.digitisation.io.FileType;

/**
 * Types of text regions in a document
 *
 * @author R.C.C.
 */
public enum RegionType {

    PAGE, BLOCK, LINE, WORD, OTHER;

    public static RegionType valueOf(FileType ftype, String rtype) {
        switch (ftype) {
            case PAGE:
                if (rtype.equals("Page")) {
                    return PAGE;
                } else if (rtype.equals("TextRegion")) {
                    return BLOCK;
                } else if (rtype.equals("TextLine")) {
                    return LINE;
                } else if (rtype.equals("Word")) {
                    return WORD;
                } else {
                    return OTHER;
                }
            case HOCR:
                if (rtype.equals("ocr_page")) {
                    return PAGE;
                } else if (rtype.equals("ocr_carea") || rtype.equals("ocr_par")) {
                    return BLOCK;
                } else if (rtype.equals("ocr_line")) {
                    return LINE;
                } else if (rtype.equals("ocrx_word")) {
                    return WORD;
                } else {
                    return OTHER;
                }
            default:
                return null;
        }
    }
}
