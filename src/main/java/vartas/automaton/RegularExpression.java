package vartas.automaton;

import vartas.automaton.alphabet.Alphabet;

/*
 * Copyright (C) 2017 Zavarov
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


/**
 * Creates a regular expression.
 * @author Zavarov
 */
public class RegularExpression extends NFA{
    /**
     * Wraps a regular expression around an automaton.
     * @param nfa the underlying automaton.
     */
    protected RegularExpression(NFA nfa){
        super(nfa.transitions,nfa.final_states,nfa.initial_states);
    }
    /**
     * @param token a letter.
     * @return an automaton accepting only the token.
     * @throws IllegalStateException if the automaton would be too large. 
     */
    public static RegularExpression singleton(int token) throws IllegalStateException{
        NFA.Builder builder = new NFA.Builder();
        int start = builder.addInitialState();
        int end = builder.addFinalState();
        builder.addTransition(start, end, token);
        return new RegularExpression(builder.build());
    }
    /**
     * @param first the first expression.
     * @param rest all remaining expressions.
     * @return an automaton accepting the union of all expressions.
     * @throws IllegalStateException if the automaton would be too large.
     */
    public static RegularExpression union(RegularExpression first, RegularExpression... rest) throws IllegalStateException{
        NFA.Builder builder = new NFA.Builder();
        builder.addAutomaton(first);
        
        int start = builder.addState();
        int end = builder.addState();

        //Add the remaining automatons
        for(RegularExpression expression : rest){
            builder.addAutomaton(expression);
        }
        //Add transitions to the initial and final state
        builder.initial_states.forEach(initial_state -> {
            builder.addEpsilonTransition(start, initial_state);
        });
        builder.final_states.forEach(final_state -> {
            builder.addEpsilonTransition(final_state, end);
        });
        //Remap initial and final states;
        builder.initial_states.clear();
        builder.makeInitialState(start);
        builder.final_states.clear();
        builder.makeFinalState(end);
        return new RegularExpression(builder.build());
    }
    /**
     * @param first the first expression
     * @param rest the remaining expressions
     * @return an automaton accepting the concatenation of the automatons.
     * @throws IllegalStateException if the automaton would be too large.
     */
    public static RegularExpression concatenation(RegularExpression first, RegularExpression... rest) throws IllegalStateException{
        NFA.Builder builder = new NFA.Builder();
        builder.addAutomaton(first);
        StateSet states, start = new StateSet(builder.initial_states);
        
        for(RegularExpression expression : rest){
            builder.initial_states.clear();
            states = new StateSet(builder.final_states);
            builder.final_states.clear();
            
            builder.addAutomaton(expression);
            states.forEach(from -> {
                builder.initial_states.forEach(to -> {
                    builder.addEpsilonTransition(from, to);
                });
            });
        }
        
        //Update initial states
        builder.initial_states.clear();
        builder.initial_states.addAll(start);
        return new RegularExpression(builder.build());
    }
    /**
     * @param expression the expression.
     * @return the smallest superset of the expression that is closed under concatenation.
     * @throws IllegalStateException if the automaton would be too large.
     */
    public static RegularExpression kleeneStar(RegularExpression expression) throws IllegalStateException{
        NFA.Builder builder = new NFA.Builder();
        builder.addAutomaton(expression);
        int start = builder.addState();
        int end = builder.addState();

        //Map each old final state to each old initial state
        builder.final_states.forEach(final_state -> {
            builder.initial_states.stream().forEach(initial_state -> {
                builder.addEpsilonTransition(final_state, initial_state);
            });
        });
        //Map the old final states to the new one
        builder.final_states.forEach(final_state -> {
            builder.addEpsilonTransition(final_state, end);
        });
        //Map the new initial state to the old ones
        builder.initial_states.forEach(initial_state -> {
            builder.addEpsilonTransition(start, initial_state);
        });
        //Map the new initial state to the new final state
        builder.addEpsilonTransition(start, end);
        
        
        //Update initial and final states
        builder.initial_states.clear();
        builder.makeInitialState(start);
        builder.final_states.clear();
        builder.makeFinalState(end);
        return new RegularExpression(builder.build());
    }

    /**
     * A parser for regular expressions.<br><br>
     * The grammar used is:<br>
     * 
     *      regularExpression -&gt; unitedExpression union<br>
     *                  union -&gt; + regularExpression | epsilon<br>
     *       unitedExpression -&gt; concatenatedExpression concatenation<br>
     *          concatenation -&gt: unitedExpression | epsilon<br>
     * concatenatedExpression -&gt; kleeneExpression kleene<br>
     *                 kleene -&gt; * kleene | epsilon<br>
     *       kleeneExpression -&gt; ( regularExpression ) | \\ | \+ | \* | \( | \) | character<br>
     * Unlike the normal automaton, each character will be represented by its integer representation and
     * not by an id it receives from an underlying alphabet.
     */
    public static class Parser{
        /**
         * The current position in the String.
         */
        protected int index;
        /**
         * The input String.
         */
        protected String expression;
        /**
         * @return true if there is a letter after the current one. 
         */
        public boolean hasNext(){
            return index < expression.length();
        }
        /**
         * @return the next letter. 
         */
        public char nextToken(){
            if(hasNext()){
                return expression.charAt(index++);
            }else{
                throw new ParserException();
            }
        }
        /**
         * @return the next letter without consuming it. 
         * @throws ParserException if we're already at the last token.
         */
        public char peekToken() throws ParserException{
            if(hasNext()){
                return expression.charAt(index);
            }else{
                throw new ParserException();
            }
        }
        /**
         * @param expression the regular expression
         * @return the automaton acception everthing that matches the expression.
         * @throws ParserException if the expression is not valid.
         */
        public synchronized RegularExpression parse(String expression) throws ParserException{
            this.expression = expression;
            this.index = 0;
            RegularExpression epsilon = RegularExpression.singleton(Alphabet.EPSILON);
            return expression.length() == 0 ? epsilon : regularExpression(epsilon);
        }
        /**
         * @param current the expression that has been parsed so far.
         * @return the automaton for a complete (sub-)expression.
         * @throws ParserException if the expression is invalid.
         */
        private RegularExpression regularExpression(RegularExpression current) throws ParserException{
            char token = peekToken();
            switch(token){
                case ')':
                case '*':
                case '+':
                    throw new ParserException(token, index);
                default:{
                    current = unitedExpression(current);
                    current = union(current);
                    return current;
                }
            }
        }
        /**
         * @param current the expression that has been parsed so far.
         * @return the automaton for a union.
         * @throws ParserException if the expression is invalid.
         */
        private RegularExpression union(RegularExpression current) throws ParserException{
            if(hasNext()){
                char token = peekToken();
                switch(token){
                    case '+':{
                        nextToken();
                        RegularExpression next = regularExpression(current);
                        return RegularExpression.union(current,next);
                    }
                    default:{
                        return current;
                    }
                }
            }else{
                return current;
            }
        }
        /**
         * @param current the expression that has been parsed so far.
         * @return the automaton for a united expression.
         * @throws ParserException if the expression is invalid.
         */
        private RegularExpression unitedExpression(RegularExpression current){
            current = concatenatedExpression(current);
            current = concatenation(current);
            return current;
        }
        /**
         * @param current the expression that has been parsed so far.
         * @return the automaton for a concatenation.
         * @throws ParserException if the expression is invalid.
         */
        private RegularExpression concatenation(RegularExpression current){
            if(hasNext()){
                char token = peekToken();
                switch(token){
                    case ')':
                    case '+':
                        return current;
                    default:{
                        current = RegularExpression.concatenation(current, unitedExpression(current));
                        return current;
                    }
                }
            }else{
                return current;
            }
        }
        /**
         * @param current the expression that has been parsed so far.
         * @return the automaton for a concatenated expression.
         * @throws ParserException if the expression is invalid.
         */
        private RegularExpression concatenatedExpression(RegularExpression current){
            current = kleeneExpression(current);
            current = kleene(current);
            return current;
        }
        /**
         * @param current the expression that has been parsed so far.
         * @return the automaton where the kleene operation could've been applied.
         * @throws ParserException if the expression is invalid.
         */
        private RegularExpression kleene(RegularExpression current){
            if( hasNext() && peekToken() == '*' ){
                nextToken();
                return kleene(RegularExpression.kleeneStar(current));
            }else{
                return current;
            }
        }
        /**
         * @param current the expression that has been parsed so far.
         * @return the automaton that is either a single character or a group.
         * @throws ParserException if the expression is invalid.
         */
        private RegularExpression kleeneExpression(RegularExpression current){
            char token = nextToken();
            switch(token){
                case '(':{
                    current = regularExpression(current);
                    if(hasNext()){
                        //Read the ')'
                        nextToken();
                        return current;
                    }else{
                        throw new ParserException(token,index);
                    }
                }
                case '\\':{
                    if(hasNext()){
                        token = nextToken();
                        switch(token){
                            case '\\':
                            case '+':
                            case '(':
                            case ')':
                            case '*':{
                                return RegularExpression.singleton(token);
                            }
                            default:{
                                throw new ParserException(token,index);
                            }
                        }
                    }else{
                        throw new ParserException(token,index);
                    }
                }
                default:{
                    return RegularExpression.singleton(token);
                }
            }
        }
    }
    /**
     * This exception is thrown when the parsing process has failed.
     * @author Zavarov
     */
    public static class ParserException extends RuntimeException{
        private static final long serialVersionUID = 0L;
        /**
         * @param letter the invalid letter.
         * @param index the position in the input String.
         */
        public ParserException(char letter, int index){
            super(String.format("Invalid letter %c at index %d", letter, index));
        }
        /**
         * This case applies when no letter is remaining, but the parser hasn't finished.
         * In other words, the input was correct so far, but too short.
         */
        public ParserException(){
            super("Invalid end.");
        }
    }
}