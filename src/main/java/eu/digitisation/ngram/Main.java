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

import eu.digitisation.Page.Sort;
import eu.digitisation.io.TextContent;
import eu.digitisation.ocrevaluation.ErrorMeasure;
import eu.digitisation.xml.DocumentWriter;
import java.io.File;
import java.io.IOException;
import org.w3c.dom.Document;

/**
 *
 * @author R.C.C.
 */
public class Main {

    public static void main(String[] args) throws IOException {
        File f1 = new File(args[0]);
        File f2 = new File(args[1]);
        TextContent c1 = new TextContent(f1, null);
        TextContent c2 = new TextContent(f2, null);
        String s1 = c1.toString();
        String s2 = c2.toString();
        NgramModel m1 = new NgramModel(3);
        NgramModel m2 = new NgramModel(3);
        m1.addWord(s1);
        m2.addWord(s2);

        System.out.println(Sort.isSorted(f1));
        
        Document doc = Sort.sorted(f1);
        DocumentWriter writer = new DocumentWriter(doc);
        File f3 = new File(f1.getName().replace(".xml", "_sorted.xml"));
        writer.write(f3);

        c1 = new TextContent(f3, null);
        s1 = c1.toString();
        
        double dis = m1.distance(m2);
        double cer = ErrorMeasure.cer(s1, s2);

        System.out.println(cer + " " + dis);
    }

}
