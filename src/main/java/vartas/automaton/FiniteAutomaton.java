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

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.tuple.Triple;
import vartas.automaton.alphabet.Alphabet;

/**
 * This class provides a frame for all types of finite automaton.
 * @author Zavarov
 */
public abstract class FiniteAutomaton {
    /**
     * The set of final states.
     */
    protected final StateSet final_states;
    /**
     * The set of initial states.
     */
    protected final StateSet initial_states;
    /**
     * Creates a finite automaton with a certain set of initial and final states.
     * The transitions have to be handled by the specific implementation.
     * @param final_states the set of final states.
     * @param initial_states the set of initial states. 
     */
    protected FiniteAutomaton(StateSet final_states, StateSet initial_states){
        this.final_states = final_states;
        this.initial_states = initial_states;
    }
    /**
     * Executes the input on the automaton.
     * @param input a set of ids of words.
     * @return true if the run ended in a final state.
     */
    public abstract boolean run(List<Integer> input);
    /**
     * Executes a single step on the automaton.<br>
     * Since the automaton allows for multiple initial states, it'll always be
     * a set of states.
     * @param from the states the automaton is currently in.
     * @param with the id of the current word.
     * @return the states that are reached after applying the transition relation.
     */
    public abstract StateSet step(StateSet from, int with);
    /**
     * @return an NFA where each transition is reversed.
     */
    public NFA reverse(){
        NFA.Builder reverse = new NFA.Builder();
        
        reverse.initial_states.addAll(final_states);
        reverse.final_states.addAll(initial_states);
        
        transitionSet().forEach(t -> reverse.addTransition(t.getRight(), t.getLeft(), t.getMiddle()));
        return reverse.build();
    }
    /**
     * @return the minimal DFA accepting the same language.
     */
    public abstract DFA minimize();
    /**
     * The first entry contains the current state, the second entry the
     * current word and the last entry contains the state that is reached after
     * using the word.
     * @return an unmodifiable set over all transitions in this automaton
     */
    public abstract Set<Triple<Integer,Integer,Integer>> transitionSet();
    /**
     * To create the complement, all final states are turned into normal states and vice versa.
     * @return the automaton accepting every language that is rejected by this automaton. 
     */
    public DFA complement() {
        DFA dfa = minimize();
        Set<Integer> all_states = new IntOpenHashSet();
        all_states.addAll(dfa.transitions.rowKeySet());
        all_states.addAll(dfa.transitions.values());
        
        DFA.Builder builder = new DFA.Builder();
        
        builder.final_states.addAll(all_states);
        builder.final_states.removeAll(dfa.final_states);
        
        builder.initial_states.addAll(dfa.initial_states);
        
        builder.table.putAll(dfa.transitions);
        
        return builder.build();
    }
    /**
     * @return true if the automaton rejects every word. 
     */
    public boolean isEmpty(){
        return minimize().final_states.isEmpty();
    }
    /**
     * A helper class that simplifies the construction of automaton.
     * @param <T> the type of automaton that is constructed.
     */
    public static abstract class Builder<T>{
        /**
         * The last id that is assigned to a state before the automaton is full.
         */
        public static final int LAST_ID = -2;
        /**
         * The id that is assigned to the next state that is created.
         */
        protected int next_id = 0;
        /**
         * All states that are defined in this automaton.
         */
        protected final StateSet all_states = new StateSet();
        /**
         * The set over all initial states.
         */
        protected final StateSet initial_states = new StateSet();
        /**
         * The set over all final states.
         */
        protected final StateSet final_states = new StateSet();
        /**
         * The alphabet containing all words and ids over which transitions are defined.
         */
        protected final Alphabet alphabet;
        /**
         * Creates a builder over an already existing alphabet.
         * @param alphabet the underlying alphabet that is used.
         */
        public Builder(Alphabet alphabet){
            this.alphabet = alphabet;
        }
        
        /**
         * Adds a new transition to this automaton.
         * @param from the leaving state.
         * @param to the entering state.
         * @param with the id of the word.
         */
        protected abstract void addTransition(int from, int to, int with);
        /**
         * @param from the starting state.
         * @param to the leading state.
         * @param with the id of the word.
         * @return true, if a transition with the specified letter from the state to the next state exists.
         */
        public abstract boolean containsTransition(int from, int to, int with);
        /**
         * @param from the starting state.
         * @param with the id of the word.
         * @return true, if a transition with the specified letter from the state exists.
         */
        public abstract boolean containsTransition(int from, int with);
        /**
         * @return the created automaton. 
         */
        public abstract T build();
        /**
         * @param from the starting state.
         * @param to the leading state.
         * @param with the id of the word.
         * @return true, if a transition with the specified letter from the state to the next state exists.
         */
        public boolean containsTransition(int from, int to, String with) {
            alphabet.add(with);
            return containsTransition(from, to, alphabet.get(with));
        }
        /**
         * @param from the starting state.
         * @param with the id of the word.
         * @return true, if a transition with the specified letter from the state exists.
         */
        public boolean containsTransition(int from, String with) {
            alphabet.add(with);
            return containsTransition(from, alphabet.get(with));
        }
        /**
         * Adds a normal state to this automaton.
         * @return the id of the state.
         * @throws IllegalStateException if the automaton has reached it maximum size. 
         */
        public int addState() throws IllegalStateException{
            //We went full circle
            if(next_id == LAST_ID+1){
                throw new IllegalStateException(String.format("The automaton reached its maximum size of %d states.",Integer.toUnsignedLong(LAST_ID)));
            }else{
                all_states.add(next_id);
                return next_id++;
            }
        }
        /**
         * Adds a final state to this automaton.
         * @return the id of the final state.
         * @throws IllegalStateException if the automaton has reached it maximum size. 
         */
        public int addFinalState() throws IllegalStateException{
            int state = addState();
            final_states.add(state);
            return state;
        }
        /**
         * Adds an initial state to this automaton.
         * @return the id of the initial state.
         * @throws IllegalStateException if the automaton has reached it maximum size. 
         */
        public int addInitialState() throws IllegalStateException{
            int state = addState();
            initial_states.add(state);
            return state;
        }
        /**
         * Adds a bunch of transitions to the automaton.
         * @param from the leaving state.
         * @param to the entering state.
         * @param with the first token.
         * @param rest the remaining token.
         * @throws IllegalStateException if the states are not in the automaton.
         */
        public void addTransition(int from, int to, String with, String... rest) throws IllegalStateException{
            alphabet.add(with);
            alphabet.addAll(Arrays.asList(rest));
            
            addTransition(from, to, alphabet.get(with));
            for(String i : rest){addTransition(from, to, alphabet.get(i));}
        }
        /**
         * Adds a bunch of transitions to the automaton.
         * @param from the leaving state.
         * @param to the entering state.
         * @param with the id of the first token.
         * @param rest the ids of the remaining token.
         * @throws IllegalStateException if the states are not in the automaton.
         */
        protected void addTransition(int from, int to, int with, int... rest) throws IllegalStateException{
            if(!all_states.contains(from)){
                throw new IllegalStateException(String.format("%d is not a state.",from));
            }else if(!all_states.contains(to)){
                throw new IllegalStateException(String.format("%d is not a state.",to));
            }else{
                addTransition(from, to, with);
                for(int i : rest){addTransition(from,to,i);}
            }
        }
        /**
         * Makes the state into an initial state.
         * @param state the state.
         * @return true if the state wasn't an initial state before.
         * @throws IllegalStateException if the state is not in the automaton.
         */
        public boolean makeInitialState(int state) throws IllegalStateException{
            if(!all_states.contains(state)){
                throw new IllegalStateException(String.format("%d is not a state.",state));
            }else{
                return initial_states.add(state);
            }
        }
        /**
         * Makes the stae into a final state.
         * @param state the state.
         * @return true if the state wasn't a final state before.
         * @throws IllegalStateException if the state is not in the automaton.
         */
        public boolean makeFinalState(int state) throws IllegalStateException{
            if(!all_states.contains(state)){
                throw new IllegalStateException(String.format("%d is not a state.",state));
            }else{
                return final_states.add(state);
            }
        }
        
        /**
         * Adds an automaton to this one. The resulting automaton will then accept
         * every word, that is accepted by at least one of the automatons.
         * @param automaton an automaton.
         * @return a mapping from the old states of the input to the new states in this automaton.
         * @throws IllegalStateException if the resulting automaton would be too large. 
         */
        protected BiMap<Integer,Integer> addAutomaton(FiniteAutomaton automaton) throws IllegalStateException{
            BiMap<Integer,Integer> map = HashBiMap.create();
            
            //Add all states and transitions
            automaton.transitionSet().forEach(t -> {
                int from = map.computeIfAbsent(t.getLeft(), (s) -> addState());
                int to = map.computeIfAbsent(t.getRight(), (s) -> addState());
                addTransition(from,to,t.getMiddle());
            });
            //Mark all final states
            automaton.final_states.stream().filter(map::containsKey).forEach(o -> makeFinalState(map.get(o)));
            //Mark all initial states
            automaton.initial_states.stream().filter(map::containsKey).forEach(o -> makeInitialState(map.get(o)));
            return map;
        }
    }
    
    /**
     * A wrapper class that contains the states of the automaton.
     */
    protected static class StateSet extends HashSet<Integer>{
        private static final long serialVersionUID = 1L;
        /**
         * Creates an empty set.
         */
        public StateSet(){
            super();
        }
        /**
         * Creates a set over a given input.
         * @param input the values the set is supposed to contain.
         */
        public StateSet(Collection<Integer> input){
            super(input);
        }
        /**
         * Creeates a set over an array
         * @param input the values the set is supposed to contain.
         */
        public StateSet(Integer... input){
            this(Sets.newHashSet(input));
        }
    }
}
