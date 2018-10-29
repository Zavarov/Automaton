/*
 * Copyright (C) 2018 Zavarov
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
package vartas.automaton;

import java.util.Arrays;
import java.util.stream.Collectors;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import vartas.automaton.Preprocessor.Builder;

/**
 *
 * @author Zavarov
 */
public class PreprocessorBuilderTest {
    Builder builder;
    @Before
    public void setUp(){
        builder = new Builder();
        builder.addExpression(Builder.createQuotationMark(), "\"");
        builder.addExpression(Builder.createKeywords(), "keyword");
        builder.addExpression(Builder.createInteger(), "integer");
        builder.addExpression(Builder.createFloat(), "float");
        
        builder.addProcess("\"", new Preprocessor.Quotation());
        builder.addProcess("keyword", new Preprocessor.Keyword());
        builder.addProcess("-", new Preprocessor.Number());
    }
    
    @Test
    public void createQuotationMarkTest(){
        RegularExpression expression = Builder.createQuotationMark();
        assertTrue(expression.run(Arrays.asList((int)'"')));
    }
    
    @Test
    public void createKeywordsTest(){
        RegularExpression expression = Builder.createKeywords();
        assertTrue(expression.run("\\\"".chars().boxed().collect(Collectors.toList())));
    }
    
    @Test
    public void createIntegerTest(){
        RegularExpression expression = Builder.createInteger();
        assertTrue(expression.run("0".chars().boxed().collect(Collectors.toList())));
        assertTrue(expression.run("1000".chars().boxed().collect(Collectors.toList())));
        assertTrue(expression.run("1234456789".chars().boxed().collect(Collectors.toList())));
        assertFalse(expression.run("01".chars().boxed().collect(Collectors.toList())));
    }
    
    @Test
    public void createFloatTest(){
        RegularExpression expression = Builder.createFloat();
        assertTrue(expression.run("1000.54321".chars().boxed().collect(Collectors.toList())));
        assertTrue(expression.run("1000.543210".chars().boxed().collect(Collectors.toList())));
        assertTrue(expression.run("100e10".chars().boxed().collect(Collectors.toList())));
        assertTrue(expression.run("100E10".chars().boxed().collect(Collectors.toList())));
        assertFalse(expression.run("1000".chars().boxed().collect(Collectors.toList())));
    }
    
    @Test
    public void buildTest(){
        Preprocessor p = builder.build();
        assertTrue(p.run("\\\"".chars().boxed().collect(Collectors.toList())));
        assertTrue(p.run("0".chars().boxed().collect(Collectors.toList())));
        assertTrue(p.run("1000".chars().boxed().collect(Collectors.toList())));
        assertTrue(p.run("1234456789".chars().boxed().collect(Collectors.toList())));
        assertTrue(p.run("1000.54321".chars().boxed().collect(Collectors.toList())));
        assertTrue(p.run("1000.543210".chars().boxed().collect(Collectors.toList())));
        assertTrue(p.run("100e10".chars().boxed().collect(Collectors.toList())));
        assertTrue(p.run("100E10".chars().boxed().collect(Collectors.toList())));
    }
}
