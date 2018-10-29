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
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import vartas.automaton.MealyAutomaton.Builder;

/**
 *
 * @author Zavarov
 */
public class MealyAutomatonBuilderTest {
    List<String> list;
    Builder builder;
    BiConsumer<Integer,Integer> output;
    int s0,s1,a;
    @Before
    public void setUp(){
        list = new LinkedList<>();
        builder = new Builder();
        s0 = builder.addInitialState();
        s1 = builder.addFinalState();
        a = 0;
        builder.addTransition(s0, s1, a);
        output = (i,j) -> list.add("Success");
    }
    
    @Test
    public void setOutputTest(){
        assertTrue(list.isEmpty());
        
        builder.setOutput(s1, a, output);
        builder.build().run(Arrays.asList(a));
        
        assertEquals(builder.outputs.get(s1, a),output);
        
        assertEquals(list,Arrays.asList("Success"));
    }
}
