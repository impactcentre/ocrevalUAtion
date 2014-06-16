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
import eu.digitisation.log.Messages;
import eu.digitisation.text.WordScanner;
import eu.digitisation.xml.DocumentParser;
import eu.digitisation.xml.XPathFilter;
import java.io.File;
import java.io.IOException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.xml.xpath.XPathExpressionException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author R.C.C. Diccionario de Autoridades Lista de problemas con los lemas:
 * Citas, por ejemplo "GARBI, Cart. 4". PENDING
 * <p>
 * I mayúscula transcrita incorréctamente como l. SOLVED</p>
 * <p>
 * ñ minúscula en el original. SOLVED</p>
 * <p>
 * Lemas multipalabra. SOLVED</p>
 * <p>
 * Comprobación de consistencia alfabética entre páginas consecutivas.
 * PENDING</p>
 * <p>
 * Participios tras infinitivo, como "BIRLAR ... BIRLADO", violan orden
 * alfabético). SOLVED</p>
 *
 * <p>
 * Vacilación en el orden alfabético de B/V, X/J PENDING</p>
 *
 * <p>
 * Error en ordenación: puede estar causado por el lema actual, por el antecesor
 * o por ambos. PENDING</p>
 *
 * @todo create collator for old Spanish
 */
public class Split {

    static XPathFilter filter; // Selects XML elements with relevant content
    final static Collator collator;  // Defines the lexicographic order
    final static String lemmaRE; // Regex for one lemma
    final static String headRE; // Regex for multiword lemmas

    static {
        String[] inclusions = {"TextRegion[@type='paragraph']"};
        try {
            filter = new XPathFilter(inclusions, null);
        } catch (XPathExpressionException ex) {
            Messages.severe(ex.getMessage());
        }
        collator = Collator.getInstance(Locale.FRENCH);  // Create old Spanish rules
        lemmaRE = "(\\p{Lu}|[ñ])+";
        headRE = lemmaRE + "([,]?\\p{Space}" + lemmaRE + ")*";
        
    }

    /**
     * 
     * @param e
     * @return uppercase prefix or null if none found
     * @throws IOException 
     */
    private static String header(Element e) throws IOException {
        String text = e.getTextContent().trim();
       
        WordScanner scanner = new WordScanner(text, "^" + headRE);
        String prefix = scanner.nextWord();
         System.out.println(text + "\n -> " + prefix);
        return prefix;
    }

    public static List<String> headers(Document doc) throws IOException {
        List<String> list = new ArrayList<String>();
        for (Element e : filter.selectElements(doc)) {
            String head = header(e);
            if (head != null && !head.isEmpty()) {
                list.add(head);
            }
        }
        return list;
    }

    /**
     * Function for debugging
     *
     * @param file
     */
    public static void view(File file) throws IOException {
        Document doc = SortPageXML.isSorted(file) ? DocumentParser.parse(file)
                : SortPageXML.sorted(DocumentParser.parse(file));
        for (String head : headers(doc)) {
            System.out.println(head);
        }
    }

    public static void split(File ifile) throws IOException {
        Document doc = SortPageXML.isSorted(ifile) ? DocumentParser.parse(ifile)
                : SortPageXML.sorted(DocumentParser.parse(ifile));

        System.out.println(ifile);
        String last = "";
        for (Element e : filter.selectElements(doc)) {
            String text = e.getTextContent().replaceAll("\\p{Space}+", " ").trim();
            if (!text.isEmpty()) {
                String head = text.split("\\p{Space}|\\p{Punct}")[0];
                //System.out.println(text);
                if (head.matches(lemmaRE)) {
                    int n = collator.compare(last, head);
                    if (n < 0) {
                        System.out.println(head);
                    } else if (n == 0) {
                        System.out.println("\t" + head.toLowerCase());
                    } else if (isParticiple(head, last)) {
                        System.out.println("*Participle*" + head);
                    } else {
                        System.out.println("***" + head);
                    }
                    last = head;
                } else if (head.replaceAll("l", "I").matches(lemmaRE)) {
                    // wrong transcription
                    System.out.println(">" + head);
                } else if (head.matches(lemmaRE + "\\p{L}" + lemmaRE)) {
                    // a single mismatch
                    System.out.println(">>>" + head);
                } else if (head.matches(headRE)) {
                    System.out.println("<<<<" + head);
                } else {
                    ;//System.out.println(">>>" + text);
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        for (String arg : args) {
            Split.view(new File(arg));
        }
    }

    private static boolean isParticiple(String head, String last) {
        return last.replaceFirst("AR$", "")
                .equals(head.replaceFirst("ADO$", ""));
    }
}
