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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Zavarov
 */
public class DFABuilderTest {
    DFA.Builder builder;
    @Before
    public void setUp(){
        builder = new DFA.Builder();
    }
    @Test
    public void addTransitionTest(){
        builder.addTransition(1, 3, 2);
        assertEquals(builder.table.size(),1);
        assertTrue(builder.table.contains(1,2));
        assertEquals(builder.table.get(1, 2).intValue(),3);
        
    }
    @Test(expected=IllegalStateException.class)
    public void addTransitionTransitionIsNondeterministicExceptionTest(){
        builder.alphabet.add("a");
        int a = builder.alphabet.get("a");
        builder.addTransition(1, 3, a);
        builder.addTransition(1, 4, a);
    }
    @Test
    public void containsTransitionOnlyFromTest(){
        assertFalse(builder.containsTransition(1, 2));
        builder.table.put(1, 2, 3);
        assertTrue(builder.containsTransition(1, 2));
    }
    @Test
    public void containsTransitionTest(){
        assertFalse(builder.containsTransition(1, 3, 2));
        builder.table.put(1, 2, 3);
        assertTrue(builder.containsTransition(1, 3, 2));
        assertFalse(builder.containsTransition(1, 9, 2));
    }
    @Test
    public void buildTest(){
        int from = builder.addInitialState();
        int to = builder.addFinalState();
        builder.table.put(from, 1, to);
        DFA dfa = builder.build();
        
        assertEquals(dfa.transitions.size(),3);
        assertEquals(dfa.initial_states.size(),1);
        assertEquals(dfa.final_states.size(),1);
        
        assertTrue(dfa.transitions.contains(from, 1));
        assertEquals(dfa.transitions.get(from, 1).intValue(),to);
        assertTrue(dfa.initial_states.contains(from));
        assertTrue(dfa.final_states.contains(to));
    }
}
