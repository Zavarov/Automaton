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
import it.unimi.dsi.fastutil.booleans.BooleanArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Deque;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This class implements a product automaton over multiple that 
 * @author Zavarov
 */
public class ProductAutomaton extends DFA{
    /**
     * @param dfa the underlying DFA.
     */
    protected ProductAutomaton(DFA dfa) {
        super(dfa.transitions, dfa.final_states, dfa.initial_states);
    }
    /**
     * This enumeration contains all possible types of product automaton.
     * Each entry specifices a condition under which a state in the product
     * automaton becomes a final state.
     */
    public static enum Comparator{
        /**
         * A state is a final states iff all states are final states.
         */
        AND(b -> !b.contains(false)),
        /**
         * A state is a final state iff at least one state is a final state.
         */
        OR(b -> b.contains(true)),
        /**
         * A state is a final state iff all state are final states or no
         * state is a final state.
         */
        XAND(b -> !b.contains(false) || !b.contains(true)),
        /**
         * A state is a final state iff at least one state is a final state and
         * at least one state is not a final state.
         */
        XOR(b -> b.contains(false) && b.contains(true)),
        /**
         * A state is a final state iff no state is a final state.
         */
        NOT(b -> !b.contains(true));
        private final Function<List<Boolean>,Boolean> function;
        private Comparator(Function<List<Boolean>,Boolean> function){
            this.function = function;
        }
        public boolean apply(List<Boolean> types){
            return function.apply(types);
        }
    }
    /**
     * A builder for product automatons.
     */
    public static class Builder{
        /**
         * A list of all automatons whose product we want to compute.
         */
        protected final List<DFA> automatons;
        /**
         * The comparator operation. By default, it is the AND comparator.
         */
        protected Comparator comparator = Comparator.AND;
        /**
         * Creates an empty builder.
         */
        public Builder(){
            automatons = new LinkedList<>();
        }
        /**
         * Adds an automaton to the internal list.
         * @param automaton the automaton who will be part of the product automaton.
         */
        public void addAutomaton(DFA automaton){
            this.automatons.add(automaton);
        }
        /**
         * Sets a new comparator.
         * @param comparator the new comparator.
         * @throws IllegalArgumentException if the comparator is null.
         */
        public void setComparator(Comparator comparator) throws IllegalArgumentException{
            if(comparator == null){throw new IllegalArgumentException("Comparator can't be null.");}
            this.comparator = comparator;
        }
        /**
         * Generates the product of all automaton and returns it.
         * @return the product automaton over the entire input.
         */
        public ProductAutomaton build(){
            DFA.Builder builder = new DFA.Builder();
            //Computes the tokens that are used by all of the automatons.
            Set<Integer> alphabet = getAlphabet();
            //Each state in the DFA is the union of all states in the input DFAs
            BiMap<Integer,List<StateSet>> state_map = HashBiMap.create();
            //The work stack
            Deque<Integer> stack = new ArrayDeque<>();
            //The current and the next step in the product automaton
            List<StateSet> current_states, next_states;
            
            current_states = automatons.stream().map(dfa -> dfa.initial_states).collect(Collectors.toList());
            //Create the initial step which might also be a final state
            int current_state = computeState(current_states, builder), next_state;
            builder.makeInitialState(current_state);
            stack.push(current_state);
            state_map.put(current_state, current_states);
            
            while(!stack.isEmpty()){
                current_state = stack.pop();
                for(int token : alphabet){
                    current_states = state_map.get(current_state);
                    next_states = computeNextStep(current_states, token);
                    //Generate a state in the builder and push it to the stack, if unvisited.
                    next_state = state_map.inverse().computeIfAbsent(next_states, (n) -> {
                        int s = computeState(n, builder);
                        stack.push(s);
                        state_map.put(s, n);
                        return s;
                    });
                    builder.addTransition(current_state, next_state, token);
                }
            }
            return new ProductAutomaton(builder.build());
        }
        /**
         * Retrieves the effective alphabet of each automaton and merges them into a single set.
         * @return a set of all tokens that are used by the automatons.
         */
        private Set<Integer> getAlphabet(){
            return automatons.stream()
                .map(dfa -> dfa.transitions.columnKeySet())
                .flatMap(o -> o.stream())
                .collect(Collectors.toSet());
        }
        /**
         * Checks whether or not the current states are final states.
         * @param states a set of states.
         * @param dfa the corresponding DFA
         * @return true if at least one of the states is a final state 
         */
        private boolean mapToBoolean(StateSet states, DFA dfa){
            return states.stream()
                    .map(state -> dfa.final_states.contains(state))
                    .reduce( (i,j) -> i || j).orElse(false);
        }
        /**
         * @param states a list over all the states the automatons are currently in.
         * @return a list of boolean, each boolean indicates that the respective automaton
         * is in a final state.
         */
        private List<Boolean> mapStepToBoolean(List<StateSet> states){
            List<Boolean> list = new BooleanArrayList(states.size());
            for(int i = 0 ; i < states.size() ; ++i){
                list.add(mapToBoolean(states.get(i),automatons.get(i)));
            }
            return list;
        }
        /**
         * Executes a single step on each automaton.
         * @param current_step the current states of all automaton.
         * @param token a token.
         * @return the state set of the automatons after applying the token.
         */
        private List<StateSet> computeNextStep(List<StateSet> current_step, int token){
            List<StateSet> next_step = new ObjectArrayList<>(current_step.size());
            for(int i = 0 ; i < current_step.size() ; ++i){
                //Not every automaton has to contain the union of the alphabet
                if(automatons.get(i).transitions.containsColumn(token)){
                    next_step.add(automatons.get(i).step(current_step.get(i),token));
                }else{
                    //The empty set represents a sink state
                    next_step.add(new StateSet());
                }
            }
            return next_step;
        }
        /**
         * Computes the next state based on the current configuration. This means
         * that the next state is either a final state or a normal state.
         * @param states the current configuration of states.
         * @param builder the builder that supplies the states.
         * @return the id of the next state.
         */
        protected int computeState(List<StateSet> states, DFA.Builder builder){
            List<Boolean> booleans = mapStepToBoolean(states);
            if(comparator.apply(booleans)){
                return builder.addFinalState();
            }else{
                return builder.addState();
            }
        }
    }
}