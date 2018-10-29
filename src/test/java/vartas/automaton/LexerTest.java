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

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import org.junit.Before;
import org.junit.Test;
import vartas.automaton.Tokenizer.Token;

/**
 *
 * @author Zavarov
 */
public class LexerTest {
    RegularExpression.Parser parser;
    Lexer.Builder builder;
    Lexer lexer;
    List<String> list = new ObjectArrayList<>();
    @Before
    public void setUp(){
        parser = new RegularExpression.Parser();
        builder = new Lexer.Builder();
        
        builder.addExpression(parser.parse("(abcd)*"), "word");
        builder.addExpression(parser.parse("(1+2+3+4+5+6+7+8+9)(0+1+2+3+4+5+6+7+8+9)*"), "number");
        builder.addExpression(parser.parse("\\("), "parenthesis");
        lexer = builder.build();
    }
    @Test
    public void lexerInvalidEndTest(){
        lexer.setInput("xxabcdxabx( 129424abcdabcd(xx");
        Token token;
        
        token = lexer.nextToken();
        assertEquals(token.getLeft(),"x");
        assertNull(token.getRight());
        
        token = lexer.nextToken();
        assertEquals(token.getLeft(),"x");
        assertNull(token.getRight());
        
        token = lexer.nextToken();
        assertEquals(token.getLeft(),"abcd");
        assertEquals(token.getRight(),"word");
        
        token = lexer.nextToken();
        assertEquals(token.getLeft(),"x");
        assertNull(token.getRight());
        
        token = lexer.nextToken();
        assertEquals(token.getLeft(),"ab");
        assertNull(token.getRight());
        
        token = lexer.nextToken();
        assertEquals(token.getLeft(),"x");
        assertNull(token.getRight());
        
        token = lexer.nextToken();
        assertEquals(token.getLeft(),"(");
        assertEquals(token.getRight(),"parenthesis");
        
        token = lexer.nextToken();
        assertEquals(token.getLeft()," ");
        assertNull(token.getRight());
        
        token = lexer.nextToken();
        assertEquals(token.getLeft(),"129424");
        assertEquals(token.getRight(),"number");
        
        token = lexer.nextToken();
        assertEquals(token.getLeft(),"abcdabcd");
        assertEquals(token.getRight(),"word");
        
        token = lexer.nextToken();
        assertEquals(token.getLeft(),"(");
        assertEquals(token.getRight(),"parenthesis");
        
        token = lexer.nextToken();
        assertEquals(token.getLeft(),"x");
        assertNull(token.getRight());
        
        token = lexer.nextToken();
        assertEquals(token.getLeft(),"x");
        assertNull(token.getRight());
        
        assertFalse(lexer.hasNext());
    }
    @Test
    public void lexerValidEndTest(){
        lexer.setInput("xxabcdxx( 129424abcdxx(");
        Token token;
        
        token = lexer.nextToken();
        assertEquals(token.getLeft(),"x");
        assertNull(token.getRight());
        
        token = lexer.nextToken();
        assertEquals(token.getLeft(),"x");
        assertNull(token.getRight());
        
        token = lexer.nextToken();
        assertEquals(token.getLeft(),"abcd");
        assertEquals(token.getRight(),"word");
        
        token = lexer.nextToken();
        assertEquals(token.getLeft(),"x");
        assertNull(token.getRight());
        
        token = lexer.nextToken();
        assertEquals(token.getLeft(),"x");
        assertNull(token.getRight());
        
        token = lexer.nextToken();
        assertEquals(token.getLeft(),"(");
        assertEquals(token.getRight(),"parenthesis");
        
        token = lexer.nextToken();
        assertEquals(token.getLeft()," ");
        assertNull(token.getRight());
        
        token = lexer.nextToken();
        assertEquals(token.getLeft(),"129424");
        assertEquals(token.getRight(),"number");
        
        token = lexer.nextToken();
        assertEquals(token.getLeft(),"abcd");
        assertEquals(token.getRight(),"word");
        
        token = lexer.nextToken();
        assertEquals(token.getLeft(),"x");
        assertNull(token.getRight());
        
        token = lexer.nextToken();
        assertEquals(token.getLeft(),"x");
        assertNull(token.getRight());
        
        token = lexer.nextToken();
        assertEquals(token.getLeft(),"(");
        assertEquals(token.getRight(),"parenthesis");
        
        assertFalse(lexer.hasNext());
    }
}
