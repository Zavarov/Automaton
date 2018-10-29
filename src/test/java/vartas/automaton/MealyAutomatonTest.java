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

import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import vartas.automaton.FiniteAutomaton.StateSet;

/**
 *
 * @author Zavarov
 */
public class MealyAutomatonTest {
    
    MealyAutomaton automaton;
    List<Integer> list;
    int s0,s1,s2,s3,s4;
    int a,b,c,d,e;
    @Before
    public void setUp(){
        list = new IntArrayList();
        MealyAutomaton.Builder mealy = new MealyAutomaton.Builder();
        s0 = mealy.addInitialState();
        s1 = mealy.addState();
        s2 = mealy.addState();
        s3 = mealy.addFinalState();
        s4 = mealy.addFinalState();
        a = 0;
        b = 1;
        c = 2;
        d = 3;
        e = 4;
        mealy.addTransition(s0, s1, a);
        mealy.addTransition(s1, s2, b);
        mealy.addTransition(s2, s3, c);
        mealy.addTransition(s3, s0, d);
        mealy.addTransition(s0, s4, e);
        
        mealy.setOutput(s1, a, (i,j) -> list.add(a));
        mealy.setOutput(s0, d, (i,j) -> list.add(d));
        mealy.setOutput(s4, e, (i,j) -> list.add(e));
        automaton = mealy.build();
    }
    @Test
    public void stepTest(){
        automaton.step(new StateSet(0), a);
        assertEquals(list,Arrays.asList(a));
        
        automaton.step(new StateSet(3), d);
        assertEquals(list,Arrays.asList(a,d));
        
        automaton.step(new StateSet(1), b);
        assertEquals(list,Arrays.asList(a,d));
        
        automaton.step(new StateSet(0), e);
        assertEquals(list,Arrays.asList(a,d,e));
    }
}