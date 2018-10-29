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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Zavarov
 */
public class LexerBuilderTest {
    RegularExpression.Parser parser;
    Lexer.Builder builder;
    List<String> list = new ObjectArrayList<>();
    @Before
    public void setUp(){
        parser = new RegularExpression.Parser();
        builder = new Lexer.Builder();
    }
    @Test
    public void addExpressionTest(){
        assertTrue(builder.tokenizer.expressions.isEmpty());
        assertTrue(builder.identifier.isEmpty());
        builder.addExpression(parser.parse("ab"), "ab");
        assertFalse(builder.tokenizer.expressions.isEmpty());
        assertEquals(builder.identifier,Arrays.asList("ab"));
    }
    @Test
    public void buildTest(){
        builder.addExpression(parser.parse("abc"), "abc");
        builder.addExpression(parser.parse("cde"), "cde");
        Lexer lexer = builder.build();
        lexer.setInput("abccde");
        assertEquals(lexer.nextToken().getLeft(),"abc");
        assertEquals(lexer.nextToken().getLeft(),"cde");
    }
}
