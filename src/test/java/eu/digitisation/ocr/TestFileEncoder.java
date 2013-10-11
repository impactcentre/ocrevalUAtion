package eu.digitisation.ocr;


import junit.framework.TestCase;
import org.junit.Test;


/**
 *
 * @author rafa
 */
public class TestFileEncoder extends TestCase {

    /**
     *
     */
    public void testFileEncoder() {
        String input = "hola&amigo2\n3.14 mi casa, todos los días\n"
                + "mesa-camilla java4you i.b.m. i+d Dª María 3+100%";
        FileEncoder encoder = new FileEncoder();
        int size = encoder.encode(input).length;
        
        assertEquals(18, size);
    }
}