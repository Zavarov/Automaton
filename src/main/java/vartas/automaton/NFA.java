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
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Triple;
import vartas.automaton.alphabet.Alphabet;


/**
 * This class implements a nondeterministic finite automaton.
 * @author Zavarov
 */
public class NFA extends FiniteAutomaton{
    /**
     * The transition relation for the automaton.<br>
     * The row contains the originating state and the column contains the id of
     * the word. The values are the states that are reached after applying the 
     * relation.
     */
    protected final TransitionTable transitions;
    /**
     * @param transitions the transition relation.
     * @param final_states the set of final states.
     * @param initial_states the set of initial states.
     */
    protected NFA(TransitionTable transitions, StateSet final_states, StateSet initial_states){
        super(final_states, initial_states);
        this.transitions = transitions;
    }
    /**
     * Executes the input on the automaton.
     * @param input a set of ids of words.
     * @return true if the run ended in a final state.
     */
    @Override
    public boolean run(List<Integer> input) {
        StateSet current_state = closure(initial_states);
        Iterator<Integer> iterator = input.iterator();
        while(iterator.hasNext() && !current_state.isEmpty()){
            current_state = closure(step(current_state,iterator.next()));
        }
        return !Collections.disjoint(current_state, final_states);
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
    public StateSet step(StateSet from, int with) {
        if(with == Alphabet.EPSILON){
            return closure(from);
        }else{
            StateSet next_states = new StateSet();
            from.forEach( state  -> {
                next_states.addAll(transitions.row(state)
                        .entrySet()
                        .stream()
                        .filter(entry -> entry.getKey() == with)
                        .flatMap(entry -> entry.getValue().stream())
                        .collect(Collectors.toSet()));
            });
            return closure(next_states);
        }
    }
    /**
     * Applies the Brzozowski algorithm.
     * @return a minimal DFA.
     */
    @Override
    public DFA minimize() {
        return determinize().minimize();
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
                    .map(c -> c.getValue().stream()
                            .map(v -> Triple.of(c.getRowKey(), c.getColumnKey(), v))
                            .collect(Collectors.toSet())
                    ).flatMap(set -> set.stream()).collect(Collectors.toSet())
        );
    }
    /**
     * Applies a powerset to remove the nondeterminism.
     * @return this automaton as an DFA.
     * @throws IllegalStateException if the constructed DFA is too large.
     */
    public DFA determinize() throws IllegalStateException{
        DFA.Builder dfa = new DFA.Builder();
        Map<StateSet,Integer> map = new Object2IntOpenHashMap<>();
        
        StateSet current_states = closure(initial_states);
        StateSet next_states;
        Queue<StateSet> unvisited = new LinkedList<>();
        unvisited.add(current_states);
        map.put(current_states, dfa.addInitialState());
        
        while(!unvisited.isEmpty()){
            current_states = unvisited.remove();
            //The set is a final state if at least one of the states is also one
            if(!Collections.disjoint(final_states, current_states)){
                dfa.makeFinalState(map.get(current_states));
            }
            //Adds the id for all words except epsilon
            for(int id : transitions.columnKeySet()){
                if(id == Alphabet.EPSILON){continue;}
                //Step already contains the closure
                next_states = step(current_states,id);
                if(!next_states.isEmpty()){
                    //The next state hasn't been visited before
                    if(!map.containsKey(next_states)){
                        map.put(next_states, dfa.addState());
                        unvisited.add(next_states);
                    }
                    dfa.addTransition(map.get(current_states) ,map.get(next_states), id);
                }
            }
        }
        return dfa.build();
    }
    
    
    /**
     * Computes the set of all states that are reachable from the original states
     * via epsilon transitions.
     * @param from an initial set of states.
     * @return a set of all reachable states. 
     */
    protected StateSet closure(StateSet from){
        StateSet closure = new StateSet(from);
        //Get all transitions via epsilon
        Map<Integer,StateSet> map = transitions.column(Alphabet.EPSILON);
        Set<Integer> to;
        do{
            to = closure
                    .stream()
                    .filter(o -> map.containsKey(o))
                    .filter(o -> !closure.containsAll(map.get(o)))
                    .flatMap(o -> map.get(o).stream())
                    .collect(Collectors.toSet());
            closure.addAll(to);
        }while(!to.isEmpty());
        return closure;
    }
    /**
     * An implementation of the builder made for nondeterministic finite automata.
     */
    public static class Builder extends FiniteAutomaton.Builder<NFA>{
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
         * Adds a new epsilon transition to this automaton.
         * @param from the leaving state.
         * @param to the entering state.
         */
        public void addEpsilonTransition(int from, int to){
            addTransition(from, to, Alphabet.EPSILON);
        }
        /**
         * Adds a new transition to this automaton.
         * @param from the leaving state.
         * @param to the entering state.
         * @param with the id of the word.
         */
        @Override
        protected void addTransition(int from, int to, int with) {
            if(!table.contains(from, with)){
                table.put(from, with, new StateSet());
            }
            table.get(from, with).add(to);
        }
        /**
         * @param from the starting state.
         * @param to the leading state.
         * @param with the id of the word.
         * @return true, if a transition with the specified letter from the state to the next state exists.
         */
        @Override
        public boolean containsTransition(int from, int to, int with) {
            if(!table.contains(from, with)){
                return false;
            }else{
                return table.get(from, with).contains(to);
            }
        }
        /**
         * @return the constructed NFA. 
         */
        @Override
        public NFA build() {
            return new NFA(new TransitionTable(table),new StateSet(final_states), new StateSet(initial_states));
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
    protected static class TransitionTable extends ForwardingTable<Integer,Integer,StateSet>{
        /**
         * The internal table.
         */
        protected final Table<Integer,Integer,StateSet> table;
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
        public TransitionTable(Table<Integer,Integer,StateSet> input){
            this.table = HashBasedTable.create(input);
        }
        /**
         * All methods in this class will operate via this method.
         * @return the internal  table.
         */
        @Override
        protected Table<Integer, Integer, StateSet> delegate() {
            return table;
        }
    }
}