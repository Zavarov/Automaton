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
package vartas.automaton;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Map;
import java.util.function.Consumer;
import vartas.automaton.alphabet.Alphabet;

/**
 * Creates a Moore automaton.
 * @author Zavarov
 */
public class MooreAutomaton extends DFA{
    /**
     * Stores the output for all states
     */
    protected final OutputMap output;
    /**
     * @param transitions the transition relation.
     * @param final_states the set of final states.
     * @param initial_states the set of initial states.
     * @param output the table containing the actions for when a transition is taken.
     */
    protected MooreAutomaton(TransitionTable transitions, StateSet final_states, StateSet initial_states, OutputMap output){
        super(transitions, final_states, initial_states);
        this.output = output;
    }
    /**
     * Acts just like a normal finite automaton. However, if a state is
     * visitied, which has an action assignedf to it, the action will be executed.
     * @param from the starting state.
     * @param with the letter.
     * @return the leading state.
     */
    @Override
    public StateSet step(StateSet from, int with){
        StateSet to = super.step(from, with);
        to.stream().filter(state -> output.containsKey(state.intValue()))
                   .forEach(state -> output.get(state.intValue()).accept(state));
        return to;
    }
    /**
     * A builder for Moore automatons.
     */
    public static class Builder extends DFA.Builder{
        protected final OutputMap outputs;
        public Builder(){
            this(new Alphabet());
        }
        public Builder(Alphabet alphabet){
            super();
            outputs = new OutputMap();
        }
        /**
         * Sets an action for a state. If the state is visited, the action will
         * be executed.
         * @param state the state.
         * @param output the action that recieves the specified state.
         * @return the previous action or null, if no action was set.
         */
        public Consumer<Integer> setOutput(int state, Consumer<Integer> output){
            return outputs.put(state, output);
        }
        /**
         * @return the Moore automaton. 
         */
        @Override
        public MooreAutomaton build(){
            return new MooreAutomaton(new TransitionTable(table),new StateSet(final_states), new StateSet(initial_states),new OutputMap(outputs));
        }
    }
    
    /**
     * A wrapper class that contains the output for all states.
     */
    protected static class OutputMap extends Int2ObjectOpenHashMap<Consumer<Integer>>{
        private static final long serialVersionUID = 1L;
        /**
         * Creates an empty map.
         */
        public OutputMap(){
            super();
        }
        /**
         * Creates a map over a given input.
         * @param input all entries the map is supposed to contain.
         */
        public OutputMap(Map<Integer,Consumer<Integer>> input){
            super(input);
        }
    }
}