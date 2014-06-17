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

import static eu.digitisation.DA.WordType.LOWERCASE;
import static eu.digitisation.DA.WordType.MIXED;
import static eu.digitisation.DA.WordType.UPPERCASE;
import eu.digitisation.layout.SortPageXML;
import eu.digitisation.log.Messages;
import eu.digitisation.text.CharFilter;
import eu.digitisation.text.StringNormalizer;
import eu.digitisation.xml.DocumentParser;
import eu.digitisation.xml.XPathFilter;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.Collator;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    static XPathFilter selector; // Selects XML elements with relevant content
    final static Collator collator;  // Defines the lexicographic order
    static CharFilter cfilter; // Map PUA characters

    static {
        String[] inclusions = {"TextRegion[@type='paragraph']"};
        URL url = Split.class.getResource("UnicodeCharEquivalences.txt");
        File file = new File(url.getFile());
        System.out.println(file);
        try {
            selector = new XPathFilter(inclusions, null);
        } catch (XPathExpressionException ex) {
            Messages.severe(ex.getMessage());
        }
        collator = OldSpanishCollator.getInstance();
        cfilter = new CharFilter(true, file);
    }

    /**
     *
     * @param text a string
     * @return the longest prefix of the text containing only uppercase letters
     */
    protected static String header(String text) {
        StringBuilder builder = new StringBuilder();
        String[] tokens = text.split("\\p{Space}+");

        for (String token : tokens) {
            String normal = cfilter.translate(token);
            String word = StringNormalizer.trim(normal);

            switch (WordType.typeOf(word)) {

                case UPPERCASE: // header word
                    if (builder.length() > 0) {
                        builder.append(' ');
                    }
                    builder.append(token);
                    break;
                case LOWERCASE: // end of header
                    return builder.toString();
                case MIXED: // striking content
                    if (WordType.initial(word)) {
                        return builder.toString();
                    } else {
                        if (builder.length() > 0) {
                            builder.append(' ');
                        }
                        builder.append(token);
                    }
            }
        }
        return builder.toString();
    }

    /**
     *
     * @param e a document element
     * @return the longest prefix of the textual content containing only
     * uppercase letters
     * @throws IOException
     */
    protected static String header(Element e) throws IOException {
        String text = e.getTextContent().trim();
        return header(text);
    }

    /**
     *
     * @param doc an XML document
     * @return the initial sentences in every selected textual element
     * @throws IOException
     */
    public static List<String> headers(Document doc) throws IOException {
        List<String> list = new ArrayList<String>();
        for (Element e : selector.selectElements(doc)) {
            String head = header(e);
            if (!head.isEmpty()) {
                list.add(head);
            }
        }
        return list;
    }

    /**
     *
     * @param text a string of text
     * @return the initial word (sequence of consecutive letters) in the text
     */
    private static String initial(String text) {
        if (text.length() > 0 && Character.isLetter(text.charAt(0))) {
            return text.split("[^\\p{L}]+")[0];
        } else {
            return "";
        }
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

    public static String split(File ifile, String last) throws IOException {
        Document doc = SortPageXML.isSorted(ifile) ? DocumentParser.parse(ifile)
                : SortPageXML.sorted(DocumentParser.parse(ifile));

        System.out.println(ifile);
        for (String head : headers(doc)) {
            if (!head.isEmpty()) {
                String start = initial(head).replaceAll("ñ", "Ñ");
                //System.out.println(text);
                if (WordType.typeOf(start) == WordType.UPPERCASE) {
                    int n = collator.compare(last, start);
                    if (n < 0) {
                        System.out.println("<entry>" + head + "</entry>");
                    } else if (n == 0) {
                        System.out.println(" <subentry>" + head + "</subentry>");
                    } else if (isParticiple(start, last)) {
                        System.out.println("<PastPart>" + head + "</PastPart>");
                    } else {
                        System.out.println("***");
                        System.out.println("<entry>" + head + "</entry>");
                    }
                    last = start;
                } else if (WordType.typeOf(start.replaceAll("l", "I"))
                        == WordType.UPPERCASE) {
                    // wrong transcription
                    System.out.println("<Itypo>" + head + "</Itypo>");
                } else if (WordType.nearlyUpper(start)) {
                    // a single mismatch
                    System.out.println("<check>" + head + "</check>");
                } else if (WordType.typeOf(start) == WordType.MIXED
                        && !WordType.initial(start)) {
                    System.out.println("<<<<" + head);
                } else {
                    ;//System.out.println(">>>" + text);
                }
            }
        }
        return last;
    }

    /**
     * Check if an entry can be a past participle of the preceding word
     *
     * @param head the entry
     * @param last the preceding word
     * @return true if head is a past participle entry after the last word
     */
    protected static boolean isParticiple(String head, String last) {
        //System.out.println("="+last.replaceFirst("[AEI]R$", ""));
        return last.replaceFirst("[AEI]R(SE)?$", "")
                .equals(head.replaceFirst("[AI]DO$", ""));
    }

    public static void main(String[] args) throws IOException {
        String lastEntry = "";
        for (String arg : args) {
            File file = new File(arg);
            //Split.view(file);
            lastEntry = Split.split(file, lastEntry);
        }
    }
}
