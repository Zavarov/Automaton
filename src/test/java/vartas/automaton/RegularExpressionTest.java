/*
 * Copyright (C) 2017 Zavarov
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import vartas.automaton.alphabet.Alphabet;

/**
 *
 * @author Zavarov
 */
public class RegularExpressionTest {
    int a,b,c;
    @Before
    public void setUp(){
        a = 0;
        b = 1;
        c = 2;
    }
    @Test
    public void singletonTest(){
        RegularExpression expression = RegularExpression.singleton(a);
        assertTrue(expression.run(Arrays.asList(a)));
        assertFalse(expression.run(Arrays.asList(a,a)));
        assertFalse(expression.run(Arrays.asList(b)));
        expression = RegularExpression.singleton(Alphabet.EPSILON);
        assertTrue(expression.run(Arrays.asList()));
        assertFalse(expression.run(Arrays.asList(a)));
    }
    
    @Test
    public void unionTest(){
        RegularExpression first = RegularExpression.singleton(a);
        RegularExpression second = RegularExpression.singleton(b);
        RegularExpression third = RegularExpression.singleton(c);
        RegularExpression expression = RegularExpression.union(first,second,third);
        assertTrue(expression.run(Arrays.asList(a)));
        assertTrue(expression.run(Arrays.asList(b)));
        assertTrue(expression.run(Arrays.asList(c)));
    }
    
    @Test
    public void concatenationTest(){
        RegularExpression first = RegularExpression.singleton(a);
        RegularExpression second = RegularExpression.singleton(b);
        RegularExpression third = RegularExpression.singleton(c);
        RegularExpression expression = RegularExpression.concatenation(first,second,third);
        assertTrue(expression.run(Arrays.asList(a,b,c)));
        assertFalse(expression.run(Arrays.asList(a)));
        assertFalse(expression.run(Arrays.asList(b)));
        assertFalse(expression.run(Arrays.asList(c)));
        assertFalse(expression.run(Arrays.asList(a,b)));
    }
    
    @Test
    public void kleeneStarTest(){
        RegularExpression first = RegularExpression.singleton(a);
        RegularExpression second = RegularExpression.singleton(b);
        RegularExpression concat = RegularExpression.concatenation(first, second);
        RegularExpression kleene = RegularExpression.kleeneStar(concat);
        assertTrue(kleene.run(Arrays.asList()));
        assertTrue(kleene.run(Arrays.asList(a,b)));
        assertTrue(kleene.run(Arrays.asList(a,b,a,b)));
        assertFalse(kleene.run(Arrays.asList(a)));
        assertFalse(kleene.run(Arrays.asList(a,b,a)));
    }
}
