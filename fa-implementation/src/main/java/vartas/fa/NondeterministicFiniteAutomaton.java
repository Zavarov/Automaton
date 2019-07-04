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
import vartas.fa.builder.DeterministicFiniteAutomatonBuilder;
import vartas.fa.transformations.PowerSet;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class implements a nondeterministic finite automaton.
 */
public class NondeterministicFiniteAutomaton extends FiniteAutomaton implements PowerSet {
    /**
     * The underlying transition table.
     */
    protected Table<State,Character,Collection<State>> transitions;
    /**
     * All epsilon transitions
     */
    protected Multimap<State, State> epsilonTransitions;
    /**
     * The builder that is responsible for creating the powerset.
     */
    private DeterministicFiniteAutomatonBuilder builder;
    /**
     * Creates a new instance of an NFA.
     * @param initialState the initial state.
     * @param states all states in the automaton.
     * @param transitions all transitions in the automaton.
     * @param epsilonTransitions all epsilon transitions in the automaton.
     */
    public NondeterministicFiniteAutomaton(State initialState, Set<State> states, Table<State,Character,Collection<State>> transitions, Multimap<State, State> epsilonTransitions){
        super(initialState, states);
        this.transitions = transitions;
        this.epsilonTransitions = epsilonTransitions;
        this.builder = new DeterministicFiniteAutomatonBuilder();
    }
    /**
     * Lets the word on this automaton.
     * @param word the input word.
     * @return true, if the word is accepted by the automaton.
     */
    public boolean run(String word){
        Collection<State> currentStates = closure(initialState);

        //Abort if there are no more states left
        for(int i = 0 ; i < word.length() && !currentStates.isEmpty() ; ++i)
            currentStates = step(currentStates, word.charAt(i));

        //Check on the closure of the current state to reach possible final states
        return currentStates.stream().anyMatch(State::isFinal);
    }
    /**
     * Executes a single step in the automaton.
     * @param states the current states.
     * @param label the letter that has been read.
     * @return the state that is reached after using the transition with the given label.
     */
    public Collection<State> step(Collection<State> states, char label){
        Collection<State> next = states.stream()
                .filter(state -> transitions.contains(state, label))
                .map(state -> transitions.get(state, label))
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        return closure(next);
    }
    /**
     * @param state the origin.
     * @return a set of all states that can be reached using epsilon transitions, the given state included.
     */
    public Set<State> closure(State state){
        Set<State> states = new HashSet<>();

        Deque<State> remaining = new LinkedList<>();
        remaining.add(state);

        while(!remaining.isEmpty()){
            State current = remaining.pop();
            //Get all states reachable via epsilon transitions
            remaining.addAll(epsilonTransitions.get(current));
            //Ignore all states that have already been visitedWildcards
            remaining.removeAll(states);

            states.add(current);
        }
        return states;
    }
    /**
     * @param states all current states the automaton is in
     * @return a set of all states that can be reached using epsilon transitions, the given states included.
     */
    public Set<State> closure(Collection<State> states){
        return states.stream().map(this::closure).flatMap(Collection::stream).collect(Collectors.toSet());
    }
    /**
     * @return all transitions via characters in this automaton.
     */
    public Table<State,Character,Collection<State>> getTransitions(){
        return transitions;
    }
    /**
     * @return all epsilon transitions in this automaton.
     */
    public Multimap<State,State> getEpsilonTransitions(){
        return epsilonTransitions;
    }
    /**
     * @return the current instance of the automaton
     */
    @Override
    public NondeterministicFiniteAutomaton getNfa() {
        return this;
    }
    /**
     * @return the builder that is responsible for creating the powerset.
     */
    @Override
    public DeterministicFiniteAutomatonBuilder getPowerSetBuilder() {
        return builder;
    }
}
