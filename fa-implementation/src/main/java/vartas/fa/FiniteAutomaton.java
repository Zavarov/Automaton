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

import java.util.Set;

/**
 * This class is the body for all finite automaton.
 */
public abstract class FiniteAutomaton{
    /**
     * The initial state.
     */
    protected State initialState;
    /**
     * All states in the automaton.
     */
    protected Set<State> states;
    /**
     * Creates a new instance of the finite automaton.
     * @param initialState the initial state.
     * @param states all states in the automaton.
     */
    protected FiniteAutomaton(State initialState, Set<State> states){
        this.initialState = initialState;
        this.states = states;
    }
    /**
     * Lets the word on this automaton.
     * @param word the input word.
     * @return true, if the word is accepted by the automaton.
     */
    public abstract boolean run(String word);
    /**
     * @return all states in the automaton.
     */
    public Set<State> getStates(){
        return states;
    }
    /**
     * @return the initial state of the automaton.
     */
    public State getInitialState(){
        return initialState;
    }
}