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
package eu.digitisation.DA;

import eu.digitisation.layout.SortPageXML;
import eu.digitisation.xml.DocumentParser;
import eu.digitisation.xml.XPathFilter;
import java.io.File;
import java.io.IOException;
import java.text.Collator;
import java.util.Locale;
import javax.xml.xpath.XPathExpressionException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author R.C.C.
 */
public class Split {

    public static void main(String[] args) throws XPathExpressionException, IOException {
        File ifile = new File(args[0]);
        Document doc = SortPageXML.isSorted(ifile) ? DocumentParser.parse(ifile)
                : SortPageXML.sorted(DocumentParser.parse(ifile));
        String[] inclusions = {"TextRegion[@type='paragraph']"};
        XPathFilter filter = new XPathFilter(inclusions, null);
        Collator collator = Collator.getInstance(Locale.FRENCH);
        // TODO: crearte Spanish collator

        String last = "";
        for (Element e : filter.selectElements(doc)) {
            String text = e.getTextContent().replaceAll("\\p{Space}+", " ").trim();

            if (text.matches("[A-Z]+(\\p{Space}|\\p{Punct}).*")) {
                String head = text.split("\\p{Space}|\\p{Punct}")[0];
                System.out.println(head);
                int n = collator.compare(text, head);
                if (n < 0) {
                    System.out.println(head);
                } else if (n == 0) {
                    System.out.println(head.toLowerCase());
                } else {
                    System.out.println("***" + head);
                }
                last = head;
            }
        }
    }
}
