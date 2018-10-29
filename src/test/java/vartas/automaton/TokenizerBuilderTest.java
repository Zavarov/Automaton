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
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import vartas.automaton.alphabet.Alphabet;

/**
 *
 * @author Zavarov
 */
public class TokenizerBuilderTest {
    
    RegularExpression.Parser parser;
    Tokenizer.Builder builder;
    Tokenizer tokenizer;
    Alphabet alphabet;
    List<String> list = new ObjectArrayList<>();
    int a,b,c,d,e;
    @Before
    public void setUp(){
        alphabet = new Alphabet();
        parser = new RegularExpression.Parser();
        builder = new Tokenizer.Builder();
        a = 'a';
        b = 'b';
        c = 'c';
        d = 'd';
        e = 'e';
    }
    
    @Test
    public void addExpressionTest(){
        assertTrue(builder.expressions.isEmpty());
        builder.addExpression(parser.parse("abc"));
        assertFalse(builder.expressions.isEmpty());
    }
    
    @Test
    public void buildTest(){
        builder.addExpression(parser.parse("(abc)*"));
        builder.addExpression(parser.parse("cde"));
        tokenizer = builder.build();

        assertTrue(tokenizer.run(Arrays.asList(a,b,c)));
        assertTrue(tokenizer.run(Arrays.asList(a,b,c,a,b,c)));
        assertFalse(tokenizer.run(Arrays.asList(a,b,c,c,a,b,c)));
        assertTrue(tokenizer.run(Arrays.asList(c,d,e)));
        assertFalse(tokenizer.run(Arrays.asList(c,c,d,e)));
        assertFalse(tokenizer.run(Arrays.asList(c,d,d,e)));
        assertFalse(tokenizer.run(Arrays.asList(a,b,c,c,d,e)));
    }
}
