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

import com.google.common.collect.Multimap;
import com.google.common.collect.Table;
import vartas.fa.builder.DeterministicFiniteDefaultAutomatonBuilder;
import vartas.fa.transformations.PowerSetDefault;

import java.util.*;

/**
 * Implements an NFA supporting default transition.
 * In each individual state, a default transition is taken whenever there is no other matching transition.
 * The current label is consumed in the process.
 */
public class NondeterministicFiniteDefaultAutomaton extends NondeterministicFiniteAutomaton implements PowerSetDefault {
    /**
     * All default transitions.
     */
    protected Multimap<State, State> defaultTransitions;
    /**
     * The builder that is responsible for creating the powerset.
     */
    private DeterministicFiniteDefaultAutomatonBuilder powerSetBuilder;
    /**
     * Creates a new instance of an NFA with wildcards.
     * @param initialState the initial state.
     * @param states all states in the automaton.
     * @param transitions all transitions in the automaton.
     * @param epsilonTransitions all epsilon transitions in the automaton.
     * @param defaultTransitions all default transitions in the automaton.
     */
    public NondeterministicFiniteDefaultAutomaton(State initialState, Set<State> states, Table<State,Character, Collection<State>> transitions, Multimap<State, State> epsilonTransitions, Multimap<State, State> defaultTransitions){
        super(initialState, states, transitions, epsilonTransitions);
        this.defaultTransitions = defaultTransitions;
        this.powerSetBuilder = new DeterministicFiniteDefaultAutomatonBuilder();
    }
    /**
     * Executes a single step in the automaton.
     * The resulting collection will contain all states that can be reached using the label,
     * epsilon transitions and wildcards.
     * @param states the current states.
     * @param label the letter that has been read.
     * @return the state that is reached after using the transition with the given label.
     */
    @Override
    public Collection<State> step(Collection<State> states, char label){
        Collection<State> next = new HashSet<>();
        for(State state : states){
            if(transitions.contains(state, label))
                next.addAll(transitions.get(state, label));
            else if(defaultTransitions.containsKey(state))
                next.addAll(defaultTransitions.get(state));
        }
        return closure(next);
    }
    /**
     * @return all default transitions in this automaton.
     */
    public Multimap<State,State> getDefaultTransitions(){
        return defaultTransitions;
    }
    /**
     * @return the current instance of the automaton
     */
    @Override
    public NondeterministicFiniteDefaultAutomaton getNfa() {
        return this;
    }
    /**
     * @return the builder that is responsible for creating the powerset.
     */
    @Override
    public DeterministicFiniteDefaultAutomatonBuilder getPowerSetBuilder() {
        return powerSetBuilder;
    }
}