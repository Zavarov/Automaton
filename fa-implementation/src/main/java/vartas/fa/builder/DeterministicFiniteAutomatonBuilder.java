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
import com.google.common.collect.Table;
import vartas.fa.DeterministicFiniteAutomaton;
import vartas.fa.State;

/**
 * This builder is used to create instances of DFAs.
 */
public class DeterministicFiniteAutomatonBuilder extends FiniteAutomatonBuilder{
    /**
     * The underlying transition table.
     */
    protected Table<State,Character, State> transitions;

    /**
     * Creates an empty builder.
     */
    public DeterministicFiniteAutomatonBuilder(){
        super();
        transitions = HashBasedTable.create();
    }

    /**
     * Adds a new transition to the automaton.
     * @param from the current state.
     * @param with the label that is read.
     * @param to the next state.
     * @throws IllegalArgumentException if there already is an outgoing transition with the given label.
     */
    public void addTransition(State from, char with, State to) throws IllegalArgumentException{
        if(transitions.contains(from, with))
            throw new IllegalArgumentException(String.format("There already exists a transition from %s via %s", from, with));
        transitions.put(from, with,to);
    }

    /**
     * @return the created automaton.
     * @throws IllegalStateException if the automaton doesn't have an initial state.
     */
    @Override
    public DeterministicFiniteAutomaton build() throws IllegalStateException{
        if(initialState == null)
            throw new IllegalStateException("The automaton doesn't have an initial state");
        return new DeterministicFiniteAutomaton(initialState, states, transitions);
    }

    /**
     * Reverts the builder back to its fresh state.
     */
    public void clear(){
        super.clear();
        transitions = HashBasedTable.create();
    }
}
