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

import vartas.fa.FiniteAutomaton;
import vartas.fa.State;

import java.util.HashSet;
import java.util.Set;

/**
 * This class implements the core functionality of the automaton builder.
 * While both the DFA and NFA have different transition tables, both share a set of states with final states
 * and a single initial state, which is provided via this super class.
 */
public abstract class FiniteAutomatonBuilder {
    /**
     * The set of all states in the automaton.
     */
    protected Set<State> states;
    /**
     * The initial state of the automaton.
     */
    protected State initialState;
    /**
     * Creates an empty builder.
     */
    protected FiniteAutomatonBuilder(){
        states = new HashSet<>();
    }
    /**
     * Adds a named state to the automaton.
     * Unless specified otherwise, states with a common name are allowed.
     * @param name the name of the state.
     * @return a new state with this name.
     */
    public State addState(String name){
        State state = new State(name);
        states.add(state);
        return state;
    }
    /**
     * Adds an unnamed state to the automaton.
     * @return a new unnamed state.
     */
    public State addState(){
        State state = new State();
        states.add(state);
        return state;
    }
    /**
     * Adds a named initial state to the automaton.
     * This method will throw an exception when called more than once.
     * @param name the name of the state.
     * @return a new initial state with this name.
     * @throws IllegalArgumentException if the automaton already has an initial state.
     */
    public State addInitialState(String name) throws IllegalArgumentException{
        if(initialState != null)
            throw new IllegalArgumentException("Only one initial state is allowed");

        initialState = addState(name);
        initialState.setInitial(true);
        return initialState;
    }
    /**
     * Adds an unnamed initial state to the automaton.
     * This method will throw an exception when called more than once.
     * @return a new unnamed initial state.
     * @throws IllegalArgumentException if the automaton already has an initial state.
     */
    public State addInitialState() throws  IllegalArgumentException{
        if(initialState != null)
            throw new IllegalArgumentException("Only one initial state is allowed");

        initialState = addState();
        initialState.setInitial(true);
        return initialState;
    }
    /**
     * Adds a named final state to the automaton.
     * Unless specified otherwise, states with a common name are allowed.
     * @param name the name of the state.
     * @return a new final state with this name.
     */
    public State addFinalState(String name){
        State state = addState(name);
        state.setFinal(true);
        return state;
    }
    /**
     * Adds an unnamed final state to the automaton.
     * @return a new unnamed final state.
     */
    public State addFinalState(){
        State state = addState();
        state.setFinal(true);
        return state;
    }

    /**
     * This method provides the interface for creating the different automata
     * and as its only restriction, must fail if no initial state is set.
     * @return the finite automaton constructed by this builder.
     * @throws IllegalStateException if the automaton doesn't have an initial state.
     */
    public abstract FiniteAutomaton build() throws IllegalStateException;

    /**
     * Reverts the builder back to its fresh state.
     */
    public void clear(){
        initialState = null;
        states = new HashSet<>();
    }
}
