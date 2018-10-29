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
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import vartas.automaton.alphabet.Alphabet;

/**
 *
 * @author Zavarov
 */
public class FiniteAutomatonBuilderTest {
    Alphabet alphabet;
    FiniteAutomaton.Builder<DFA> builder;
    @Before
    public void setUp(){
        alphabet = new Alphabet();
        builder = new DFA.Builder(alphabet);
    }
    @Test
    public void containsTransitionOnlyFromTest(){
        int s1 = builder.addState();
        int s2 = builder.addState();
        assertFalse(builder.containsTransition(s1, "a"));
        builder.addTransition(s1, s2, "a");
        assertTrue(builder.containsTransition(s1, "a"));
    }
    @Test
    public void containsTransitionTest(){
        int s1 = builder.addState();
        int s2 = builder.addState();
        assertFalse(builder.containsTransition(s1, s2, "a"));
        builder.addTransition(s1, s2, "a");
        assertTrue(builder.containsTransition(s1, s2, "a"));
    }
    @Test
    public void addStateTest(){
        int state = builder.addState();
        assertEquals(state,0);
        assertEquals(builder.all_states.size(),1);
    }
    @Test(expected=IllegalStateException.class)
    public void addStateFullTest(){
        builder.next_id = FiniteAutomaton.Builder.LAST_ID+1;
        builder.addState();
    }
    @Test
    public void addFinalStateTest(){
        int state = builder.addFinalState();
        assertEquals(state,0);
        assertEquals(builder.all_states.size(),1);
        assertEquals(builder.final_states.size(),1);
        assertTrue(builder.final_states.contains(state));
    }
    @Test(expected=IllegalStateException.class)
    public void addFinalStateFullTest(){
        builder.next_id = FiniteAutomaton.Builder.LAST_ID+1;
        builder.addFinalState();
    }
    @Test
    public void addInitialStateTest(){
        int state = builder.addInitialState();
        assertEquals(state,0);
        assertEquals(builder.all_states.size(),1);
        assertEquals(builder.initial_states.size(),1);
        assertTrue(builder.initial_states.contains(state));
    }
    @Test(expected=IllegalStateException.class)
    public void addInitialStateFullTest(){
        builder.next_id = FiniteAutomaton.Builder.LAST_ID+1;
        builder.addInitialState();
    }
    
    @Test
    public void addTransitionStringTest(){
        int s1 = builder.addState();
        int s2 = builder.addState();
        builder.addTransition(s1, s2, "a","b");
        int a = builder.alphabet.get("a");
        int b = builder.alphabet.get("b");
        assertTrue(builder.containsTransition(s1, s2, "a"));
        assertTrue(builder.containsTransition(s1, s2, "b"));
    }
    
    @Test
    public void addTransitionIntegerTest(){
        int s1 = builder.addState();
        int s2 = builder.addState();
        builder.addTransition(s1, s2, 0,1);
        assertTrue(builder.containsTransition(s1, s2, 0));
        assertTrue(builder.containsTransition(s1, s2, 1));
    }
    
    @Test(expected=IllegalStateException.class)
    public void addTransitionFromIllegalStateExceptionTest(){
        builder.addTransition(100,0,0,1);
    }
    
    @Test(expected=IllegalStateException.class)
    public void addTransitionToIllegalStateExceptionTest(){
        builder.addTransition(builder.addState(),100,0,1);
    }
    @Test
    public void makeFinalStateTest(){
        int s0 = builder.addState();
        int s1 = builder.addFinalState();
        assertTrue(builder.makeFinalState(s0));
        assertFalse(builder.makeFinalState(s1));
        assertTrue(builder.final_states.contains(s0));
        assertTrue(builder.final_states.contains(s1));
        assertEquals(builder.final_states.size(),2);
    }
    @Test(expected=IllegalStateException.class)
    public void makeFinalStateExceptionTest(){
        builder.makeFinalState(0);
    }
    @Test
    public void makeInitialStateTest(){
        int s0 = builder.addState();
        int s1 = builder.addInitialState();
        assertTrue(builder.makeInitialState(s0));
        assertFalse(builder.makeInitialState(s1));
        assertTrue(builder.initial_states.contains(s0));
        assertTrue(builder.initial_states.contains(s1));
        assertEquals(builder.initial_states.size(),2);
    }
    @Test(expected=IllegalStateException.class)
    public void makeInitialStateExceptionTest(){
        builder.makeInitialState(0);
    }
    @Test
    public void addAutomatonTest(){
        int s1 = builder.addInitialState();
        int s2 = builder.addFinalState();
        builder.addTransition(s1, s2, "b");
        DFA.Builder dfa = new DFA.Builder(alphabet);
        int t1 = dfa.addInitialState();
        int t2 = dfa.addFinalState();
        dfa.addTransition(t1, t2, "a");
        
        builder.addAutomaton(dfa.build());
        
        int a = alphabet.get("a");
        int b = alphabet.get("b");
        
        FiniteAutomaton automaton = builder.build();
        assertTrue(automaton.run(Arrays.asList(a)));
        assertTrue(automaton.run(Arrays.asList(b)));
    }
}
