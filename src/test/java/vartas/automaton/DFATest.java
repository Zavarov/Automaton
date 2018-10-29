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

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Set;
import org.apache.commons.lang3.tuple.Triple;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import vartas.automaton.FiniteAutomaton.StateSet;

/**
 *
 * @author Zavarov
 */
public class DFATest {
    int s0,s1,s2,s3,s4;
    int a;
    int b;
    DFA dfa;
    @Before
    public void setUp(){
        DFA.Builder builder = new DFA.Builder();
        s0 = builder.addInitialState();
        s1 = builder.addState();
        s2 = builder.addState();
        s3 = builder.addFinalState();
        s4 = builder.addState();
        
        builder.addTransition(0, 3, "a");
        builder.addTransition(0, 1, "b");
        builder.addTransition(1, 2, "a");
        builder.addTransition(1, 4, "b");
        builder.addTransition(2, 3, "a");
        builder.addTransition(2, 4, "b");
        builder.addTransition(3, 4, "a");
        builder.addTransition(3, 0, "b");
        builder.addTransition(4, 4, "a");
        builder.addTransition(4, 4, "b");
        dfa = builder.build();
        
        a = builder.alphabet.get("a");
        b = builder.alphabet.get("b");
    }
    
    @Test
    public void runTest(){
        assertTrue(dfa.run(Arrays.asList(a)));
        assertTrue(dfa.run(Arrays.asList(a,b,a)));
        assertTrue(dfa.run(Arrays.asList(b,a,a)));
        assertFalse(dfa.run(Arrays.asList()));
        assertFalse(dfa.run(Arrays.asList(b)));
        assertFalse(dfa.run(Arrays.asList(b,a)));
        assertFalse(dfa.run(Arrays.asList(b,a,a,b)));
        assertFalse(dfa.run(Arrays.asList(a,b)));
    }
    
    @Test
    public void stepTest(){
        assertEquals(dfa.step(new StateSet(0), a),Sets.newHashSet(3));
        assertEquals(dfa.step(new StateSet(0), b),Sets.newHashSet(1));
        assertEquals(dfa.step(new StateSet(1), a),Sets.newHashSet(2));
        assertEquals(dfa.step(new StateSet(2), a),Sets.newHashSet(3));
        assertEquals(dfa.step(new StateSet(3), b),Sets.newHashSet(0));
    }
    
    @Test(expected=MissingTransitionException.class)
    public void stepMissingTransitionExceptionTest(){
        dfa.step(new StateSet(-1), -1);
    }
    
    @Test
    public void reverseTest(){
        NFA nfa = dfa.reverse();
        
        assertEquals(nfa.final_states,dfa.initial_states);
        assertEquals(nfa.initial_states,dfa.final_states);
        nfa.transitions.cellSet().forEach(cell -> {
            cell.getValue().forEach(to -> {
                assertTrue(dfa.transitions.contains(to, cell.getColumnKey()));
                assertEquals(dfa.transitions.get(to, cell.getColumnKey()),cell.getRowKey());
            });
        });
        
        assertTrue(nfa.run(Arrays.asList(a)));
        assertTrue(nfa.run(Arrays.asList(a,b,a)));
        assertFalse(nfa.run(Arrays.asList(b,a,a)));
        assertTrue(nfa.run(Arrays.asList(a,a,b)));
    }
    
    @Test
    public void minimizeTest(){
        DFA.Builder builder = new DFA.Builder();
        s0 = builder.addInitialState();
        s1 = builder.addFinalState();
        s2 = builder.addFinalState();
        s3 = builder.addFinalState();
        builder.makeFinalState(s0);
        
        builder.addTransition(0, 1, "a");
        builder.addTransition(1, 2, "a");
        builder.addTransition(2, 3, "a");
        builder.addTransition(3, 0, "a");
        dfa = builder.build();
        int a = builder.alphabet.get("a");
        
        dfa = dfa.minimize();
        assertEquals(dfa.transitions.size(),1);
        assertTrue(dfa.transitions.contains(0, 0));
        assertEquals(dfa.transitions.get(0, 0).intValue(),a);
    }
    
    @Test
    public void isEmptyTest(){
        assertFalse(dfa.isEmpty());
    }
    
    
    
    @Test
    public void complementTest(){
        dfa = dfa.complement();
        assertFalse(dfa.run(Arrays.asList(a)));
        assertFalse(dfa.run(Arrays.asList(a,b,a)));
        assertFalse(dfa.run(Arrays.asList(b,a,a)));
        assertTrue(dfa.run(Arrays.asList()));
        assertTrue(dfa.run(Arrays.asList(b)));
        assertTrue(dfa.run(Arrays.asList(b,a)));
        assertTrue(dfa.run(Arrays.asList(b,a,a,b)));
        assertTrue(dfa.run(Arrays.asList(a,b)));
    }
    
    @Test
    public void transitionSetTest(){
        Set<Triple<Integer,Integer,Integer>> transitions = dfa.transitionSet();
        assertEquals(transitions.size(),10);
        assertTrue(transitions.contains(Triple.of(s0, a, s3)));
        assertTrue(transitions.contains(Triple.of(s0, b, s1)));
        assertTrue(transitions.contains(Triple.of(s1, a, s2)));
        assertTrue(transitions.contains(Triple.of(s1, b, s4)));
        assertTrue(transitions.contains(Triple.of(s2, a, s3)));
        assertTrue(transitions.contains(Triple.of(s2, b, s4)));
        assertTrue(transitions.contains(Triple.of(s3, a, s4)));
        assertTrue(transitions.contains(Triple.of(s3, b, s0)));
        assertTrue(transitions.contains(Triple.of(s4, a, s4)));
        assertTrue(transitions.contains(Triple.of(s4, b, s4)));
    }
}
