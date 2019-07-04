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

package vartas.fa.finiteautomaton._ast;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Extends the root node of the finite automaton
 * to compute both the initial state and the final states.
 */
public class ASTFiniteAutomaton extends ASTFiniteAutomatonTOP{
    /**
     * Creates an empty finite automaton.
     */
    protected  ASTFiniteAutomaton (){
        super();
    }

    /**
     * Creates a new instance of a finite automaton.
     * @param name the name of the automaton.
     * @param states the states in the automaton.
     * @param transitions the transitions in the automaton.
     */
    protected  ASTFiniteAutomaton(String name, List<ASTState> states, List<ASTTransition> transitions){
        super(name, states, transitions);
    }

    /**
     * @return all final states in the automaton.
     */
    public List<ASTState> getFinalStates(){
        return states.stream().filter(ASTState::isFinal).collect(Collectors.toList());
    }

    /**
     * @return the initial state.
     */
    public Optional<ASTState> getInitialState(){
        return states.stream().filter(ASTState::isInitial).findAny();
    }
}