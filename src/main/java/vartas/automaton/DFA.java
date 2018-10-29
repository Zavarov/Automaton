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

import com.google.common.collect.ForwardingTable;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import vartas.automaton.alphabet.Alphabet;

/**
 * This class implements a deterministic finite automaton.
 * @author Zavarov
 */
public class DFA extends FiniteAutomaton{
    /**
     * The transition function for the automaton.<br>
     * The row contains the originating state and the column contains the id of
     * the word. The value is the state that is reached after applying the 
     * function.
     */
    protected final TransitionTable transitions;
    /**
     * @param transitions the transition function.
     * @param final_states the set of final states.
     * @param initial_states the set of initial states.
     */
    protected DFA(TransitionTable transitions, StateSet final_states, StateSet initial_states){
        super(final_states, initial_states);
        this.transitions = transitions;
        this.final_states.addAll(final_states);
    }
    /**
     * Executes the input on the automaton.
     * @param input a set of ids of words.
     * @return true if the run ended in a final state.
     */
    @Override
    public boolean run(List<Integer> input) {
        StateSet current_states = new StateSet(initial_states);
        for(int with : input){
            current_states = step(current_states, with);
        }
        return !Collections.disjoint(final_states, current_states);
    }
    /**
     * Executes a single step on the automaton.<br>
     * Since the automaton allows for multiple initial states, it'll always be
     * a set of states.
     * @param from the states the automaton is currently in.
     * @param with the id of the current word.
     * @return the states that are reached after applying the transition relation.
     * @throws MissingTransitionException if the automaton encountered an undefined transition.
     */
    @Override
    public StateSet step(StateSet from, int with) throws MissingTransitionException{
        return new StateSet(from.stream().map(state -> {
            if(transitions.contains(state,with)){
                return transitions.get(state, with);
            }else{
                throw new MissingTransitionException(state, with);
            }
        }).collect(Collectors.toSet()));
    }
    /**
     * Flips all transitions, makes final states into initial states and vice versa.
     * @return an NFA accepting every reversed word that is accepted by this DFA.
     */
    @Override
    public NFA reverse() {
        NFA.Builder reverse = new NFA.Builder();
        
        reverse.initial_states.addAll(final_states);
        reverse.final_states.addAll(initial_states);
        //The total number of states is abandoned once the construction is finished.
        reverse.all_states.addAll(transitions.rowKeySet());
        reverse.all_states.addAll(transitions.values());
        
        transitionSet().forEach(t -> reverse.addTransition(t.getRight(), t.getLeft(), t.getMiddle()));
        return reverse.build();
    }
    /**
     * Applies the Brzozowski algorithm.
     * @return a minimal DFA.
     */
    @Override
    public DFA minimize(){        
        return reverse().determinize().reverse().determinize();
    }
    /**
     * The first entry contains the current state, the second entry the
     * current word and the last entry contains the state that is reached after
     * using the word.
     * @return an unmodifiable set over all transitions in this automaton
     */
    @Override
    public Set<Triple<Integer, Integer, Integer>> transitionSet() {
        return Collections.unmodifiableSet(
            transitions.cellSet().stream()
                .map(c -> ImmutableTriple.of(c.getRowKey(),c.getColumnKey(),c.getValue()))
                .collect(Collectors.toSet())
        );
    }
    /**
     * An implementation of the builder made for deterministic finite automata.
     */
    public static class Builder extends FiniteAutomaton.Builder<DFA>{
        /**
         * Creates a builder over an empty alphabet.
         */
        public Builder(){
            super(new Alphabet());
        }
        /**
         * Creates a builder over an already existing alphabet.
         * @param alphabet the underlying alphabet that is used.
         */
        public Builder(Alphabet alphabet){
            super(alphabet);
        }
        /**
         * The table containing all transitions in this automaton.
         */
        protected final TransitionTable table = new TransitionTable();
        /**
         * Adds a new transition to this automaton.
         * @param from the leaving state.
         * @param to the entering state.
         * @param with the id of the word.
         * @throws IllegalStateException if a transition from the state with that id already exists.
         */
        @Override
        protected void addTransition(int from, int to, int with) throws IllegalStateException{
            if(table.contains(from, with)){
                throw new IllegalStateException(String.format("There already exists a transition %s from %d to %d.",alphabet.get(with),from,to));
            }else{
                table.put(from, with, to);
            }
        }
        /**
         * @param from the starting state.
         * @param to the leading state.
         * @param with the id of the word.
         * @return true, if a transition with the specified letter from the state to the next state exists.
         */
        @Override
        public boolean containsTransition(int from, int to, int with) {
            return table.contains(from, with) && table.get(from, with) == to;
        }
        /**
         * Creates the complete automaton.
         * @return the constructed DFA. 
         * @throws IllegalStateException if the complete automaton couldn't be constructed.
         */
        @Override
        public DFA build() throws IllegalStateException{
            if(!isComplete()){
                int sink = addState();
                //Only look at the used words
                Set<Integer> words = new IntOpenHashSet(table.columnKeySet());
                words.forEach(word -> {
                    //Add all missing states
                    all_states.forEach(state -> {
                        if(!table.contains(state, word)){
                            table.put(state,word,sink);
                        }
                    });
                });
            }
            return new DFA(new TransitionTable(table),new StateSet(final_states), new StateSet(initial_states));
        }
        
        /**
         * Checks if the automaton contains all possible transitions.
         * @return true if the automaton is complete.
         */
        private boolean isComplete(){
            Set<Integer> words = new IntOpenHashSet(table.columnKeySet());
            return all_states.stream()
                    .allMatch(state -> words.stream()
                            .allMatch(word -> table.contains(state, word)));
        }
        /**
         * @param from the starting state.
         * @param with the id of the word.
         * @return true, if a transition with the specified letter from the state exists.
         */
        @Override
        public boolean containsTransition(int from, int with) {
            return table.contains(from, with);
        }
    }
    /**
     * A wrapper class that contains all transitions from one state to another state.
     */
    protected static class TransitionTable extends ForwardingTable<Integer,Integer,Integer>{
        /**
         * The internal table.
         */
        protected final Table<Integer,Integer,Integer> table;
        /**
         * Creates an empty table.
         */
        public TransitionTable(){
            this.table = HashBasedTable.create();
        }
        /**
         * Creates a table over a given input.
         * @param input all entries the table is supposed to contain.
         */
        public TransitionTable(Table<Integer,Integer,Integer> input){
            this.table = HashBasedTable.create(input);
        }
        /**
         * All methods in this class will operate via this method.
         * @return the internal  table.
         */
        @Override
        protected Table<Integer, Integer, Integer> delegate() {
            return table;
        }
    }
}