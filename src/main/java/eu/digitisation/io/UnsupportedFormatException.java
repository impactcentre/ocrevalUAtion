/*
 * Copyright (C) 2014 Universidad  de Alicante
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

import java.io.File;

/**
 *
 * @author R.C.C.
 */
public class UnsupportedFormatException extends Exception {

    private static final long serialVersionUID = 1L;
    File file;
    FileType type;

    public UnsupportedFormatException(String message) {
        super(message);
    }

    public UnsupportedFormatException(File file, FileType type) {
        this.file = file;
        this.type = type;
    }

    @Override
    public String getMessage() {
        if (file == null) {
            return super.getMessage();
        } else {
            return "Unsupported file format (" + type
                    + " format) for file " + file.getName();
        }
    }
}
