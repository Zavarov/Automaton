/*
 * Copyright (C) 2018 Zavarov
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
package vartas.automaton;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import vartas.automaton.Tokenizer.Builder;
import vartas.automaton.Tokenizer.Token;

/**
 *
 * @author Zavarov
 */
public class TokenizerTest {
    Tokenizer tokenizer;
    RegularExpression.Parser parser;
    Builder builder;
    @Before
    public void setUp(){
        parser = new RegularExpression.Parser();
        builder = new Builder();
        builder.addExpression(parser.parse("ab"));
        builder.addExpression(parser.parse("ba"));
        tokenizer = builder.build();
    }
    
    @Test
    public void emptyTokenTest(){
        builder.addExpression(parser.parse("ac"));
        tokenizer = builder.build();
        tokenizer.setInput("c");
        assertEquals(tokenizer.nextToken().getLeft(),"c");
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void setInputIllegalArgumentExceptionTest(){
        tokenizer.setInput(null);
    }
    
    @Test
    public void setInputTest(){
        assertNull(tokenizer.next_letter);
        assertNull(tokenizer.data);
        tokenizer.setInput("ab");
        assertEquals(tokenizer.next_letter.intValue(),(int)'a');
        assertNotNull(tokenizer.data);
    }
    
    @Test
    public void hasNextTest(){
        tokenizer.setInput("");
        assertFalse(tokenizer.hasNext());
        tokenizer.setInput("ab");
        assertTrue(tokenizer.hasNext());
    }
    
    @Test
    public void nextTokenTest(){
        tokenizer.setInput("abbba");
        assertEquals(tokenizer.nextToken(),new Token("ab",null));
        assertEquals(tokenizer.nextToken(),new Token("b",null));
        assertEquals(tokenizer.nextToken(),new Token("ba",null));
        assertFalse(tokenizer.hasNext());
    }
    
    @Test
    public void nextTokenInvalidLetterTest(){
        tokenizer.setInput("abaxaabxbax");
        assertEquals(tokenizer.nextToken().getLeft(),"ab");
        assertEquals(tokenizer.nextToken().getLeft(),"a");
        assertEquals(tokenizer.nextToken().getLeft(),"x");
        assertEquals(tokenizer.nextToken().getLeft(),"a");
        assertEquals(tokenizer.nextToken().getLeft(),"ab");
        assertEquals(tokenizer.nextToken().getLeft(),"x");
        assertEquals(tokenizer.nextToken().getLeft(),"ba");
        assertEquals(tokenizer.nextToken().getLeft(),"x");
        assertFalse(tokenizer.hasNext());
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void nextTokenIllegalArgumentExceptionTest(){
        tokenizer.setInput("");
        tokenizer.nextToken();
    }
    
    @Test(expected=IllegalStateException.class)
    public void hasNextIllegalStateExceptionTest(){
        tokenizer.hasNext();
    }
}