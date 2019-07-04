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
import vartas.fa.builder.DeterministicFiniteAutomatonBuilder;
import vartas.fa.visitor.NondeterministicFiniteAutomatonTransitionVisitor;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * This interfaces adds the ability to transform NFAs into DFAs by applying the power set construction.
 */
public interface PowerSet extends NondeterministicFiniteAutomatonTransitionVisitor {
    /**
     * A map that relates states in the given NFA to a single state in the resulting DFA.
     */
    Map<Collection<State>,State> groups = new HashMap<>();

    /**
     * @return the DFA builder for the power set.
     */
    DeterministicFiniteAutomatonBuilder getPowerSetBuilder();

    /**
     * @return a DFA that is equivalent to the current NFA.
     */
    default DeterministicFiniteAutomaton powerSet(){
        getPowerSetBuilder().clear();
        groups.clear();
        visitedTransitions.clear();

        accept(getNfa());

        return getPowerSetBuilder().build();
    }

    /**
     * Starts building the power set starting from the initial state.
     * @param nfa the given NFA.
     */
    default void accept(NondeterministicFiniteAutomaton nfa){
        State initialState = nfa.getInitialState();
        Collection<State> currentStates = nfa.closure(initialState);

        groups.put(currentStates, getPowerSetBuilder().addInitialState());

        handle(currentStates);
    }

    /**
     * Creates a state in the DFA that relates to the given set of states of the NFA,
     * if no such relation exists.
     * This new state will be a final state, if at least one state in the NFA is also one.
     * @param states a set of states in the automaton.
     */
    @Override
    default void visit(Collection<State> states){
        State currentState = groups.computeIfAbsent(states, x -> getPowerSetBuilder().addState());

        if(states.stream().anyMatch(State::isFinal))
            currentState.setFinal(true);
    }

    /**
     * Adds a transition over the given label to the DFA.
     * @param from the states the NFA currently is in.
     * @param with the label that is read.
     * @param to the states in the NFA that are reached after consuming the label.
     */
    @Override
    default void visit(Collection<State> from, char with, Collection<State> to){
        getPowerSetBuilder().addTransition(groups.get(from), with, groups.get(to));
    }
}
