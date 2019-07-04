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

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This class tests if all state names in the automaton are unique.
 */
public class StateNamesAreUniqueCoCo implements FiniteAutomatonASTFiniteAutomatonCoCo{
    /**
     * The error message that is throw.
     * As a parameter, it requires the name of one the duplicate states.
     */
    public static final String ERROR_MESSAGE = "There are two or more states with the name %s.";

    /**
     * Goes through all states and counts the names.
     * If a name appears more than once, throw an error.
     * @param node the root node of the automaton.
     */
    @Override
    public void check(ASTFiniteAutomaton node) {
        Map<String, Long> counts = node
                .getStateList()
                .stream()
                .map(ASTState::getName)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        for(Map.Entry<String, Long> count : counts.entrySet()){
            if(count.getValue() != 1){
                Log.error(String.format(ERROR_MESSAGE, count.getKey()));
            }
        }
    }
}
