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

package vartas.fa.visitor;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import vartas.fa.DeterministicFiniteAutomaton;
import vartas.fa.State;

/**
 * This interface implements a visitor over all transitions in a DFA.
 */
public interface DeterministicFiniteAutomatonTransitionVisitor {
    /**
     * A table over all transitions that have already been visited.
     */

    Table<State, Character, State> visitedTransitions = HashBasedTable.create();
    /**
     * @return the DFA this visitor traverses over.
     */
    DeterministicFiniteAutomaton getDfa();

    /**
     * Calls the handle method for all outgoing labels.
     * @param from a state in the automaton.
     */
    default void handle(State from){
        getDfa().getTransitions().row(from).keySet().forEach(with -> handle(from, with));
    }

    /**
     * Calls the handle method for all outgoing labels and the states that are reached after consuming it.
     * @param from a state in the automaton.
     * @param with the label of an outgoing transition.
     */
    default void handle(State from, char with){
        handle(from, with, getDfa().step(from, with));
    }

    /**
     * Visits the incoming and outgoing state, visits the transition and then
     * calls the handle method for the next state if this transition hasn't already been visited.
     * @param from the current state.
     * @param with a transition label.
     * @param to the next state.
     */
    default void handle(State from, char with, State to){
        if(visitedTransitions.contains(from, with))
            return;
        visitedTransitions.put(from, with, to);

        visit(from);
        visit(to);
        visit(from, with, to);
        handle(to);
    }

    /**
     * This method is called for the states of every transition before the actual transition is visited.
     * @param state a state in the automaton.
     */
    void visit(State state);

    /**
     * This method is called when a transition is visited.
     * @param from the current state.
     * @param with a transition label.
     * @param to the next state.
     */
    void visit(State from, char with, State to);
}
