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

package vartas.fa.transformations;

import vartas.fa.DeterministicFiniteAutomaton;
import vartas.fa.NondeterministicFiniteAutomaton;
import vartas.fa.State;
import vartas.fa.builder.NondeterministicFiniteAutomatonBuilder;
import vartas.fa.visitor.DeterministicFiniteAutomatonTransitionVisitor;

import java.util.HashMap;
import java.util.Map;

/**
 * This interfaces adds the ability to reverse DFAs.
 */
public interface Reverse extends DeterministicFiniteAutomatonTransitionVisitor{
    /**
     * A map that relates states in the DFA to the states in the reverse automaton.
     */
    Map<State, State> map = new HashMap<>();
    /**
     * @return the NFA builder for the reverse language.
     */
    NondeterministicFiniteAutomatonBuilder getReverseBuilder();

    /**
     * @return a NFA accept the reverse language of the current DFA.
     */
    default NondeterministicFiniteAutomaton reverse(){
        getReverseBuilder().clear();
        visitedTransitions.clear();
        map.clear();

        accept(getDfa());

        return getReverseBuilder().build();
    }

    /**
     * Starts building the reverse starting from the initial state.
     * @param dfa the given NFA.
     */
    default void accept(DeterministicFiniteAutomaton dfa){
        State initialState = dfa.getInitialState();

        map.put(initialState, getReverseBuilder().addState());

        handle(initialState);

        //All the old final states are new "initial" states
        State newInitialState = getReverseBuilder().addInitialState();
        map.values().stream().filter(State::isFinal).forEach(oldFinalState -> {
            oldFinalState.setFinal(false);
            getReverseBuilder().addEpsilonTransition(newInitialState, oldFinalState);
        });

        //The old initial state is the new final state.
        State newFinalState = map.get(initialState);
        newFinalState.setFinal(true);
    }


    /**
     * Creates a state in the NFA that relates to the given state in the DFA,
     * if no such relation exists.
     * This new state will be a final state, if the state in the DFA also is one.
     * @param state a state in the automaton.
     */
    @Override
    default void visit(State state){
        State newState = map.computeIfAbsent(state, x -> getReverseBuilder().addState());

        if(state.isFinal())
            newState.setFinal(true);
    }

    /**
     * Adds a transition over the given label to the NFA.
     * This transition will go from the next state to the current state via the label.
     * @param from the current state in the DFA.
     * @param with the label that is read.
     * @param to the state in the DFA that is reached after consuming the label.
     */
    @Override
    default void visit(State from, char with, State to){
        getReverseBuilder().addTransition(map.get(to), with, map.get(from));
    }
}
