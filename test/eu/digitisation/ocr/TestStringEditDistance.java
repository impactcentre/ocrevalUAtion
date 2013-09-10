package eu.digitisation.ocr;

import junit.framework.TestCase;
import org.junit.Test;

/**
 *
 * @author rafa
 */
public class TestStringEditDistance extends TestCase {
    @Test
    public void testStringEditDistance() {
        String first = "holanda";
        String second = "wordland";
        int dist = StringEditDistance.levenshteinDistance(first, second);
        assertEquals(4, dist);
    }
    
    
}
