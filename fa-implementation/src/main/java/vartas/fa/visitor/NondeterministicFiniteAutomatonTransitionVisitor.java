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
import vartas.fa.NondeterministicFiniteAutomaton;
import vartas.fa.State;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This interface implements a visitor over all transitions in an NFA.
 */
public interface NondeterministicFiniteAutomatonTransitionVisitor{
    /**
     * A table over all transitions that have already been visited.
     */

    Table<Collection<State>, Character, Collection<State>> visitedTransitions = HashBasedTable.create();
    /**
     * @return the NFA this visitor traverses over.
     */
    NondeterministicFiniteAutomaton getNfa();

    /**
     * Calls the handle method for all outgoing labels.
     * @param from a collection of states in the automaton.
     */
    default void handle(Collection<State> from){
        Set<Character> labels = from
                .stream()
                .map(state -> getNfa().getTransitions().row(state).keySet())
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        for(char with : labels)
            handle(from, with);
    }

    /**
     * Calls the handle method for all outgoing labels and the states that are reached after consuming it.
     * @param from a collection of states in the automaton.
     * @param with the label of an outgoing transition.
     */
    default void handle(Collection<State> from, char with){
        handle(from, with, getNfa().step(from, with));
    }

    /**
     * Visits the incoming and outgoing collection of states, visits the transition and then
     * calls the handle method for the next collection of states if this transition hasn't already been visited.
     * @param from the current states.
     * @param with a transition label.
     * @param to the next states.
     */
    default void handle(Collection<State> from, char with, Collection<State> to){
        if(visitedTransitions.contains(from, with))
            return;
        visitedTransitions.put(from, with, to);

        visit(from);
        visit(to);
        visit(from, with, to);
        handle(to);
    }

    /**
     * This method is called for the collection of states of every transition before the actual transition is visited.
     * @param state a collection of states in the automaton.
     */
    void visit(Collection<State> state);

    /**
     * This method is called when a transition is visited.
     * @param from the current states.
     * @param with a transition label.
     * @param to the next states.
     */
    void visit(Collection<State> from, char with, Collection<State> to);
}
