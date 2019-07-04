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

import vartas.fa.NondeterministicFiniteDefaultAutomaton;
import vartas.fa.State;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This interface implements a visitor over all transitions in an NFA with an infinite alphabet.
 */
public interface NondeterministicFiniteDefaultAutomatonTransitionVisitor extends NondeterministicFiniteAutomatonTransitionVisitor {
    /**
     * A map over all 'else' transitions that have already been visited.
     */
    Map<Collection<State>, Collection<State>> visitedDefaultTransitions = new HashMap<>();
    /**
     * @return the NFA this visitor traverses over.
     */
    @Override
    NondeterministicFiniteDefaultAutomaton getNfa();

    /**
     * Calls the handle method for all outgoing labels.
     * In addition, it also handles all 'else' transitions.
     * @param from a collection of states in the automaton.
     */
    @Override
    default void handle(Collection<State> from){
        NondeterministicFiniteAutomatonTransitionVisitor.super.handle(from);

        //Deal with the default transitions
        Set<State> to = from
                .stream()
                .map(state -> getNfa().getDefaultTransitions().get(state))
                .map(states -> getNfa().closure(states))
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
        handle(from, to);
    }

    /**
     * Visits the incoming and outgoing states, visits the 'else' transition and then
     * calls the handle method for the next states if this transition hasn't already been visited.
     * @param from the current states.
     * @param to the next states.
     */
    default void handle(Collection<State> from, Collection<State> to){
        if(visitedDefaultTransitions.containsKey(from))
            return;
        visitedDefaultTransitions.put(from, to);

        visit(from);
        visit(to);
        visit(from, to);
        handle(to);
    }

    /**
     * This method is called when an 'else' transition is visited.
     * @param from the current states.
     * @param to the next states.
     */
    void visit(Collection<State> from, Collection<State> to);
}
