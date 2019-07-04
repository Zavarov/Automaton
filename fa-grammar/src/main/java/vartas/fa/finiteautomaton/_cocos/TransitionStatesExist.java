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
import vartas.fa.finiteautomaton._ast.ASTTransition;
import vartas.fa.finiteautomaton._symboltable.StateSymbol;

import java.util.Optional;

/**
 * This class tests if both the incoming and outgoing state of a transition exists.
 */
public class TransitionStatesExist implements  FiniteAutomatonASTTransitionCoCo{
    /**
     * The error message that is throw.
     * As a parameter, it requires the name of the state.
     */
    public static final String ERROR_MESSAGE = "There state %s doesn't exist.";

    /**
     * Uses the symbol table to resolve the names of the states.
     * If at least one of the states doesn't exist, throw an error.
     * @param node a transition node in the automaton.
     */
    @Override
    public void check(ASTTransition node) {
        Optional<StateSymbol> from = node.getEnclosingScope().resolve(node.getFrom(), StateSymbol.KIND);
        Optional<StateSymbol> to = node.getEnclosingScope().resolve(node.getTo(), StateSymbol.KIND);

        if(!from.isPresent())
            Log.error(String.format(ERROR_MESSAGE, node.getFrom()));
        if(!to.isPresent())
            Log.error(String.format(ERROR_MESSAGE, node.getTo()));
    }
}
