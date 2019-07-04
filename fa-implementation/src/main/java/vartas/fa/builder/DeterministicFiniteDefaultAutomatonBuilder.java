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

import vartas.fa.DeterministicFiniteDefaultAutomaton;
import vartas.fa.State;

import java.util.HashMap;
import java.util.Map;

/**
 * This builder is used to create instances of DFAs that allow infinite alphabets.
 */
public class DeterministicFiniteDefaultAutomatonBuilder extends DeterministicFiniteAutomatonBuilder {
    /**
     * All 'else' transitions in the automaton.
     */
    protected Map<State, State> defaultTransitions;

    /**
     * Creates an empty builder.
     */
    public DeterministicFiniteDefaultAutomatonBuilder(){
        super();
        defaultTransitions = new HashMap<>();
    }

    /**
     * Adds a new 'else' transition to the automaton.
     * @param from the current state.
     * @param to the next state.
     * @throws IllegalArgumentException if the transitions already exists.
     */
    public void addDefaultTransition(State from, State to){
        if(defaultTransitions.containsKey(from))
            throw new IllegalArgumentException(String.format("There already exists a default transition from %s", from));
        defaultTransitions.put(from, to);

    }

    /**
     * @return the created automaton.
     * @throws IllegalStateException if the automaton doesn't have an initial state.
     */
    @Override
    public DeterministicFiniteDefaultAutomaton build() throws IllegalStateException{
        if(initialState == null)
            throw new IllegalStateException("The automaton doesn't have an initial state");
        return new DeterministicFiniteDefaultAutomaton(initialState, states, transitions, defaultTransitions);
    }

    /**
     * Reverts the builder back to its fresh state.
     */
    public void clear(){
        super.clear();
        defaultTransitions = new HashMap<>();
    }
}
