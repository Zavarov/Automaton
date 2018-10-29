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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import vartas.automaton.ProductAutomaton.Comparator;

/**
 *
 * @author Zavarov
 */
public class ProductAutomatonBuilderTest {
    ProductAutomaton.Builder product;
    DFA.Builder builder;
    DFA first,second;
    int a,b,c,d;
    @Before
    public void setUp(){
        a = 0;
        b = 1;
        c = 2;
        d = 3;
        
        builder = new DFA.Builder();
        int s0 = builder.addInitialState();
        int s1 = builder.addFinalState();
        int s2 = builder.addFinalState();
        builder.addTransition(s0, s1, a);
        builder.addTransition(s1, s2, b);
        first = builder.build();
        
        
        builder = new DFA.Builder();
        
        s0 = builder.addInitialState();
        s1 = builder.addFinalState();
        builder.addTransition(s0, s1, a);
        s1 = builder.addFinalState();
        builder.addTransition(s0, s1, b);
        s1 = builder.addState();
        builder.addTransition(s0, s1, c);
        s1 = builder.addState();
        builder.addTransition(s0, s1, d);
        
        s0 = builder.addInitialState();
        s1 = builder.addFinalState();
        builder.addTransition(s0, s1, a);
        s1 = builder.addState();
        builder.addTransition(s0, s1, b);
        s1 = builder.addFinalState();
        builder.addTransition(s0, s1, c);
        s1 = builder.addState();
        builder.addTransition(s0, s1, d);
        
        second = builder.build();
        
        product = new ProductAutomaton.Builder();
    }
    @Test
    public void addAutomatonTest(){
        product.addAutomaton(first);
        product.addAutomaton(second);
        
        assertEquals(product.automatons.size(),2);
        assertEquals(product.automatons.get(0),first);
        assertEquals(product.automatons.get(1),second);
    }
    @Test
    public void setComparatorTest(){
        assertNotEquals(product.comparator,Comparator.XOR);
        product.setComparator(Comparator.XOR);
        assertEquals(product.comparator,Comparator.XOR);
    }
    @Test(expected=IllegalArgumentException.class)
    public void setComparatorNullTest(){
        product.setComparator(null);
    }
    @Test
    public void buildTest(){
        product.addAutomaton(first);
        product.addAutomaton(second);
        
        ProductAutomaton automaton = product.build();
        assertTrue(automaton.run(Arrays.asList(a)));
        assertFalse(automaton.run(Arrays.asList(b)));
        assertFalse(automaton.run(Arrays.asList(c)));
        assertFalse(automaton.run(Arrays.asList(d)));
        assertFalse(automaton.run(Arrays.asList(a,b)));
    }
    @Test
    public void computeStateTest(){
        DFA.Builder dfa = new DFA.Builder();
        product.addAutomaton(first);
        
        int i = product.computeState(Arrays.asList(first.initial_states), dfa);
        assertFalse(dfa.final_states.contains(i));
        i = product.computeState(Arrays.asList(first.final_states), dfa);
        assertTrue(dfa.final_states.contains(i));
    }
}
