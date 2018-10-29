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
import vartas.automaton.MooreAutomaton.Builder;

/**
 *
 * @author Zavarov
 */
public class MooreAutomatonTest {
    
    MooreAutomaton automaton;
    List<Integer> list = new IntArrayList();
    int s0,s1,s2,s3,s4;
    int a,b,c,d,e;
    @Before
    public void setUp(){
        Builder moore = new Builder();
        s0 = moore.addInitialState();
        s1 = moore.addState();
        s2 = moore.addState();
        s3 = moore.addFinalState();
        s4 = moore.addFinalState();
        a = 0;
        b = 1;
        c = 2;
        d = 3;
        e = 4;
        moore.addTransition(s0, s1, a);
        moore.addTransition(s1, s2, b);
        moore.addTransition(s2, s3, c);
        moore.addTransition(s3, s0, d);
        moore.addTransition(s0, s4, e);
        
        moore.setOutput(s1, (i) -> list.add(1));
        moore.setOutput(s3, (i) -> list.add(3));
        moore.setOutput(s4, (i) -> list.add(4));
        automaton = moore.build();
    }
    @Test
    public void outputTest(){
        automaton.step(new StateSet(0), a);
        assertEquals(list,Arrays.asList(1));
        
        automaton.step(new StateSet(1), b);
        assertEquals(list,Arrays.asList(1));
        
        automaton.step(new StateSet(2), c);
        assertEquals(list,Arrays.asList(1,3));
        
        automaton.step(new StateSet(0), e);
        assertEquals(list,Arrays.asList(1,3,4));
    }
}
