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
import vartas.fa.builder.DeterministicFiniteDefaultAutomatonBuilder;

import static org.assertj.core.api.Assertions.assertThat;

public class DeterministicFiniteDefaultAutomatonTest extends FiniteAutomatonTest{
    DeterministicFiniteDefaultAutomaton dfa;
    DeterministicFiniteDefaultAutomatonBuilder builder;

    State initialState;
    State sink;

    @Before
    public void setUp(){
        builder = new DeterministicFiniteDefaultAutomatonBuilder();

        initialState = builder.addInitialState();
        sink = builder.addState();

        initialState.setFinal(true);

        builder.addTransition(initialState, 'b', sink);
        builder.addDefaultTransition(initialState, initialState);
        builder.addDefaultTransition(sink, sink);

        //Accepts all words that don't contain a b
        dfa = builder.build();


    }

    @Test
    public void testRun(){
        assertThat(dfa.run("a")).isTrue();
        assertThat(dfa.run("aaccaa")).isTrue();
        assertThat(dfa.run("b")).isFalse();
        assertThat(dfa.run("ab")).isFalse();
        assertThat(dfa.run("aba")).isFalse();
    }

    @Test
    public void testRunEmptyWord(){
        assertThat(dfa.run("")).isTrue();
    }

    @Test
    public void testReverse(){
        NondeterministicFiniteAutomaton nfa = dfa.reverse();

        assertThat(nfa.run("a")).isTrue();
        assertThat(nfa.run("aaccaa")).isTrue();
        assertThat(nfa.run("b")).isFalse();
        assertThat(nfa.run("ba")).isFalse();
        assertThat(nfa.run("aba")).isFalse();
        assertThat(nfa.run("")).isTrue();
    }

    @Test
    public void testMinimize(){
        dfa = dfa.minimize();

        assertThat(dfa.run("a")).isTrue();
        assertThat(dfa.run("aaccaa")).isTrue();
        assertThat(dfa.run("b")).isFalse();
        assertThat(dfa.run("ab")).isFalse();
        assertThat(dfa.run("aba")).isFalse();
        assertThat(dfa.run("")).isTrue();
    }

    @Override
    public void testGetStates() {
        assertThat(dfa.getStates()).containsExactlyInAnyOrder(initialState, sink);
    }
}
