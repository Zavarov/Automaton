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

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import vartas.fa.NondeterministicFiniteDefaultAutomaton;
import vartas.fa.State;

/**
 * This builder is used to create instances of NFAs that allow infinite alphabets.
 */
public class NondeterministicFiniteDefaultAutomatonBuilder extends NondeterministicFiniteAutomatonBuilder{
    /**
     * All 'else' transitions in the automaton.
     */
    protected Multimap<State, State> defaultTransitions;

    /**
     * Creates an empty builder.
     */
    public NondeterministicFiniteDefaultAutomatonBuilder(){
        super();
        defaultTransitions = HashMultimap.create();
    }

    /**
     * Adds a new 'else' transition to the automaton.
     * @param from the current state.
     * @param to the next state.
     * @throws IllegalArgumentException if the transitions already exists.
     */
    public void addDefaultTransition(State from, State to){
        if(defaultTransitions.containsEntry(from, to))
            throw new IllegalArgumentException(String.format("There already exists a default transition from %s to %s", from, to));
        defaultTransitions.put(from, to);
    }

    /**
     * @return the created automaton.
     * @throws IllegalStateException if the automaton doesn't have an initial state.
     */
    @Override
    public NondeterministicFiniteDefaultAutomaton build() {
        if(initialState == null)
            throw new IllegalStateException("The automaton doesn't have an initial state");
        return new NondeterministicFiniteDefaultAutomaton(initialState, states, transitions, epsilonTransitions, defaultTransitions);
    }

    /**
     * Reverts the builder back to its fresh state.
     */
    public void clear(){
        super.clear();
        defaultTransitions = HashMultimap.create();
    }
}
