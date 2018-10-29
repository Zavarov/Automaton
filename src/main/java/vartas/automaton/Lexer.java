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

import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import vartas.automaton.ProductAutomaton.Comparator;

/**
 * An implementation of the lexer. In addition to the tokenizer, the lexer also
 * assigns a unique identifier for each token, that matches a regular expression.
 * @author Zavarov
 */
public class Lexer extends Tokenizer{
    protected final Map<Integer, String> identifier;
    protected Lexer(DFA dfa, Map<Integer,String> identifier) {
        super(dfa);
        this.identifier = identifier;
    }
    /**
     * @return the next token with an identifier, specified by the lexer.
     */
    @Override
    public Token nextToken(){
        Token token = super.nextToken();
        
        int state = Collections.min(token.getStates());
        if(identifier.containsKey(state)){
            token.setRight(identifier.get(state));
        }
        return token;
    }
    /**
     * An implementation of the builder made for lexer.
     */
    public static class Builder{
        /**
         * The underlying tokenizer that is used to construct the lexer.
         */
        protected final Tokenizer.Builder tokenizer;
        /**
         * The list of integers that identify the expressions.
         */
        protected final List<String> identifier;
        /**
         * Creates an empty lexer.
         */
        public Builder(){
            tokenizer = new Tokenizer.Builder();
            identifier = new ObjectArrayList<>();
        }
        /**
         * Adds an expression and an identifier for that expression.
         * @param expression the regular expression.
         * @param identifier the identifier that each token that matches this expression gets.
         */
        public void addExpression(RegularExpression expression, String identifier){
            this.tokenizer.addExpression(expression);
            this.identifier.add(identifier);
        }
        /**
         * Creates the automaton over all expressions and adds an output every
         * time a token was read.
         * @return the tokenizer over all the words.
         * @throws IllegalStateException in case the specified consumer is null.
         */
        public Lexer build() throws IllegalStateException{
            Map<Integer, String> mapping = new Object2ObjectOpenHashMap<>();
            LexerProductBuilder builder = new LexerProductBuilder();
            
            builder.addAutomaton(tokenizer.error);
            tokenizer.expressions.forEach(builder::addAutomaton);
            DFA product = builder.build();
            
            //Ignore the error state
            builder.final_states_mapping.removeAll(0);
            //The keys as sorted, so this'll apply the first match
            builder.final_states_mapping.forEach( (i,j) -> mapping.computeIfAbsent(j,(x) -> identifier.get(i-1)));
            return new Lexer(product, mapping);
        }
    }
    /**
     * An extention of the builder for product automaton, that also keeps track
     * of the states in the product, the final states of the input are assigned to.
     */
    private static class LexerProductBuilder extends ProductAutomaton.Builder{
        /**
         * This map keeps track of which final state belongs to which expression.
         */
        protected final Multimap<Integer,Integer> final_states_mapping;
        /**
         * Initializes the builder with an empty table.
         */
        public LexerProductBuilder(){
            //We want the keys to be sorted
            final_states_mapping = TreeMultimap.create();
            comparator = Comparator.OR;
        }
        /**
         * Computes the product automaton over the entire input. This automaton
         * will make ever state into a final state, if at least one state of
         * the input also is one.<br>
         * It'll also keep track of where the final states of the inputs will be
         * represented in the product.
         * @return the automaton accepting every word that is accepted by at least
         * one of the input values.
         */
        @Override
        public ProductAutomaton build(){
            ProductAutomaton automaton = super.build();
            for(int i = 0 ; i < automatons.size() ; ++i){
                //Add the initial states if they're also final states
                if(!Collections.disjoint(automatons.get(i).initial_states, automatons.get(i).final_states)){
                    final_states_mapping.putAll(i, automaton.initial_states);
                }
            }
            return automaton;
        }
        /**
         * Does the same the former function did and additionally keeps track
         * of where all the states are mapped to.
         * @param states the current configuration of states.
         * @param builder the builder that supplies the states.
         * @return the id of the next state.
         */
        @Override
        protected int computeState(List<StateSet> states, DFA.Builder builder){
            int next = super.computeState(states, builder);
            for(int i = 0 ; i < states.size() ; ++i){
                //Only store final states
                if(!Collections.disjoint(automatons.get(i).final_states,states.get(i))){
                    final_states_mapping.put(i, next);
                }
            }
            return next;
        }
    }
}
