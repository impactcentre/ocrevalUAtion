/*
 * Copyright (C) 2014 IMPACT Centre of Competence
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

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author carrasco@ua.es
 */
public class WordTypeTest {

    @Test
    public void testTypeOf() {
        System.out.println("typeOf");
        assertEquals(WordType.UPPERCASE, WordType.typeOf("UP"));
        assertEquals(WordType.LOWERCASE, WordType.typeOf("low"));
        assertEquals(WordType.MIXED, WordType.typeOf("LaTeX"));
        assert (WordType.nearlyUpper("MIsTAKE"));
        assert (WordType.nearlyUpper("MIs"));
        assert (!WordType.nearlyUpper("La"));
        assert (WordType.initial("L"));
        assert (WordType.initial("La"));
        assert (!WordType.initial("MIs"));
    }
}