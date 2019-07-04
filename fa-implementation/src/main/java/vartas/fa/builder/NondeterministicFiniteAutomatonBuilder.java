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

package vartas.fa.builder;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;
import vartas.fa.NondeterministicFiniteAutomaton;
import vartas.fa.State;

import java.util.Collection;
import java.util.HashSet;

/**
 * This builder is used to create instances of NFAs.
 */
public class NondeterministicFiniteAutomatonBuilder extends FiniteAutomatonBuilder{
    /**
     * All labeled transitions in the automaton.
     */
    protected Table<State,Character, Collection<State>> transitions;
    /**
     * All epsilon transitions in the automaton.
     */
    protected Multimap<State,State> epsilonTransitions;

    /**
     * Creates an empty builder.
     */
    public NondeterministicFiniteAutomatonBuilder(){
        super();
        transitions = HashBasedTable.create();
        epsilonTransitions = HashMultimap.create();
    }

    /**
     * Adds a new transition to the automaton.
     * @param from the current state.
     * @param with the label that is read.
     * @param to the next state.
     * @throws IllegalArgumentException if the transitions already exists.
     */
    public void addTransition(State from, char with, State to){
        if(!transitions.contains(from, with))
            transitions.put(from, with, new HashSet<>());
        if(transitions.get(from, with).contains(to))
            throw new IllegalArgumentException(String.format("There already exists a transition from %s to %s via %s", from, to, with));

        transitions.get(from, with).add(to);
    }

    /**
     * Adds a new epsilon transition to the automaton.
     * @param from the current state.
     * @param to the next state.
     * @throws IllegalArgumentException if the transitions already exists.
     */
    public void addEpsilonTransition(State from, State to){
        if(epsilonTransitions.containsEntry(from, to))
            throw new IllegalArgumentException(String.format("There already exists an epsilon transition from %s to %s", from, to));
        epsilonTransitions.put(from, to);
    }

    /**
     * @return the created automaton.
     * @throws IllegalStateException if the automaton doesn't have an initial state.
     */
    @Override
    public NondeterministicFiniteAutomaton build() {
        if(initialState == null)
            throw new IllegalStateException("The automaton doesn't have an initial state");
        return new NondeterministicFiniteAutomaton(initialState, states, transitions, epsilonTransitions);
    }

    /**
     * Reverts the builder back to its fresh state.
     */
    public void clear(){
        super.clear();
        transitions = HashBasedTable.create();
        epsilonTransitions = HashMultimap.create();
    }
}
