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

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import vartas.automaton.Tokenizer.Token;

/**
 * The preprocessor is a modifier version of the lexer that refines the
 * token and deals with possible errors before they're passed on.
 */
public class Preprocessor extends Lexer implements Function<Token,Token>{
    /**
     * The identifier the content within two quotation marks.
     */
    public static final String QUOTATION = "quotation";
    /**
     * The token that is returned in case an error occured.
     */
    public static final Token ERROR = new Token(null,null,null);
    /**
     * The processes for the identifier.
     */
    protected final Map<String,Process> processes;
    /**
     * The last token that has been read.
     */
    protected Token last_token;
    /**
     * @param dfa the underlying automaton.
     * @param identifier a map that assigns each final state an identifier.
     * @param processes the map that assignes a process to tags.
     */
    public Preprocessor(DFA dfa, Map<Integer,String> identifier, Map<String,Process> processes){
        super(dfa,identifier);
        this.processes = processes;
    }
    /**
     * Stores the data and sets the last token back to null.
     * @param data the data that is processed.
     */
    @Override
    public void setInput(String data){
        super.setInput(data);
        last_token = null;
    }
    
    /**
     * Returns the next valid token and applies a process defined by the identifier.
     * @return the next valid token or (null,null,null).
     */
    public Lexer.Token processToken(){
        Token current_token;
        if(hasNext()){
            Lexer.Token token;
            //Skip all invalid tokens
            do{
                token = nextToken();
            }while(hasNext() && token.getRight() == null);
            current_token = token.getRight() == null ? ERROR : apply(token);
        }else{
            current_token = ERROR;
        }
        last_token = current_token;
        return current_token;
    }
    /**
     * Preprocesses the token, if a proces for the indentifier was specified.
     * Otherwise the original token will be returned.
     * @param t the token that has been read.
     * @return the preprocessed token.
     */
    @Override
    public Lexer.Token apply(Lexer.Token t){
        return processes.containsKey(t.getRight()) ? processes.get(t.getRight()).apply(t, this) : t;
    }
    /**
     * An implementation of the builder made for preprocessors.
     */
    public static class Builder extends Lexer.Builder{
        /**
         * The processes for the identifier.
         */
        protected final Map<String,Process> processes = new Object2ObjectOpenHashMap<>();
        /**
         * Adds a process for a specified tag.
         * @param tag the tag on which the process is executed.
         * @param process the process that is executed.
         */
        public void addProcess(String tag, Process process){
            processes.put(tag, process);
        }
        /**
         * @return a preprocessor over the given input. 
         */
        @Override
        public Preprocessor build(){
            Lexer lexer = super.build();
            return new Preprocessor(lexer,lexer.identifier,new Object2ObjectOpenHashMap<>(processes));
        }
        /**
         * Initializes the builder with expressions for the quotation mark,
         * integer, floats and the keywords.
         * @return the regular expression accepting the quotation mark.
         */
        public final static RegularExpression createQuotationMark(){
            return RegularExpression.singleton('"');
        }
        /**
         * @return a regular expression for all the keywords that are used so far.
         */
        public final static RegularExpression createKeywords(){
            RegularExpression.Parser parser = new RegularExpression.Parser();
            return parser.parse("\\\\\"");
        }
        /**
         * @return a regular expression for positive integers. 
         */
        public final static RegularExpression createInteger(){
            RegularExpression.Parser parser = new RegularExpression.Parser();
            return parser.parse("(1+2+3+4+5+6+7+8+9)(0+1+2+3+4+5+6+7+8+9)*+0");
        }
        /**
         * @return a regular expression for positive floating point numbers.
         */
        public final static RegularExpression createFloat(){
            RegularExpression.Parser parser = new RegularExpression.Parser();
            RegularExpression epsilon = parser.parse("");
            
            RegularExpression integer = createInteger();
            RegularExpression digit = parser.parse("(0+1+2+3+4+5+6+7+8+9)(0+1+2+3+4+5+6+7+8+9)*");
            RegularExpression point = parser.parse(".");
            RegularExpression number = RegularExpression.concatenation(integer, point, digit);
            RegularExpression e = parser.parse("(e+E)");
            RegularExpression exponent = RegularExpression.concatenation(e, integer);
            
            //Integers with the exponent part
            RegularExpression float1 = RegularExpression.concatenation(integer, exponent);
            //Floats with an optional exponent part
            exponent = RegularExpression.union(exponent, epsilon);
            RegularExpression float2 = RegularExpression.concatenation(number, exponent);
            
            return RegularExpression.union(float1,float2);
            
        }
    }
    /**
     * A wrapper for all the processes in the preprocessor.
     */
    public static interface Process extends BiFunction<Lexer.Token,Preprocessor,Lexer.Token>{}
    /**
     * A process that merges a sequence of tokens that are inside quotation marks.
     */
    public static class Quotation implements Process{
        /**
         * @param t the token that triggered this process.
         * @param p the underlying preprocessor that gives the next tokens.
         * @return a token containing the content within the quotation marks
         */
        @Override
        public Lexer.Token apply(Lexer.Token t, Preprocessor p ){
            StringBuilder builder = new StringBuilder();
            Lexer.Token buffer;
            if(p.hasNext()){
                do{
                    buffer = p.nextToken();
                    if(!t.getRight().equals(buffer.getRight())){
                        //Can cause recursion, but it shouldn't be much of a problem.
                        buffer = p.apply(buffer);
                        builder.append(buffer.getLeft());
                    }
                }while(p.hasNext() && !t.getRight().equals(buffer.getRight()));
                return t.getRight().equals(buffer.getRight()) ? new Token(builder.toString(),t.getStates(),Preprocessor.QUOTATION) : ERROR; 
            //We have a lose quotation mark
            }else{
                return ERROR;
            }
        }
    }
    /**
     * A process that allows keywords to be treated as normal characters
     */
    public static class Keyword implements Process{
        /**
         * @param t the token that triggered this process.
         * @param p the underlying preprocessor that gives the next tokens.
         * @return the keyword as a normal character.
         */
        @Override
        public Tokenizer.Token apply(Tokenizer.Token t, Preprocessor p) {
            //Remove the letter that represents the keyword.
            return new Lexer.Token(t.getLeft().substring(1,t.getLeft().length()), t.getStates(), t.getRight());
        }
    }
    /**
     * This process assigns the minus sign to the token that follows.
     */
    public static class Number implements Process{
        /**
         * If the token that has been read before is a minus, we have read two
         * minuses in a row. This means that the second one will be added to
         * the next token, if it exists.<br>
         * Otherwise the current token will be returned.
         * @param t the token that has been read.
         * @param p the underlying preprocessor that gives the next tokens.
         * @return the processed token.
         */
        @Override
        public Token apply(Token t, Preprocessor p) {
            if(p.last_token != null && p.last_token.getRight().equals(t.getRight()) && p.hasNext()){
                Token next_token = p.processToken();
                next_token.setLeft(t.getLeft()+next_token.getLeft());
                return next_token;
            }else{
                return t;
            }
        }
    }
}