/*
 * Copyright (c) 2019 Zavarov
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

package vartas.fa;

import org.junit.Before;
import org.junit.Test;
import vartas.fa.builder.DeterministicFiniteAutomatonBuilder;

import static org.assertj.core.api.Assertions.assertThat;

public class DeterministicFiniteAutomatonTest extends FiniteAutomatonTest{
    DeterministicFiniteAutomaton dfa;
    DeterministicFiniteAutomatonBuilder builder;

    State initialState;
    State a;
    State b;

    @Before
    public void setUp(){
        builder = new DeterministicFiniteAutomatonBuilder();

        initialState = builder.addInitialState("initial state");
        a = builder.addState("a");
        b = builder.addFinalState("b");

        builder.addTransition(initialState, 'a', a);
        builder.addTransition(initialState, 'b', b);
        builder.addTransition(a, 'a', a);
        builder.addTransition(a, 'b', b);
        builder.addTransition(b, 'a', a);
        builder.addTransition(b, 'b', b);

        //Accepts (a+b)*b
        dfa = builder.build();


    }

    @Test
    public void testRun(){
        assertThat(dfa.run("b")).isTrue();
        assertThat(dfa.run("ab")).isTrue();
        assertThat(dfa.run("aab")).isTrue();
        assertThat(dfa.run("ababab")).isTrue();
        assertThat(dfa.run("a")).isFalse();
        assertThat(dfa.run("ba")).isFalse();
        assertThat(dfa.run("bba")).isFalse();
        assertThat(dfa.run("bababa")).isFalse();
    }

    @Test
    public void testRunEmptyWord(){
        assertThat(dfa.run("")).isFalse();
    }

    @Test
    public void testInvalidWord(){
        assertThat(dfa.run("c")).isFalse();
    }

    @Test
    public void testReverse(){
        NondeterministicFiniteAutomaton nfa = dfa.reverse();

        assertThat(nfa.run("b")).isTrue();
        assertThat(nfa.run("ba")).isTrue();
        assertThat(nfa.run("baa")).isTrue();
        assertThat(nfa.run("bababa")).isTrue();
        assertThat(nfa.run("a")).isFalse();
        assertThat(nfa.run("ab")).isFalse();
        assertThat(nfa.run("abb")).isFalse();
        assertThat(nfa.run("ababab")).isFalse();
        assertThat(nfa.run("")).isFalse();
        assertThat(nfa.run("c")).isFalse();
    }

    @Test
    public void testMinimize(){
        dfa = dfa.minimize();

        assertThat(dfa.run("b")).isTrue();
        assertThat(dfa.run("ab")).isTrue();
        assertThat(dfa.run("aab")).isTrue();
        assertThat(dfa.run("ababab")).isTrue();
        assertThat(dfa.run("a")).isFalse();
        assertThat(dfa.run("ba")).isFalse();
        assertThat(dfa.run("bba")).isFalse();
        assertThat(dfa.run("bababa")).isFalse();
        assertThat(dfa.run("")).isFalse();
        assertThat(dfa.run("c")).isFalse();
    }

    @Override
    public void testGetStates() {
        assertThat(dfa.getStates()).containsExactlyInAnyOrder(initialState, a, b);
    }
}
