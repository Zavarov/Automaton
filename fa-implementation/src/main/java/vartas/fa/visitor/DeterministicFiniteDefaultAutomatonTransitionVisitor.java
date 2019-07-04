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

import vartas.fa.DeterministicFiniteDefaultAutomaton;
import vartas.fa.State;

import java.util.HashMap;
import java.util.Map;

/**
 * This interface implements a visitor over all transitions in a DFA with an infinite alphabet.
 */
public interface DeterministicFiniteDefaultAutomatonTransitionVisitor extends DeterministicFiniteAutomatonTransitionVisitor{
    /**
     * A map over all 'else' transitions that have already been visited.
     */
    Map<State, State> visitedDefaultTransitions = new HashMap<>();
    /**
     * @return the DFA this visitor traverses over.
     */
    @Override
    DeterministicFiniteDefaultAutomaton getDfa();

    /**
     * Calls the handle method for all outgoing labels.
     * In addition, it also handles all 'else' transitions.
     * @param from a state in the automaton.
     */
    @Override
    default void handle(State from){
        DeterministicFiniteAutomatonTransitionVisitor.super.handle(from);

        if(getDfa().getDefaultTransitions().containsKey(from))
            handle(from, getDfa().getDefaultTransitions().get(from));
    }

    /**
     * Visits the incoming and outgoing state, visits the 'else' transition and then
     * calls the handle method for the next state if this transition hasn't already been visited.
     * @param from the current state.
     * @param to the next state.
     */
    default void handle(State from, State to){
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
     * @param from the current state.
     * @param to the next state.
     */
    void visit(State from, State to);
}