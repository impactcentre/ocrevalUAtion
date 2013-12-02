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
package eu.digitisation.ocr;

import eu.digitisation.distance.Aligner;
import eu.digitisation.distance.BagOfWords;
import eu.digitisation.io.CharFilter;
import eu.digitisation.io.TextContent;
import eu.digitisation.xml.DocumentBuilder;
import java.io.File;
import org.w3c.dom.Element;

/**
 * Create a report in HTML format
 *
 * @author R.C.C
 */
public class Report {

    public static DocumentBuilder report(File gtfile, String gtencoding,
            File ocrfile, String ocrencoding,
            File repfile, File ofile) {

        CharFilter filter = (repfile == null) ? null : new CharFilter(repfile);

        // Prepare inputs
        TextContent gt = new TextContent(gtfile, gtencoding, filter);
        TextContent ocr = new TextContent(ocrfile, ocrencoding, filter);

        // Compute error rates
        String gts = gt.toString();
        String ocrs = ocr.toString();
        double cer = ErrorMeasure.cer(gts, ocrs);
        double cerDL = ErrorMeasure.cerDL(gts, ocrs);
        double wer = ErrorMeasure.wer(gts, ocrs);
        double bwer = BagOfWords.wer(gts, ocrs);

        // HTML output
        DocumentBuilder builder = new DocumentBuilder("html");
        Element head = builder.addElement("head");
        Element meta = builder.addElement(head, "meta");
        Element body = builder.addElement("body");
        Element table;
        Element row;

        // head content 
        meta.setAttribute("http-equiv", "content-type");
        meta.setAttribute("content", "text/html; charset=UTF-8");

        // body 
        builder.addTextElement(body, "h2", "General results");
        table = builder.addElement(body, "table");
        table.setAttribute("border", "1");
        row = builder.addElement(table, "tr");
        builder.addTextElement(row, "td", "CER");
        builder.addTextElement(row, "td",
                String.format("%.2f", cer * 100));
        row = builder.addElement(table, "tr");
        builder.addTextElement(row, "td", "CER-DL");
        builder.addTextElement(row, "td",
                String.format("%.2f", cerDL * 100));
        row = builder.addElement(table, "tr");
        builder.addTextElement(row, "td", "WER");
        builder.addTextElement(row, "td", String.format("%.2f", wer * 100));
        row = builder.addElement(table, "tr");
        builder.addTextElement(row, "td", "WER (bag of words)");
        builder.addTextElement(row, "td", String.format("%.2f", bwer * 100));
        // Alignments
        builder.addTextElement(body, "h2", "Difference spotting");
        builder.addElement(body,
                Aligner.alignmentTable(gts, ocrs));
        // Stats
        builder.addTextElement(body, "h2",
                "Error rate per character and type");
        builder.addElement(body, ErrorMeasure.stats(gts, ocrs));
        builder.write(ofile);
        return builder;
    }
}
