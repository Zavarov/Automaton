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

package vartas.fa.finiteautomaton._cocos;

import de.se_rwth.commons.logging.Log;
import vartas.fa.finiteautomaton._ast.ASTFiniteAutomaton;
import vartas.fa.finiteautomaton._ast.ASTState;
import vartas.fa.finiteautomaton._visitor.StateVisitor;

import java.util.HashSet;
import java.util.Set;

/**
 * This class tests if all states are reachable.
 */
public class AllStatesAreReachableCoCo extends StateVisitor implements FiniteAutomatonASTFiniteAutomatonCoCo {
    /**
     * The error message that is throw.
     * As a parameter, it requires the name of the state.
     */
    public static final String ERROR_MESSAGE = "The state %s is not reachable.";

    /**
     * Starts at the initial state and go through all reachable states.
     * If the number of visited states differs from the total number of states, throw an error.
     * @param node the root node of the automaton.
     */
    @Override
    public void check(ASTFiniteAutomaton node){
        node.accept(getRealThis());

        if(visitedStates.size() != node.getStateList().size()){
            Set<ASTState> difference = new HashSet<>(node.getStateList());
            difference.removeAll(visitedStates);
            Log.error(String.format(ERROR_MESSAGE, difference.iterator().next().getName()));
        }
    }
}
