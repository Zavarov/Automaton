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

import java.util.Arrays;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import vartas.automaton.RegularExpression.ParserException;

/**
 *
 * @author Zavarov
 */
public class RegularExpressionParserTest {
    RegularExpression.Parser parser;
    @Before
    public void setUp(){
        parser = new RegularExpression.Parser();
    }
    
    @Test
    public void parseTest(){
        RegularExpression expression = parser.parse("ab+(c*d)*");
        int a = 'a';
        int b = 'b';
        int c = 'c';
        int d = 'd';
        assertTrue(expression.run(Arrays.asList()));
        assertTrue(expression.run(Arrays.asList(a,b)));
        assertTrue(expression.run(Arrays.asList(d)));
        assertTrue(expression.run(Arrays.asList(c,d)));
        assertTrue(expression.run(Arrays.asList(c,c,c,c,d)));
        assertTrue(expression.run(Arrays.asList(c,c,c,c,d,c,d,c,c,c,d,c,d,c,c,c,c,c,d)));
        assertFalse(expression.run(Arrays.asList(a)));
        assertFalse(expression.run(Arrays.asList(b)));
        assertFalse(expression.run(Arrays.asList(c)));
    }
    
    @Test
    public void keywordTest(){
        RegularExpression expression = parser.parse("\\\\");
        int letter = '\\';
        assertTrue(expression.run(Arrays.asList(letter)));
        
        expression = parser.parse("\\+");
        letter = '+';
        assertTrue(expression.run(Arrays.asList(letter)));
        
        expression = parser.parse("\\*");
        letter = '*';
        assertTrue(expression.run(Arrays.asList(letter)));
        
        expression = parser.parse("\\(");
        letter = '(';
        assertTrue(expression.run(Arrays.asList(letter)));
        
        expression = parser.parse("\\)");
        letter = ')';
        assertTrue(expression.run(Arrays.asList(letter)));
    }
    
    @Test(expected=ParserException.class)
    public void peekTokenTest(){
        parser.expression = "";
        parser.peekToken();
    }
    
    @Test(expected=ParserException.class)
    public void nextTokenTest(){
        parser.expression = "";
        parser.nextToken();
    }
    
    @Test
    public void hasNext(){
        parser.expression = "";
        assertFalse(parser.hasNext());
    }
    
    @Test(expected=ParserException.class)
    public void keywordInvalidTest(){
        parser.parse("\\a");
    }
    
    @Test(expected=ParserException.class)
    public void keywordTooShortTest(){
        parser.parse("\\");
    }
    
    @Test(expected=ParserException.class)
    public void regularExpressionInvalidStartTest(){
        parser.parse("+");
    }
    
    @Test(expected=ParserException.class)
    public void kleeneExpressionMissingEndTest(){
        parser.parse("(a");
    }
    
    @Test
    public void parseEpsilonTest(){
        RegularExpression expression = parser.parse("");
        assertTrue(expression.run(Arrays.asList()));
    }
}
