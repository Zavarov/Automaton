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

package vartas.fa.regularexpression;import org.junit.Test;
import vartas.fa.FiniteAutomaton;
import vartas.fa.regularexpression._ast.ASTRegularExpressionArtifact;
import vartas.fa.regularexpression._parser.RegularExpressionParser;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

public class RegularExpressionCreatorTest {

    protected FiniteAutomaton build(String expression){
        ASTRegularExpressionArtifact ast = parse(expression);
        return RegularExpressionCreator.createFrom(ast);
    }

    protected ASTRegularExpressionArtifact parse(String expression){
        try{
            RegularExpressionParser parser = new RegularExpressionParser();
            Optional<ASTRegularExpressionArtifact> ast = parser.parse_StringRegularExpressionArtifact(expression);
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
    public void testKleene(){
        FiniteAutomaton fa = build("a*");
        assertThat(fa.run("")).isTrue();
        assertThat(fa.run("a")).isTrue();
        assertThat(fa.run("aa")).isTrue();
        assertThat(fa.run("b")).isFalse();
        assertThat(fa.run("ba")).isFalse();
        assertThat(fa.run("ab")).isFalse();
    }

    @Test
    public void testUnion(){
        FiniteAutomaton fa = build("a+b");
        assertThat(fa.run("")).isFalse();
        assertThat(fa.run("a")).isTrue();
        assertThat(fa.run("aa")).isFalse();
        assertThat(fa.run("b")).isTrue();
        assertThat(fa.run("ba")).isFalse();
        assertThat(fa.run("ab")).isFalse();
    }

    @Test
    public void testConcatenation(){
        FiniteAutomaton fa = build("ab");
        assertThat(fa.run("")).isFalse();
        assertThat(fa.run("a")).isFalse();
        assertThat(fa.run("ab")).isTrue();
        assertThat(fa.run("ba")).isFalse();
        assertThat(fa.run("b")).isFalse();
        assertThat(fa.run("aba")).isFalse();
    }

    @Test
    public void testComposition(){
        FiniteAutomaton fa = build("a+.'c'*");
        assertThat(fa.run("")).isFalse();
        assertThat(fa.run("a")).isTrue();
        assertThat(fa.run("aa")).isFalse();
        assertThat(fa.run("b")).isTrue();
        assertThat(fa.run("ac")).isTrue();
        assertThat(fa.run("bcc")).isTrue();
        assertThat(fa.run("bbc")).isFalse();
    }

    @Test
    public void testInterval(){
        FiniteAutomaton fa = build("[b-c]");
        assertThat(fa.run("")).isFalse();
        assertThat(fa.run("a")).isFalse();
        assertThat(fa.run("b")).isTrue();
        assertThat(fa.run("c")).isTrue();
        assertThat(fa.run("d")).isFalse();
        assertThat(fa.run("bb")).isFalse();
        assertThat(fa.run("cc")).isFalse();
    }

    @Test
    public void testGetRealThis(){
        RegularExpressionCreator creator = new RegularExpressionCreator();
        assertThat(creator.getRealThis()).isEqualTo(creator);
    }

    @Test
    public void testSetRealThis(){
        RegularExpressionCreator creator = new RegularExpressionCreator();
        assertThat(creator.getRealThis()).isEqualTo(creator);
        creator.setRealThis(null);
        assertThat(creator.getRealThis()).isNull();
    }
}
