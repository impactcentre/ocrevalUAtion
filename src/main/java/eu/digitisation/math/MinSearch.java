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
package eu.digitisation.math;

import java.lang.reflect.Method;

/**
 * Find local minimum
 *
 * @author R.C.C.
 */
public class MinSearch {

    public double argmin(Object o, Method f, double xleft, double xright, double absolutePrecision) {

    //Double xval; 
        // double ymid2;
        while (xright - xleft > absolutePrecision) {
            double xmid1 = (2 * xleft + xright) / 3;
            double xmid2 = (xleft + 2 * xright) / 3;
            Object ymid1 = f.invoke(o, xmid1);
            Object ymid2 = f.invoke(o, xmid2);
           
            if (ymid1 < ymid2) {
                xleft = xmid1;
            } else {
                xright = xmid2;
            }
        }
        return (left + right) / 2;
    }
