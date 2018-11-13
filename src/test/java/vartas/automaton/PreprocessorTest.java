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

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import vartas.automaton.Preprocessor.Builder;
import vartas.automaton.Tokenizer.Token;

/**
 *
 * @author Zavarov
 */
public class PreprocessorTest {
    Preprocessor preprocessor;
    @Before
    public void setUp(){
        RegularExpression.Parser parser = new RegularExpression.Parser();
        Builder builder = new Builder();
        builder.addExpression(parser.parse("test"), "stuff");
        builder.addExpression(parser.parse("-"), "-");
        builder.addExpression(parser.parse("\\*"), "*");
        builder.addExpression(Builder.createQuotationMark(), "\"");
        builder.addExpression(Builder.createKeywords(), "keyword");
        builder.addExpression(Builder.createInteger(), "integer");
        builder.addExpression(Builder.createFloat(), "float");
        builder.addNumberSeparator(new Token("*","*"));
        builder.addNumberSeparator(new Token("-","-"));
        
        builder.addProcess("\"", new Preprocessor.Quotation());
        builder.addProcess("keyword", new Preprocessor.Keyword());
        builder.addProcess("-", new Preprocessor.Number());
        preprocessor = builder.build();
    }
    
    @Test
    public void processTokenIgnoreTest(){
        preprocessor.setInput("testtest");
        Token token = preprocessor.processToken();
        assertEquals(token.getLeft(),"test");
        assertEquals(token.getRight(),"stuff");
        token = preprocessor.processToken();
        assertEquals(token.getLeft(),"test");
        assertEquals(token.getRight(),"stuff");
        token = preprocessor.processToken();
        assertEquals(token,new Token(null,null,null));
    }
    
    @Test
    public void processTokenProcessTest(){
        preprocessor.setInput("\"xxtestxx\"");
        Token token = preprocessor.processToken();
        assertEquals(token.getLeft(),"xxtestxx");
        assertEquals(token.getRight(),"string");
        
        preprocessor.setInput("\"xxtestxx\"test");
         token = preprocessor.processToken();
        assertEquals(token.getLeft(),"xxtestxx");
        assertEquals(token.getRight(),"string");
        token = preprocessor.processToken();
        assertEquals(token.getLeft(),"test");
        assertEquals(token.getRight(),"stuff");
        
        preprocessor.setInput("\\\"test");
        token = preprocessor.processToken();
        assertEquals(token.getLeft(),"\"");
        assertEquals(token.getRight(),"keyword");
        token = preprocessor.processToken();
        assertEquals(token.getLeft(),"test");
        assertEquals(token.getRight(),"stuff");
    }
    
    @Test
    public void processTokenNumberMinusTest(){
        preprocessor.setInput("12345-");
        Token token = preprocessor.processToken();
        assertEquals(token,new Token("12345","integer"));
        token = preprocessor.processToken();
        assertEquals(token,new Token("-","-"));
        assertEquals(preprocessor.processToken(),new Token(null,null,null));
    }
    
    @Test
    public void processTokenMinusMinusTest(){
        preprocessor.setInput("--");
        Token token = preprocessor.processToken();
        assertEquals(token,new Token("-","-"));
        token = preprocessor.processToken();
        assertEquals(token,new Token("-","-"));
        assertEquals(preprocessor.processToken(),new Token(null,null,null));
    }
    
    @Test
    public void processTokenNumberMinusNumberTest(){
        preprocessor.setInput("12345-12345");
        Token token = preprocessor.processToken();
        assertEquals(token,new Token("12345","integer"));
        token = preprocessor.processToken();
        assertEquals(token,new Token("-","-"));
        token = preprocessor.processToken();
        assertEquals(token,new Token("12345","integer"));
        assertEquals(preprocessor.processToken(),new Token(null,null,null));
    }
    
    @Test
    public void processTokenMinusNumberTest(){
        preprocessor.setInput("-12345");
        Token token = preprocessor.processToken();
        assertEquals(token,new Token("-","-"));
        token = preprocessor.processToken();
        assertEquals(token,new Token("12345","integer"));
        assertEquals(preprocessor.processToken(),new Token(null,null,null));
    }
    
    @Test
    public void processTokenMinusNegativeNumberTest(){
        preprocessor.setInput("--12345");
        Token token = preprocessor.processToken();
        assertEquals(token,new Token("-","-"));
        token = preprocessor.processToken();
        assertEquals(token,new Token("-12345","integer"));
        assertEquals(preprocessor.processToken(),new Token(null,null,null));
    }
    
    @Test
    public void processTokenTimesNegativeNumberTest(){
        preprocessor.setInput("12345*-12345");
        Token token = preprocessor.processToken();
        assertEquals(token,new Token("12345","integer"));
        token = preprocessor.processToken();
        assertEquals(token,new Token("*","*"));
        token = preprocessor.processToken();
        assertEquals(token,new Token("-12345","integer"));
        assertEquals(preprocessor.processToken(),new Token(null,null,null));
    }
    @Test
    public void processTokenPositiveNumberTest(){
        preprocessor.setInput("12345");
        Token token = preprocessor.processToken();
        assertEquals(token,new Token("12345","integer"));
        assertEquals(preprocessor.processToken(),new Token(null,null,null));
    }
    
    @Test
    public void processTokenInvalidEndTest(){
        preprocessor.setInput("x");
        Token token = preprocessor.processToken();
        assertEquals(token,new Token(null,null,null));
    }
    
    @Test
    public void processTokenSkipTest(){
        preprocessor.setInput("xxxtest");
        Token token = preprocessor.processToken();
        assertEquals(token.getLeft(),"test");
        assertEquals(token.getRight(),"stuff");
    }
    
    @Test
    public void processTokenInvalidProcessTest(){
        preprocessor.setInput("\"test");
        Token token = preprocessor.processToken();
        assertEquals(token,new Token(null,null,null));
        
        preprocessor.setInput("\"");
         token = preprocessor.processToken();
        assertEquals(token,new Token(null,null,null));
    }
    
    @Test
    public void applyIgnoreTest(){
        Token token = new Token("stuff",null,"test");
        assertEquals(token,preprocessor.apply(token));
    }
    
    @Test
    public void applyProcessTest(){
        Token token = new Token("\\\"",null,"keyword");
        token = preprocessor.apply(token);
        assertEquals(token.getLeft(),"\"");
        assertEquals(token.getRight(),"keyword");
    }
}
