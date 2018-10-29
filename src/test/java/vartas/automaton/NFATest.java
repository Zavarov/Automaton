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
import java.util.Set;
import org.apache.commons.lang3.tuple.Triple;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import vartas.automaton.FiniteAutomaton.StateSet;
import vartas.automaton.alphabet.Alphabet;

/**
 *
 * @author Zavarov
 */
public class NFATest {
    NFA nfa;
    int s0,s1,s2,s3,s4,s5,s6,s7,s8;
    int a,b,c;
    @Before
    public void setUp(){
        NFA.Builder builder = new NFA.Builder();
        s0 = builder.addInitialState();
        s1 = builder.addState();
        s2 = builder.addState();
        s3 = builder.addFinalState();
        s4 = builder.addState();
        s5 = builder.addState();
        s6 = builder.addState();
        s7 = builder.addState();
        s8 = builder.addFinalState();
        
        builder.addTransition(s0, s1, "a");
        builder.addTransition(s1, s2, "b");
        builder.addTransition(s2, s3, "c");
        
        builder.addEpsilonTransition(s0, s4);
        builder.addTransition(s4, s5, "a");
        builder.addEpsilonTransition(s5, s6);
        builder.addEpsilonTransition(s6, s7);
        builder.addTransition(s7, s8, "a");
        nfa = builder.build();
        a = builder.alphabet.get("a");
        b = builder.alphabet.get("b");
        c = builder.alphabet.get("c");
    }
    @Test
    public void stepTest(){
        Set<Integer> set = nfa.step(new StateSet(s1), b);
        assertEquals(set.size(),1);
        assertTrue(set.contains(s2));
        set = nfa.step(new StateSet(s0), Alphabet.EPSILON);
        assertEquals(set.size(),2);
        assertTrue(set.contains(s0));
        assertTrue(set.contains(s4));
    }
    @Test
    public void stepClosureTest(){
        StateSet set = nfa.step(new StateSet(s4),a);
        assertEquals(set.size(),3);
        assertTrue(set.contains(s5));
        assertTrue(set.contains(s6));
        assertTrue(set.contains(s7));
    }
    @Test
    public void runTest(){
        assertTrue(nfa.run(Arrays.asList(a,b,c)));
    }
    @Test
    public void runEpsilonTest(){
        assertTrue(nfa.run(Arrays.asList(a,a)));
        
    }
    @Test
    public void runEmptyWordTest(){
        NFA.Builder builder = new NFA.Builder();
        s0 = builder.addInitialState();
        builder.makeFinalState(s0);
        nfa = builder.build();
        assertTrue(nfa.run(Arrays.asList()));
    }
    @Test
    public void runFailureTest(){
        assertFalse(nfa.run(Arrays.asList(a)));
        assertFalse(nfa.run(Arrays.asList()));
        assertFalse(nfa.run(Arrays.asList(a,b)));
    }
    @Test
    public void runMissingTransitionTest(){
        assertFalse(nfa.run(Arrays.asList(Integer.MAX_VALUE)));
    }
    @Test
    public void closureTest(){
        Set<Integer> set = nfa.closure(new StateSet(s1));
        assertEquals(set.size(),1);
        assertTrue(set.contains(s1));
        
        set = nfa.closure(new StateSet(s5));
        assertEquals(set.size(),3);
        assertTrue(set.contains(s5));
        assertTrue(set.contains(s6));
        assertTrue(set.contains(s7));
    }
    @Test
    public void determinizeTest(){
        NFA.Builder builder = new NFA.Builder();
        s0 = builder.addInitialState();
        s1 = builder.addState();
        s2 = builder.addFinalState();
        builder.makeFinalState(s0);
        builder.addEpsilonTransition(s0, s2);
        builder.addTransition(s0, s1, "b");
        builder.addTransition(s1, s1, "a");
        builder.addTransition(s1, s2, "a");
        builder.addTransition(s1, s2, "b");
        builder.addTransition(s2, s0, "a");
        a = builder.alphabet.get("a");
        b = builder.alphabet.get("b");
        
        DFA dfa = builder.build().determinize();
        assertEquals(dfa.final_states.size(),4);
        assertEquals(dfa.initial_states.size(),1);
        
        assertEquals(dfa.transitions.size(),12);
        assertTrue(dfa.final_states.contains(s0));
        
        assertTrue(dfa.run(Arrays.asList()));
        assertTrue(dfa.run(Arrays.asList(a)));
        assertTrue(dfa.run(Arrays.asList(a,a)));
        assertTrue(dfa.run(Arrays.asList(b,b)));
        assertTrue(dfa.run(Arrays.asList(b,b,a)));
        assertTrue(dfa.run(Arrays.asList(b,a,a)));
        assertTrue(dfa.run(Arrays.asList(b,a,a,b,a)));
        assertTrue(dfa.run(Arrays.asList(a,b,b,a,a,b,b,a,b,b,a)));
        assertTrue(dfa.run(Arrays.asList(a,b,b,a,b,a,b,a,b,a,a,b,a,a)));
        assertFalse(dfa.run(Arrays.asList(b)));
        assertFalse(dfa.run(Arrays.asList(b,b,a,b)));
        assertFalse(dfa.run(Arrays.asList(b,a,b,a,b)));
        assertFalse(dfa.run(Arrays.asList(a,b,a,a,b,b,a,b)));
    }
    @Test
    public void determinizeLoopTest(){
        NFA.Builder builder = new NFA.Builder();
        s0 = builder.addState();
        s1 = builder.addState();
        s2 = builder.addState();
        s3 = builder.addFinalState();
        s4 = builder.addInitialState();
        s5 = builder.addFinalState();
        builder.addEpsilonTransition(s4, s0);
        builder.addEpsilonTransition(s4, s5);
        builder.addTransition(s0, s1, "a");
        builder.addEpsilonTransition(s1, s2);
        builder.addTransition(s2, s3, "b");
        builder.addEpsilonTransition(s3, s0);
        builder.addEpsilonTransition(s3, s5);
        a = builder.alphabet.get("a");
        b = builder.alphabet.get("b");
        DFA dfa = builder.build().determinize();
        assertTrue(dfa.run(Arrays.asList()));
        assertTrue(dfa.run(Arrays.asList(a,b)));
        assertTrue(dfa.run(Arrays.asList(a,b,a,b)));
        assertFalse(dfa.run(Arrays.asList(a)));
        assertFalse(dfa.run(Arrays.asList(a,b,a)));
    }
    @Test
    public void closureNotDisjointTest(){
        NFA.Builder builder = new NFA.Builder();
        s0 = builder.addState();
        s1 = builder.addState();
        s2 = builder.addState();
        s3 = builder.addFinalState();
        builder.addTransition(s0, s1, "a");
        builder.addTransition(s0, s0, "b");
        builder.addEpsilonTransition(s0, s0);
        builder.addEpsilonTransition(s1, s0);
        
        builder.addTransition(s2, s2, "a");
        builder.addTransition(s2, s3, "b");
        builder.addEpsilonTransition(s2, s2);
        builder.addEpsilonTransition(s3, s2);
        
        builder.addEpsilonTransition(s2, s0);
        builder.addEpsilonTransition(s3, s0);
        builder.addEpsilonTransition(s0, s2);
        builder.addEpsilonTransition(s0, s3);
        
        NFA nfa = builder.build();
        assertEquals(nfa.closure(new StateSet(s0,s3)),new StateSet(s0,s2,s3));
    }
    @Test
    public void minimizeTest(){
        NFA.Builder builder = new NFA.Builder();
        int s0 = builder.addInitialState();
        builder.makeFinalState(s0);
        int i1 = builder.addFinalState();
        int j1 = builder.addFinalState();
        int i0 = builder.addState();
        int j0 = builder.addState();
        builder.addTransition(s0, i0, "a");
        builder.addTransition(i0, s0, "a");
        builder.addTransition(i0, i1, "a");
        builder.addTransition(i1, i0, "a");
        
        builder.addTransition(s0, j0, "b");
        builder.addTransition(j0, s0, "b");
        builder.addTransition(j0, j1, "b");
        builder.addTransition(j1, j0, "b");
        int a = builder.alphabet.get("a");
        int b = builder.alphabet.get("b");
        
        DFA dfa = builder.build().minimize();
        
        assertEquals(dfa.transitions.size(),8);
        assertFalse(dfa.run(Arrays.asList(a)));
        assertFalse(dfa.run(Arrays.asList(a,a,a)));
        assertFalse(dfa.run(Arrays.asList(a,a,a,b)));
        assertFalse(dfa.run(Arrays.asList(b)));
        assertFalse(dfa.run(Arrays.asList(b,b,b)));
        assertFalse(dfa.run(Arrays.asList(b,b,b,a)));
        assertTrue(dfa.run(Arrays.asList()));
        assertTrue(dfa.run(Arrays.asList(a,a)));
        assertTrue(dfa.run(Arrays.asList(b,b)));
        assertTrue(dfa.run(Arrays.asList(b,b,a,a)));
        assertTrue(dfa.run(Arrays.asList(a,a,b,b)));
        assertTrue(dfa.run(Arrays.asList(a,a,a,a)));
    }
    
    @Test
    public void isEmptyTest(){
        assertFalse(nfa.isEmpty());
    }
    
    @Test
    public void complementTest(){
        DFA dfa = nfa.complement();
        assertFalse(dfa.run(Arrays.asList(a,b,c)));
    }
    
    @Test
    public void reverseTest(){
        NFA reverse = nfa.reverse();
        
        assertEquals(reverse.final_states,nfa.initial_states);
        assertEquals(reverse.initial_states,nfa.final_states);
        
        reverse.transitions.cellSet().forEach(cell -> {
            cell.getValue().forEach(to -> {
                assertTrue(nfa.transitions.contains(to, cell.getColumnKey()));
                assertTrue(nfa.transitions.get(to, cell.getColumnKey()).contains(cell.getRowKey()));
            });
        });
        assertTrue(reverse.run(Arrays.asList(c,b,a)));
    }
    
    @Test
    public void transitionSetTest(){
        Set<Triple<Integer,Integer,Integer>> transitions = nfa.transitionSet();
        assertEquals(transitions.size(),8);
        assertTrue(transitions.contains(Triple.of(s0, a, s1)));
        assertTrue(transitions.contains(Triple.of(s0, Alphabet.EPSILON, s4)));
        assertTrue(transitions.contains(Triple.of(s1, b, s2)));
        assertTrue(transitions.contains(Triple.of(s2, c, s3)));
        assertTrue(transitions.contains(Triple.of(s4, a, s5)));
        assertTrue(transitions.contains(Triple.of(s5, Alphabet.EPSILON, s6)));
        assertTrue(transitions.contains(Triple.of(s6, Alphabet.EPSILON, s7)));
        assertTrue(transitions.contains(Triple.of(s7, a, s8)));
    }
}
