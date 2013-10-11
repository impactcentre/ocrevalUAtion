package eu.digitisation.ocr;

import junit.framework.TestCase;
import org.junit.Test;

/**
 *
 * @author R.C.C.
 */
public class TestWordScanner extends TestCase {

    public void testWordScanner() throws Exception {
        String input = "hola&amigo2\n3.14 mi casa, todos los días\n"
                 + "mesa-camilla java4you i.b.m. i+d Dª María 3+100%";
        WordScanner scanner = new WordScanner(input);
        String word;
        int num = 0;
        while ((word = scanner.nextWord()) != null) {
            ++num;
            //System.out.println(word);
        }
        assertEquals(18, num);
    }
}
