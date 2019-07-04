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
import vartas.fa.builder.NondeterministicFiniteDefaultAutomatonBuilder;

import static org.assertj.core.api.Assertions.assertThat;

public class NondeterministicFiniteDefaultAutomatonTest extends FiniteAutomatonTest{
    NondeterministicFiniteDefaultAutomaton nfa;
    NondeterministicFiniteDefaultAutomatonBuilder builder;
    State initialState;
    State s1;
    State s2;
    State s3;
    State s4;
    State s5;
    State s6;
    State s7;

    @Before
    public void setUp(){
        builder = new NondeterministicFiniteDefaultAutomatonBuilder();

        initialState = builder.addInitialState("initial state");
        s1 = builder.addState("s1");
        s2 = builder.addState("s2");
        s3 = builder.addFinalState("s3");
        s4 = builder.addState("s4");
        s5 = builder.addState("s5");
        s6 = builder.addState("s6");
        s7 = builder.addFinalState("s7");

        builder.addTransition(initialState, 'a', s1);
        builder.addTransition(initialState, 'a', s4);
        builder.addTransition(s2, 'b', s3);
        builder.addTransition(s6, 'c', s7);

        builder.addEpsilonTransition(s4, s5);

        builder.addDefaultTransition(s1, s2);
        builder.addDefaultTransition(s5, s6);

        //Accepts all words of length 3 that begin with a and either end with b or c.
        nfa = builder.build();
    }

    @Test
    public void testRun(){
        assertThat(nfa.run("ac")).isFalse();
        assertThat(nfa.run("ab")).isFalse();
        assertThat(nfa.run("axb")).isTrue();
        assertThat(nfa.run("ayc")).isTrue();
        assertThat(nfa.run("axx")).isFalse();
        assertThat(nfa.run("byc")).isFalse();
    }

    @Test
    public void testRunEmptyWord(){
        assertThat(nfa.run("")).isFalse();
    }

    @Test
    public void testPowerSet(){
        DeterministicFiniteDefaultAutomaton dfa = nfa.powerSet();

        assertThat(dfa.run("")).isFalse();
        assertThat(dfa.run("ac")).isFalse();
        assertThat(dfa.run("ab")).isFalse();
        assertThat(dfa.run("axb")).isTrue();
        assertThat(dfa.run("ayc")).isTrue();
        assertThat(dfa.run("axx")).isFalse();
        assertThat(dfa.run("byc")).isFalse();
    }

    @Override
    public void testGetStates() {
        assertThat(nfa.getStates()).containsExactlyInAnyOrder(initialState, s1, s2, s3, s4, s5, s6, s7);
    }
}
