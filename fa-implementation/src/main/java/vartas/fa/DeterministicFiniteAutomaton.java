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
import vartas.fa.builder.NondeterministicFiniteAutomatonBuilder;
import vartas.fa.transformations.Minimize;
import vartas.fa.transformations.Reverse;

import java.util.Set;

/**
 * This class implements a deterministic finite automaton.
 */
public class DeterministicFiniteAutomaton extends FiniteAutomaton implements Reverse, Minimize {
    /**
     * The underlying transition table.
     */
    protected Table<State,Character,State> transitions;
    /**
     * The builder that is responsible for creating the reverse language.
     */
    private NondeterministicFiniteAutomatonBuilder builder;
    /**
     * Creates a new instance of a DFA
     * @param initialState the initial state.
     * @param states all states in the automaton.
     * @param transitions all transitions in the automaton.
     */
    public DeterministicFiniteAutomaton(State initialState, Set<State> states, Table<State,Character,State> transitions){
        super(initialState, states);
        this.transitions = transitions;
        this.builder = new NondeterministicFiniteAutomatonBuilder();
    }
    /**
     * Lets the word on this automaton.
     * @param word the input word.
     * @return true, if the word is accepted by the automaton.
     */
    public boolean run(String word){
        State currentState = initialState;

        for(int i = 0 ; i < word.length() && hasNext(currentState, word.charAt(i)); ++i) {
            currentState = step(currentState, word.charAt(i));
        }

        return currentState.isFinal();
    }
    /**
     * Executes a single step in the automaton.
     * @param state the current state.
     * @param label the letter that has been read.
     * @return the state that is reached after using the transition with the given label.
     */
    public State step(State state, char label){
        return transitions.get(state, label);
    }
    /**
     * @param state the current state.
     * @param label the letter that has been read.
     * @return true if there is a matching transition for that label.
     */
    protected boolean hasNext(State state, char label){
        return transitions.contains(state, label);
    }
    /**
     * @return all transitions via characters in this automaton.
     */
    public Table<State,Character,State> getTransitions(){
        return transitions;
    }

    /**
     * @return the current instance of the automaton.
     */
    @Override
    public DeterministicFiniteAutomaton getDfa() {
        return this;
    }
    /**
     * @return the builder that is responsible for creating the reverse language.
     */
    @Override
    public NondeterministicFiniteAutomatonBuilder getReverseBuilder() {
        return builder;
    }
}