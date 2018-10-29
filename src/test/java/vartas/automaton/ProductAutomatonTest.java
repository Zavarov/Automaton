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
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import vartas.automaton.ProductAutomaton.Builder;
import vartas.automaton.ProductAutomaton.Comparator;
import vartas.automaton.RegularExpression.Parser;

/**
 *
 * @author Zavarov
 */
public class ProductAutomatonTest {
    ProductAutomaton automaton;
    Builder builder;
    Parser parser;
    int a,b,c;
    @Before
    public void setUp(){
        builder = new Builder();
        a = 'a';
        b = 'b';
        c = 'c';
        parser = new Parser();
    }
    
    @Test
    public void andTest(){
        builder.addAutomaton(parser.parse("ab+c").determinize());
        builder.addAutomaton(parser.parse("ba+c").determinize());
        builder.setComparator(Comparator.AND);
        automaton = builder.build();
        assertTrue(automaton.run(Arrays.asList(c)));
        assertFalse(automaton.run(Arrays.asList(a)));
        assertFalse(automaton.run(Arrays.asList(b)));
        assertFalse(automaton.run(Arrays.asList(a,a)));
        assertFalse(automaton.run(Arrays.asList(b,b)));
        assertFalse(automaton.run(Arrays.asList(a,b)));
        assertFalse(automaton.run(Arrays.asList(b,a)));
    }
    
    @Test
    public void orTest(){
        builder.addAutomaton(parser.parse("ab+c").determinize());
        builder.addAutomaton(parser.parse("ba+c").determinize());
        builder.setComparator(Comparator.OR);
        automaton = builder.build();
        assertTrue(automaton.run(Arrays.asList(c)));
        assertTrue(automaton.run(Arrays.asList(a,b)));
        assertTrue(automaton.run(Arrays.asList(b,a)));
        assertFalse(automaton.run(Arrays.asList(a)));
        assertFalse(automaton.run(Arrays.asList(b)));
        assertFalse(automaton.run(Arrays.asList(a,a)));
        assertFalse(automaton.run(Arrays.asList(b,b)));
    }
    
    @Test
    public void xandTest(){
        builder.addAutomaton(parser.parse("ab+c").determinize());
        builder.addAutomaton(parser.parse("ba+c").determinize());
        builder.setComparator(Comparator.XAND);
        automaton = builder.build();
        assertTrue(automaton.run(Arrays.asList(a)));
        assertTrue(automaton.run(Arrays.asList(b)));
        assertTrue(automaton.run(Arrays.asList(c)));
        assertTrue(automaton.run(Arrays.asList(a,a)));
        assertTrue(automaton.run(Arrays.asList(b,b)));
        assertFalse(automaton.run(Arrays.asList(a,b)));
        assertFalse(automaton.run(Arrays.asList(b,a)));
    }
    
    @Test
    public void xorTest(){
        builder.addAutomaton(parser.parse("ab+c").determinize());
        builder.addAutomaton(parser.parse("ba+c").determinize());
        builder.setComparator(Comparator.XOR);
        automaton = builder.build();        
        assertTrue(automaton.run(Arrays.asList(a,b)));
        assertTrue(automaton.run(Arrays.asList(b,a)));
        assertFalse(automaton.run(Arrays.asList(c)));
    }
    
    @Test
    public void notTest(){
        builder.addAutomaton(parser.parse("ab").determinize());
        builder.addAutomaton(parser.parse("cc").determinize());
        builder.setComparator(Comparator.NOT);
        automaton = builder.build();        
        assertTrue(automaton.run(Arrays.asList(a,c)));
        assertTrue(automaton.run(Arrays.asList(b,a)));
        assertTrue(automaton.run(Arrays.asList(c,b)));
        assertTrue(automaton.run(Arrays.asList(a)));
        assertTrue(automaton.run(Arrays.asList(b)));
        assertTrue(automaton.run(Arrays.asList(c)));
        assertTrue(automaton.run(Arrays.asList()));
        assertFalse(automaton.run(Arrays.asList(a,b)));
        assertFalse(automaton.run(Arrays.asList(c,c)));
    }
    @Test
    public void valuesTest(){
        List<Comparator> values = Arrays.asList(Comparator.values());
        assertTrue(values.contains(Comparator.AND));
        assertTrue(values.contains(Comparator.NOT));
        assertTrue(values.contains(Comparator.OR));
        assertTrue(values.contains(Comparator.XAND));
        assertTrue(values.contains(Comparator.XOR));
        assertEquals(values.size(),5);
    }
    @Test
    public void valuesOfTest(){
        assertEquals(Comparator.valueOf("AND"),Comparator.AND);
        assertEquals(Comparator.valueOf("NOT"),Comparator.NOT);
        assertEquals(Comparator.valueOf("OR"),Comparator.OR);
        assertEquals(Comparator.valueOf("XAND"),Comparator.XAND);
        assertEquals(Comparator.valueOf("XOR"),Comparator.XOR);
    }
}
