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

import org.junit.Before;
import org.junit.Test;
import vartas.fa.AbstractTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

public class ASTFiniteAutomatonTest extends AbstractTest {
    ASTFiniteAutomaton ast;
    @Before
    public void setUp(){
        ast = parseValidModel("Simple");
    }
    @Test
    public void testConstructor(){
        ASTFiniteAutomaton copy = new ASTFiniteAutomaton(ast.getName(), ast.getStateList(), ast.getTransitionList());

        assertTrue(ast.deepEquals(copy));
    }
    @Test
    public void testGetInitialState(){
        Optional<ASTState> state = ast.getInitialState();

        assertThat(state).isPresent();
        assertThat(state.get().getName()).isEqualTo("A");
    }
    @Test
    public void testGetFinalState(){
        List<ASTState> states = ast.getFinalStates();

        assertThat(states).hasSize(1);
        assertThat(states.get(0).getName()).isEqualTo("B");
    }
}
