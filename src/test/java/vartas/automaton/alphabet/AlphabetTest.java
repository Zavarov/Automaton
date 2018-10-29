/*
 * Copyright (C) 2017 Zavarov
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package vartas.automaton.alphabet;

import java.util.Iterator;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Zavarov
 */
public class AlphabetTest {
    Alphabet alphabet;
    @Before
    public void setUp(){
        alphabet = new Alphabet();
    }
    @Test
    public void addTest(){
        assertEquals(alphabet.data.size(),0);
        assertTrue(alphabet.add("word"));
        assertEquals(alphabet.data.size(),1);
    }
    @Test
    public void addReusabelTest(){
        alphabet.reusable.push(1000);
        assertEquals(alphabet.data.size(),0);
        assertTrue(alphabet.add("word"));
        assertEquals(alphabet.data.size(),1);
        assertTrue(alphabet.data.containsValue(1000));
        
    }
    @Test
    public void addFullTest(){
        alphabet.next_id = Alphabet.LAST_ID+1;
        assertFalse(alphabet.add("word"));
    }
    @Test
    public void addFullReusableTest(){
        alphabet.reusable.push(1000);
        alphabet.next_id = Alphabet.LAST_ID+1;
        assertEquals(alphabet.data.size(),0);
        assertTrue(alphabet.add("word"));
        assertEquals(alphabet.data.size(),1);
        assertTrue(alphabet.data.containsValue(1000));
    }
    @Test
    public void addTwiceTest(){
        assertEquals(alphabet.data.size(),0);
        assertTrue(alphabet.add("word"));
        assertFalse(alphabet.add("word"));
        assertEquals(alphabet.data.size(),1);
    }
    @Test
    public void iteratorTest(){
        alphabet.add("word");
        assertFalse(alphabet.data.isEmpty());
        assertTrue(alphabet.reusable.isEmpty());
        
        Iterator<String> iterator = alphabet.iterator();
        assertTrue(iterator.hasNext());
        assertEquals(iterator.next(),"word");
        iterator.remove();
        
        assertTrue(alphabet.data.isEmpty());
        assertFalse(alphabet.reusable.isEmpty());
    }
    @Test
    public void sizeTest(){
        assertEquals(alphabet.size(),0);
        assertTrue(alphabet.add("word"));
        assertEquals(alphabet.size(),1);
    }
    @Test
    public void idsTest(){
        assertTrue(alphabet.add("word1"));
        assertTrue(alphabet.add("word2"));
        assertTrue(alphabet.add("word3"));
        assertTrue(alphabet.add("word4"));
        assertEquals(alphabet.ids().size(),4);
        assertTrue(alphabet.data.values().containsAll(alphabet.ids()));
    }
    @Test
    public void getIdTest(){
        assertTrue(alphabet.add("word"));
        assertEquals("word",alphabet.get(alphabet.data.values().iterator().next()));
        
    }
    @Test(expected=IdNotInAlphabetException.class)
    public void getIdIdNotInAlphabetExceptionTest(){
        alphabet.get(0);
    }
    @Test
    public void getStringTest(){
        assertTrue(alphabet.add("word"));
        assertEquals(alphabet.get("word"),alphabet.data.values().iterator().next().intValue());
    }
    @Test(expected=WordNotInAlphabetException.class)
    public void getStringWordNotInAlphabetExceptionTest(){
        alphabet.get("word");
    }
}
