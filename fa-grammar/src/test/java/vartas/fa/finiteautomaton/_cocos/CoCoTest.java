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
import vartas.fa.AbstractTest;
import org.junit.Before;
import org.junit.Test;

public class CoCoTest extends AbstractTest {
    FiniteAutomatonCoCoChecker checker;

    @Before
    public void setUp(){
        Log.getFindings().clear();
        checker = new FiniteAutomatonCoCoChecker();
    }

    @Test
    public void testNoFinalState(){
        checker.addCoCo(new HasFinalStateCoCo());
        ASTFiniteAutomaton automaton = parseInvalidModel("NoFinalState");

        checker.checkAll(automaton);

        checkForFindings(HasFinalStateCoCo.ERROR_MESSAGE);
    }
    @Test
    public void testNoInitialState(){
        checker.addCoCo(new HasInitialStateCoCo());
        ASTFiniteAutomaton automaton = parseInvalidModel("NoInitialState");

        checker.checkAll(automaton);

        checkForFindings(HasInitialStateCoCo.ERROR_MESSAGE);
    }
    @Test
    public void testTwoInitialStates(){
        checker.addCoCo(new OnlyOneInitialStateCoCo());
        ASTFiniteAutomaton automaton = parseInvalidModel("TwoInitialStates");

        checker.checkAll(automaton);

        checkForFindings(String.format(OnlyOneInitialStateCoCo.ERROR_MESSAGE,2));
    }
    @Test
    public void testUnreachabelStates(){
        checker.addCoCo(new AllStatesAreReachableCoCo());
        ASTFiniteAutomaton automaton = parseInvalidModel("UnreachableStates");

        checker.checkAll(automaton);

        checkForFindings(String.format(AllStatesAreReachableCoCo.ERROR_MESSAGE,"C"));

    }
    @Test
    public void testDuplicateState(){
        checker.addCoCo(new StateNamesAreUniqueCoCo());
        ASTFiniteAutomaton automaton = parseInvalidModel("DuplicateStates");

        checker.checkAll(automaton);

        checkForFindings(String.format(StateNamesAreUniqueCoCo.ERROR_MESSAGE,"A"));

    }
    @Test
    public void testUnknownOutgoingTransitionState(){
        checker.addCoCo(new TransitionStatesExist());
        ASTFiniteAutomaton automaton = parseInvalidModel("UnknownOutgoingTransitionState");

        checker.checkAll(automaton);

        checkForFindings(String.format(TransitionStatesExist.ERROR_MESSAGE,"B"));

    }
    @Test
    public void testUnknownIncomingTransitionState(){
        checker.addCoCo(new TransitionStatesExist());
        ASTFiniteAutomaton automaton = parseInvalidModel("UnknownIncomingTransitionState");

        checker.checkAll(automaton);

        checkForFindings(String.format(TransitionStatesExist.ERROR_MESSAGE,"A"));

    }
    @Test
    public void testSelfLoop(){
        checker = FiniteAutomatonCoCos.getCheckerForAllCoCos();
        ASTFiniteAutomaton automaton = parseValidModel("SelfLoop");

        checker.checkAll(automaton);

        checkForFindings();
    }

    @Test
    public void testSimple(){
        checker = FiniteAutomatonCoCos.getCheckerForAllCoCos();
        ASTFiniteAutomaton automaton = parseValidModel("Simple");

        checker.checkAll(automaton);

        checkForFindings();
    }

    @Test
    public void testWithLoop(){
        checker = FiniteAutomatonCoCos.getCheckerForAllCoCos();
        ASTFiniteAutomaton automaton = parseValidModel("WithLoop");

        checker.checkAll(automaton);

        checkForFindings();
    }
}