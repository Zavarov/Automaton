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
import vartas.fa.builder.NondeterministicFiniteAutomatonBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

public class NondeterministicFiniteAutomatonTest extends FiniteAutomatonTest{
    NondeterministicFiniteAutomaton nfa;
    NondeterministicFiniteAutomatonBuilder builder;
    State initialState;
    State s1;
    State s2;
    State s3;
    State s4;
    State s5;
    State s6;
    State s7;
    State s8;
    State s9;

    @Before
    public void setUp(){
        builder = new NondeterministicFiniteAutomatonBuilder();
        initialState = builder.addInitialState("initial state");

        s1 = builder.addState("s1");
        s2 = builder.addFinalState("s2");
        s3 = builder.addState("s3");
        s4 = builder.addFinalState("s4");
        s5 = builder.addState("s5");
        s6 = builder.addState("s6");
        s7 = builder.addState("s7");
        s8 = builder.addState("s8");
        s9 = builder.addFinalState("s9");

        builder.addTransition(initialState, 'a', s1);
        builder.addTransition(initialState, 'a', s3);
        builder.addTransition(initialState, 'b', s5);
        builder.addTransition(s1, 'c', s2);
        builder.addTransition(s3, 'b', s4);
        builder.addTransition(s8, 'c', s9);

        //Add an epsilon loop
        builder.addEpsilonTransition(s5, s6);
        builder.addEpsilonTransition(s6, s5);
        //Nested epsilon transitions
        builder.addEpsilonTransition(s6, s7);
        builder.addEpsilonTransition(s7, s8);

        //Accepts ab+ac+bc
        nfa = builder.build();
    }

    @Test
    public void testRun(){
        assertThat(nfa.run("ac")).isTrue();
        assertThat(nfa.run("ab")).isTrue();
        assertThat(nfa.run("a")).isFalse();
        assertThat(nfa.run("b")).isFalse();
    }

    @Test
    public void testRunEmptyWord(){
        assertThat(nfa.run("")).isFalse();
    }

    @Test
    public void testInvalidWord(){
        assertThat(nfa.run("c")).isFalse();
    }

    @Test
    public void testEpsilonTransition(){
        assertThat(nfa.run("bc")).isTrue();
    }

    @Test
    public void testPowerSet(){
        DeterministicFiniteAutomaton dfa = nfa.powerSet();

        assertThat(dfa.run("ac")).isTrue();
        assertThat(dfa.run("ab")).isTrue();
        assertThat(dfa.run("a")).isFalse();
        assertThat(dfa.run("b")).isFalse();
        assertThat(dfa.run("")).isFalse();
        assertThat(dfa.run("c")).isFalse();
        assertThat(dfa.run("bc")).isTrue();
    }
    @Test
    public void testGetEpsilonTransitions(){
        assertTrue(nfa.getEpsilonTransitions().containsEntry(s5, s6));
        assertTrue(nfa.getEpsilonTransitions().containsEntry(s6, s5));
        assertTrue(nfa.getEpsilonTransitions().containsEntry(s6, s7));
        assertTrue(nfa.getEpsilonTransitions().containsEntry(s7, s8));
    }

    @Override
    public void testGetStates() {
        assertThat(nfa.getStates()).containsExactlyInAnyOrder(initialState, s1, s2, s3, s4, s5, s6, s7, s8, s9);
    }
}
