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

import eu.digitisation.log.Messages;
import java.text.Collator;
import java.text.ParseException;
import java.text.RuleBasedCollator;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author R.C.C
 */
public class OldSpanishCollator {

    final static String nTilde = "\u00F1";    // ñ
    final static String NTilde = "\u00D1";  // Ñ
    final static String rules = ("< a,A < b,B < c,C "
            + "< ch, cH, Ch, CH "
            + "< d,D < e,E < f,F "
            + "< g,G < h,H < i,I < j,J < k,K < l,L "
            + "< ll, lL, Ll, LL "
            + "< m,M < n,N "
            + "< " + nTilde + "," + NTilde + " "
            + "< o,O < p,P < q,Q < r,R "
            + "< s,S < t,T < u,U < v,V < w,W < x,X "
            + "< y,Y < z,Z");

   
    
    public static Collator getInstance() {
        try {
            return new RuleBasedCollator(rules);
        } catch (ParseException ex) {
            Messages.severe(ex.getMessage());
            return Collator.getInstance(Locale.FRENCH);
        }
    }
}
