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

package vartas.fa.finiteautomaton;

import org.junit.Test;
import vartas.fa.FiniteAutomaton;
import vartas.fa.finiteautomaton._ast.ASTFiniteAutomaton;
import vartas.fa.finiteautomaton._parser.FiniteAutomatonParser;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

public class FiniteAutomatonCreatorTest {
    String modelPath = "src/test/resources/fa/created/";

    protected FiniteAutomaton build(String expression){
        ASTFiniteAutomaton ast = parse(expression);
        return FiniteAutomatonCreator.createFrom(ast);
    }

    protected ASTFiniteAutomaton parse(String fileName){
        try{
            FiniteAutomatonParser parser = new FiniteAutomatonParser();
            Optional<ASTFiniteAutomaton> ast = parser.parse(modelPath + fileName);
            if(parser.hasErrors() || !ast.isPresent()){
                fail("Parse failed");
                return null;
            }else{
                return ast.get();
            }
        }catch(IOException e){
            fail(e.getMessage());
            return null;
        }
    }

    @Test
    public void testSimple(){
        //L = a*
        FiniteAutomaton fa = build("Loop.fa");
        assertThat(fa.run("")).isTrue();
        assertThat(fa.run("a")).isTrue();
        assertThat(fa.run("aa")).isTrue();
        assertThat(fa.run("b")).isFalse();
        assertThat(fa.run("ba")).isFalse();
        assertThat(fa.run("ab")).isFalse();
    }

    @Test
    public void testEpsilon(){
        //L = ab
        FiniteAutomaton fa = build("Epsilon.fa");
        assertThat(fa.run("")).isFalse();
        assertThat(fa.run("a")).isFalse();
        assertThat(fa.run("ab")).isTrue();
        assertThat(fa.run("ba")).isFalse();
        assertThat(fa.run("b")).isFalse();
        assertThat(fa.run("aba")).isFalse();
    }

    @Test
    public void testDefault(){
        //L = a + _c*
        FiniteAutomaton fa = build("Default.fa");
        assertThat(fa.run("")).isFalse();
        assertThat(fa.run("a")).isTrue();
        assertThat(fa.run("aa")).isFalse();
        assertThat(fa.run("b")).isTrue();
        assertThat(fa.run("ac")).isTrue();
        assertThat(fa.run("bcc")).isTrue();
        assertThat(fa.run("bbc")).isFalse();
    }

    @Test
    public void testGetRealThis(){
        FiniteAutomatonCreator creator = new FiniteAutomatonCreator();
        assertThat(creator.getRealThis()).isEqualTo(creator);
    }

    @Test
    public void testSetRealThis(){
        FiniteAutomatonCreator creator = new FiniteAutomatonCreator();
        assertThat(creator.getRealThis()).isEqualTo(creator);
        creator.setRealThis(null);
        assertThat(creator.getRealThis()).isNull();
    }
}
