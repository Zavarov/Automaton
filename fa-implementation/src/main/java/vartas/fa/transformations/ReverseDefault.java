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

package vartas.fa.transformations;

import vartas.fa.NondeterministicFiniteDefaultAutomaton;
import vartas.fa.State;
import vartas.fa.builder.NondeterministicFiniteDefaultAutomatonBuilder;
import vartas.fa.visitor.DeterministicFiniteDefaultAutomatonTransitionVisitor;

import java.util.Set;

/**
 * This interfaces adds the ability to reverse DFAs with infinite alphabets.
 */
public interface ReverseDefault extends Reverse, DeterministicFiniteDefaultAutomatonTransitionVisitor {
    /**
     * @return the NFA builder for the reverse language.
     */
    @Override
    NondeterministicFiniteDefaultAutomatonBuilder getReverseBuilder();
    /**
     * @return a NFA accept the reverse language of the current DFA.
     */
    @Override
    default NondeterministicFiniteDefaultAutomaton reverse(){
        getReverseBuilder().clear();
        visitedDefaultTransitions.clear();
        visitedTransitions.clear();
        map.clear();

        accept(getDfa());

        return getReverseBuilder().build();
    }

    /**
     * Adds an 'else' transition over the given state to the NFA.
     * This transition will go from the next state to the current state.
     * In addition, it will also add transitions to a sink state for all other labels, since
     * our NFA will always take all possible branches and we have to make ensure that the 'else' transition
     * is only taken when there is no other valid choice.
     * @param from the states the NFA currently is in.
     * @param to the states in the NFA that are reached after taking this transition.
     */
    @Override
    default void visit(State from, State to){
        //We have to add a sink states for all transitions that are not covered by the default transition
        //When reversing the automaton.
        Set<Character> labels = getDfa().getTransitions().row(to).keySet();
        if(!labels.isEmpty()) {
            State sink = getReverseBuilder().addState();
            map.put(sink, sink);
            for(char with : labels)
                handle(sink, with, to);
        }

        getReverseBuilder().addDefaultTransition(map.get(to), map.get(from));
    }
}
