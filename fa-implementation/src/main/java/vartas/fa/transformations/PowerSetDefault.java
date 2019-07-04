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

import vartas.fa.DeterministicFiniteDefaultAutomaton;
import vartas.fa.State;
import vartas.fa.builder.DeterministicFiniteDefaultAutomatonBuilder;
import vartas.fa.visitor.NondeterministicFiniteDefaultAutomatonTransitionVisitor;

import java.util.Collection;

/**
 * This interfaces adds the ability to transform NFAs into DFAs by applying the power set construction.
 */
public interface PowerSetDefault extends PowerSet, NondeterministicFiniteDefaultAutomatonTransitionVisitor {
    /**
     * @return the DFA builder for the power set.
     */
    @Override
    DeterministicFiniteDefaultAutomatonBuilder getPowerSetBuilder();

    /**
     * @return a DFA that is equivalent to the current NFA.
     */
    @Override
    default DeterministicFiniteDefaultAutomaton powerSet(){
        getPowerSetBuilder().clear();
        groups.clear();
        visitedTransitions.clear();
        visitedDefaultTransitions.clear();

        accept(getNfa());

        return getPowerSetBuilder().build();
    }

    /**
     * Adds an 'else' transition over the given states to the DFA.
     * @param from the states the NFA currently is in.
     * @param to the states in the NFA that are reached after taking this transition.
     */
    @Override
    default void visit(Collection<State> from, Collection<State> to){
        getPowerSetBuilder().addDefaultTransition(groups.get(from), groups.get(to));
    }
}
