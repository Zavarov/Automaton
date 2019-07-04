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

import java.util.Optional;

/**
 * This class tests if the automaton has at least one final state.
 */
public class HasFinalStateCoCo implements FiniteAutomatonASTFiniteAutomatonCoCo {
    /**
     * The error message that is throw.
     */
    public static final String ERROR_MESSAGE = "This automaton doesn't have a final state.";

    /**
     * Goes through all states in an attempt to find final states.
     * If none are found, throw an error.
     * @param node the root node of the automaton.
     */
    @Override
    public void check(ASTFiniteAutomaton node){
        Optional<ASTState> state = node.getStateList().stream().filter(ASTState::isFinal).findAny();
        if(!state.isPresent())
            Log.error(ERROR_MESSAGE);
    }
}
