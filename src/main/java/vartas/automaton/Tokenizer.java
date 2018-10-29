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

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang3.tuple.MutablePair;
import vartas.automaton.alphabet.Alphabet;

/**
 *
 * @author Zavarov
 */
public class Tokenizer extends DFA{
    /**
     * The iterator over the given data.
     */
    protected Iterator<Integer> data;
    /**
     * The last letter that has been read.
     */
    protected Integer next_letter;
    /**
     * The sink states.
     */
    protected final StateSet sink_states;
    /**
     * Initializes the DFA.
     * @param dfa the underlying DFA.
     */
    protected Tokenizer(DFA dfa) {
        super(dfa.transitions, dfa.final_states, dfa.initial_states);
        //Compute all sink states that loop back to itself
        sink_states = sinkStates();
    }
    /**
     * Computes the set of all sink states. A state is a sink state, when it
     * can't reach a final state.
     * @return the set of all sink states. 
     */
    private StateSet sinkStates(){
        StateSet sink = new StateSet(transitions.rowKeySet()), productive;
        sink.removeAll(final_states);
        do{
            productive = new StateSet();
            for(int state : sink){
                //We can reach a state that is not a sink state
                if(!sink.containsAll(transitions.row(state).values())){
                    productive.add(state);
                }
            }
            sink.removeAll(productive);
        }while(!productive.isEmpty());
        return sink;
    }
    /**
     * This function returns the next valid letter and skips all invalid ones.
     * A letter is considered to be invalid, if it isn't a valid transition in
     * the automaton.
     * @return the next valid letter.
     */
    protected Integer nextLetter(){
        return data.hasNext() ? data.next() : null;
    }
    /**
     * Sets the current input for the tokenizer.
     * @param data the data the tokenizer runs on.
     */
    public void setInput(String data){
        if(data == null){throw new IllegalArgumentException("The data can't be null.");}
        this.data = data.chars().iterator();
        this.next_letter = nextLetter();
    }
    /**
     * @return true if a part of the data hasn't been processed.
     */
    public boolean hasNext(){
        if(data==null){throw new IllegalStateException("The data is null.");}
        return next_letter != null;
    }
    /**
     * This method returns the next longest match.
     * @return the next token. 
     */
    public Token nextToken(){
        if(!hasNext()){throw new IllegalArgumentException("There is no next token.");}
        List<Integer> token = new IntArrayList();
        StateSet current_states = initial_states, next_states;
        do{
            next_states = transitions.containsColumn(next_letter) ? step(current_states, next_letter) : step(current_states, Alphabet.ERROR);
            if(!sink_states.containsAll(next_states)){
                token.add(next_letter);
                next_letter = nextLetter();
                current_states = next_states;
            //We have a letter that moves from a initial state to a sink state
            }else if(current_states.equals(initial_states)){
                token.add(next_letter);
                next_letter = nextLetter();
            }
        }while(!sink_states.containsAll(next_states) && hasNext());
        StringBuilder word = new StringBuilder();
        token.stream().map(i -> (char)i.intValue()).forEach(word::append);
        return new Token(word.toString(), current_states, null);
    }
    /**
     * An implementation of the builder made for tokenizer.
     */
    public static class Builder{
        /**
         * All expressions that are tokenized.
         */
        protected final List<DFA> expressions = new ObjectArrayList<>();
        /**
         * The error state that is visited when an invalid letter is read.
         */
        protected final DFA error;
        /**
         * Initializes an empty builder and the automaton for the error.
         */
        public Builder(){
            error = RegularExpression.singleton(Alphabet.ERROR).minimize();
        }
        /**
         * Adds a new expression to the tokenizer.
         * @param expression the expression
         */
        public void addExpression(RegularExpression expression){
            expressions.add(expression.minimize());
        }
        /**
         * Creates the automaton over all expressions and adds an output every
         * time a token was read.
         * @return the tokenizer over all the words.
         * @throws IllegalStateException in case the specified consumer is null.
         */
        public Tokenizer build() throws IllegalStateException{
            NFA.Builder builder = new NFA.Builder();
            builder.addAutomaton(error);
            expressions.forEach(builder::addAutomaton);
            DFA union = builder.build().minimize();
            return new Tokenizer(union);
        }
    }
    /**
     * A wrapper class for the token.
     */
    public static class Token extends MutablePair<String,String>{
        private static final long serialVersionUID = 1L;
        /**
         * The states that correspond to this token.
         */
        protected final StateSet states;
        /**
         * @param token the word that has been read.
         * @param current_states the state the automaton stopped in.
         * @param identifier an optional identifier for the token.
         */
        public Token(String token, StateSet current_states, String identifier){
            super(token, identifier);
            this.states = current_states;
        }
        /**
         * @param token the word that has been read.
         * @param identifier an optional identifier for the token.
         */
        public Token(String token, String identifier){
            this(token,null,identifier);
        }
        /**
         * @return the states that the token ended in.
         */
        public StateSet getStates(){
            return states;
        }
    }
}