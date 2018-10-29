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
public class NFABuilderTest {
    NFA.Builder builder;
    Alphabet alphabet;
    @Before
    public void setUp(){
        alphabet = new Alphabet();
        builder = new NFA.Builder(alphabet);
    }
    @Test
    public void addEpsilonTransitionTest(){
        builder.addEpsilonTransition(1, 3);
        builder.addEpsilonTransition(1, 4);
        assertEquals(builder.table.size(),1);
        assertTrue(builder.table.contains(1,Alphabet.EPSILON));
        assertEquals(builder.table.get(1, Alphabet.EPSILON),Sets.newHashSet(3,4));
    }
    @Test
    public void addTransitionTest(){
        builder.addTransition(1, 3, 2);
        builder.addTransition(1, 4, 2);
        assertEquals(builder.table.size(),1);
        assertTrue(builder.table.contains(1,2));
        assertEquals(builder.table.get(1, 2),Sets.newHashSet(3,4));
        
    }
    @Test
    public void containsTransitionOnlyFromTest(){
        assertFalse(builder.containsTransition(1, 2));
        builder.table.put(1, 2, new StateSet(3));
        assertTrue(builder.containsTransition(1, 2));
    }
    @Test
    public void containsTransitionTest(){
        assertFalse(builder.containsTransition(1, 3, 2));
        assertFalse(builder.containsTransition(1, 4, 2));
        builder.table.put(1, 2, new StateSet(3,4));
        assertTrue(builder.containsTransition(1, 3, 2));
        assertTrue(builder.containsTransition(1, 4, 2));
    }
    @Test
    public void buildTest(){
        int from = builder.addInitialState();
        StateSet to = new StateSet(builder.addFinalState());
        builder.table.put(from, 1, to);
        NFA nfa = builder.build();
        
        assertEquals(nfa.transitions.size(),1);
        assertEquals(nfa.initial_states.size(),1);
        assertEquals(nfa.final_states.size(),1);
        
        assertTrue(nfa.transitions.contains(from, 1));
        assertEquals(nfa.transitions.get(from, 1),Sets.newHashSet(to));
        assertTrue(nfa.initial_states.contains(from));
        assertTrue(nfa.final_states.containsAll(to));
    }
}
