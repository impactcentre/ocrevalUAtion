/*
 * Copyright (C) 2013 IMPACT Universidad de Alicante
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
package eu.digitisation.output;

import eu.digitisation.distance.Aligner;
import eu.digitisation.distance.ArrayEditDistance;
import eu.digitisation.distance.BagOfWords;
import eu.digitisation.distance.EdOpWeight;
import eu.digitisation.distance.EditDistance;
import eu.digitisation.distance.EditDistanceType;
import eu.digitisation.distance.EditSequence;
import eu.digitisation.distance.OcrOpWeight;
import eu.digitisation.document.TermFrequencyVector;
import eu.digitisation.document.TokenArray;
import eu.digitisation.input.Batch;
import eu.digitisation.input.Parameters;
import eu.digitisation.input.WarningException;
import eu.digitisation.math.MinimalPerfectHash;
import eu.digitisation.math.Pair;
import eu.digitisation.text.CharFilter;
import eu.digitisation.text.StringNormalizer;
import eu.digitisation.text.Text;
import eu.digitisation.text.TextContent;
import eu.digitisation.xml.DocumentBuilder;
import java.io.File;
import org.w3c.dom.Element;

/**
 * Create a report in HTML format
 *
 * @author R.C.C
 */
public class Report extends DocumentBuilder {

    Element head;
    Element body;

    /**
     * Initial settings: create an empty HTML document
     */
    private void init() {
        head = addElement("head");
        body = addElement("body");
        // metadata
        Element meta = addElement(head, "meta");
        meta.setAttribute("http-equiv", "content-type");
        meta.setAttribute("content", "text/html; charset=UTF-8");
    }

    /**
     * Insert a table at the end of the document body
     *
     * @param content the table content
     * @return the table element
     */
    private Element addTable(Element parent, String[][] content) {
        Element table = addElement(parent, "table");
        table.setAttribute("border", "1");
        for (String[] row : content) {
            Element tabrow = addElement(table, "tr");
            for (String cell : row) {
                addTextElement(tabrow, "td", cell);
            }
        }
        return table;
    }

    /**
     * Create a report for the input files
     *
     * @param gtfile the ground-truth or reference file
     * @param gtencoding the ground-truth or reference file encoding (optional)
     * @param ocrfile the OCR file
     * @param ocrencoding the OCR file encoding (optional)
     * @param filter the Unicode equivalences file (CSV format)
     * @deprecated
     */
    public Report(File gtfile, String gtencoding,
            File ocrfile, String ocrencoding,
            CharFilter filter) throws WarningException {
        super("html");
        init();

        // Prepare inputs
        TextContent gt = new TextContent(gtfile, filter, gtencoding);
        TextContent ocr = new TextContent(ocrfile, filter, ocrencoding);

        // Compute error rates
        String gts = gt.toString();
        String ocrs = ocr.toString();
        double cer = ErrorMeasure.cer(gts, ocrs);
        double cerDL = ErrorMeasure.cerDL(gts, ocrs);
        double wer = ErrorMeasure.wer(gts, ocrs);
        double ber = ErrorMeasure.ber(gts, ocrs);
        Element alitab = Aligner.alignmentMap(gtfile.getName(),
                ocrfile.getName(), gts, ocrs);
        CharStatTable stats = new CharStatTable(gts, ocrs);

        // General info
        String[][] summary = {{"CER", String.format("%.2f", cer * 100)},
            {"CER (with swaps)", String.format("%.2f", cerDL * 100)},
            {"WER", String.format("%.2f", wer * 100)},
            {"WER (order independent)", String.format("%.2f", ber * 100)}
        };
        addTextElement(body, "h2", "General results");
        addTable(body, summary);
        // Alignments
        addTextElement(body, "h2", "Difference spotting");
        addElement(body, alitab);
        // CharStatTable
        addTextElement(body, "h2", "Error rate per character and type");
        addElement(body, stats.asTable());
    }

    /**
     *
     * @param batch a batch of file pairs
     * @param gtencoding the ground-truth file encoding
     * @param ocrencoding the OCR file encoding
     * @param filter Unicode character filter
     * @throws eu.digitisation.input.WarningException
     * @deprecated
     */
    public Report(Batch batch, String gtencoding, String ocrencoding,
            CharFilter filter) throws WarningException {
        super("html");
        init();

        CharStatTable stats = new CharStatTable();
        Element summaryTab;
        int numwords = 0;   // number of words in GT
        int wdist = 0;      // word distances
        int bdist = 0;      //bag-of-words distanbces

        addTextElement(body, "h2", "General results");
        summaryTab = addElement(body, "div");
        addTextElement(body, "h2", "Difference spotting");

        for (int n = 0; n < batch.size(); ++n) {
            Pair<File, File> input = batch.pair(n);
            TextContent gt = new TextContent(input.first, filter, gtencoding);
            TextContent ocr = new TextContent(input.second, filter, ocrencoding);
            String gts = gt.toString();
            String ocrs = ocr.toString();
            MinimalPerfectHash factory = new MinimalPerfectHash(false);
            TokenArray gtarray = new TokenArray(factory, gts);
            TokenArray ocrarray = new TokenArray(factory, ocrs);
            BagOfWords gtbag = new BagOfWords(gts);
            BagOfWords ocrbag = new BagOfWords(ocrs);
            Element alitab = Aligner.alignmentMap(input.first.getName(),
                    input.second.getName(), gts, ocrs);
            stats.add(gts, ocrs);
            addTextElement(body, "div", " ");
            addElement(body, alitab);
            numwords += gtarray.length();
            wdist += ArrayEditDistance.distance(gtarray.tokens(), ocrarray.tokens(),
                    EditDistanceType.LEVENSHTEIN);
            bdist += gtbag.distance(ocrbag);
        }
        //Summary table
        double cer = stats.cer();
        double wer = wdist / (double) numwords;
        double ber = bdist / (double) numwords;
        String[][] summaryContent = {{"CER", String.format("%.2f", cer * 100)},
            //   {"CER (with swaps)", String.format("%.2f", cerDL * 100)},
            {"WER", String.format("%.2f", wer * 100)},
            {"WER (order independent)", String.format("%.2f", ber * 100)}
        };
        addTable(summaryTab, summaryContent);
        // CharStatTable
        addTextElement(body, "h2", "Error rate per character and type");
        addElement(body, stats.asTable());
    }

    /**
     *
     * @param batch a batch of file pairs
     * @param pars input parameters
     * @throws eu.digitisation.input.WarningException
     */
    public Report(Batch batch, Parameters pars) throws WarningException {
        super("html");
        init();

        EdOpWeight w = new OcrOpWeight(pars);
        CharStatTable stats = new CharStatTable();
        CharFilter filter = new CharFilter(pars.compatibility.getValue());
        Element summaryTab;
        int numwords = 0;   // number of words in GT
        int wdist = 0;      // word distances
        int bdist = 0;      // bag-of-words distanbces

        addTextElement(body, "h2", "General results");
        summaryTab = addElement(body, "div");
        addTextElement(body, "h2", "Difference spotting");

        for (int n = 0; n < batch.size(); ++n) {
            Pair<File, File> input = batch.pair(n);
            Text gt = new Text(input.first);
            Text ocr = new Text(input.second);
            String gtref = gt.toString(filter);
            String ocrref = ocr.toString(filter);
            String gts = StringNormalizer.canonical(gtref, 
                    pars.ignoreCase.getValue(), 
                    pars.ignoreDiacritics.getValue(), 
                    false);
            String ocrs = StringNormalizer.canonical(ocrref, 
                    pars.ignoreCase.getValue(), 
                    pars.ignoreDiacritics.getValue(),
                    false);
            EditSequence eds = new EditSequence(gts, ocrs, w, 2000);
            TermFrequencyVector gtv = new TermFrequencyVector(gts);
            TermFrequencyVector ocrv = new TermFrequencyVector(ocrs);
            Element alitab = Aligner.bitext(input.first.getName(),
                    input.second.getName(), gtref, ocrref, w, eds);
            int[] wd = EditDistance.wordDistance(gts, ocrs, 1000);

            stats.add(eds.stats(gtref, ocrref, w));
            addTextElement(body, "div", " ");
            addElement(body, alitab);
            numwords += wd[0]; // length (words) in gts
            wdist += wd[2];    // word-based distance
            bdist += gtv.distance(ocrv);
        }
        //Summary table
        double cer = stats.cer();
        double wer = wdist / (double) numwords;
        double ber = bdist / (double) numwords;
        String[][] summaryContent = {{"CER", String.format("%.2f", cer * 100)},
            //   {"CER (with swaps)", String.format("%.2f", cerDL * 100)},
            {"WER", String.format("%.2f", wer * 100)},
            {"WER (order independent)", String.format("%.2f", ber * 100)}
        };
        addTable(summaryTab, summaryContent);
        // CharStatTable
        addTextElement(body, "h2", "Error rate per character and type");
        addElement(body, stats.asTable());
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: aligner file1 file2");
        } else {
            Element alitab = Aligner.alignmentMap("", "", args[0], args[1], null);
        }
    }
}
