package eu.digitisation.io;

import junit.framework.TestCase;
/**
 *
 * @author rafa
 */
public class TestUnicodeReader extends TestCase {

    public void testUnicodeReader() {
        String input = "día, mes y año";
        String ref = "[100, 237, 97, 44, 32, 109, 101, 115, 32, 121, 32, 97, 241, 111]";

        String output =
                java.util.Arrays.toString(UnicodeReader.toCodepoints(input));
        //System.out.println(output);
        assertEquals(ref, output);
    }
}
