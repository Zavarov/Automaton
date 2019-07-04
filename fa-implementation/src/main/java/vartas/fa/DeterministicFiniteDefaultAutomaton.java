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

import com.google.common.collect.Table;
import vartas.fa.builder.NondeterministicFiniteDefaultAutomatonBuilder;
import vartas.fa.transformations.MinimizeDefault;
import vartas.fa.transformations.ReverseDefault;

import java.util.Map;
import java.util.Set;

/**
 * Implements a DFA supporting default transitions.
 * A default transition is a labelless transition that is always taken when there is no other matching transition.
 * The current label is consumed in the process.
 */
public class DeterministicFiniteDefaultAutomaton extends DeterministicFiniteAutomaton implements ReverseDefault, MinimizeDefault {
    /**
     * The builder that is responsible for creating the reverse language.
     */
    private NondeterministicFiniteDefaultAutomatonBuilder builder;
    /**
     * All default transitions.
     */
    protected Map<State, State> defaults;
    /**
     * Creates a new instance of a DFA  with default transitions.
     * @param initialState the initial state.
     * @param states all states in the automaton.
     * @param transitions all transitions in the automaton.
     * @param defaults all default transitions in the automaton.
     */
    public DeterministicFiniteDefaultAutomaton(State initialState, Set<State> states, Table<State,Character,State> transitions, Map<State, State> defaults){
        super(initialState, states, transitions);
        this.defaults = defaults;
        this.builder = new NondeterministicFiniteDefaultAutomatonBuilder();
    }
    /**
     * Executes a single step over the given label.
     * If there is no matching transition, the default transition is used.
     * @param state the current state.
     * @param label the letter that has been read.
     * @return the state that is reached after using the transition with the given label or the default transition.
     */
    @Override
    public State step(State state, char label){
        if(transitions.contains(state, label))
            return transitions.get(state, label);
        else
            return defaults.get(state);
    }
    /**
     * Unlike its parent, there is a successor is either a transition is present or if a default transition is present.
     * @param state the current state.
     * @param label the letter that has been read.
     * @return true if there is a matching transition for that label.
     */
    protected boolean hasNext(State state, char label){
        return super.hasNext(state, label) || defaults.containsKey(state);
    }
    /**
     * @return all default transitions in this automaton.
     */
    public Map<State,State> getDefaultTransitions(){
        return defaults;
    }
    /**
     * @return the builder that is responsible for creating the reverse language.
     */
    @Override
    public NondeterministicFiniteDefaultAutomatonBuilder getReverseBuilder() {
        return builder;
    }
    /**
     * @return the current instance of the automaton
     */
    @Override
    public DeterministicFiniteDefaultAutomaton getDfa() {
        return this;
    }
}