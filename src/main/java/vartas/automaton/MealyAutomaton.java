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

import com.google.common.collect.ForwardingTable;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import java.util.function.BiConsumer;
import vartas.automaton.alphabet.Alphabet;

/**
 * Creates a Mealy-Automaton.
 * @author Zavarov
 */
public class MealyAutomaton extends DFA{
    /**
     * Stores the output for all transitions
     */
    protected final OutputTable output;
    /**
     * @param transitions the transition relation.
     * @param final_states the set of final states.
     * @param initial_states the set of initial states.
     * @param output the table containing the actions for when a transition is taken.
     */
    protected MealyAutomaton(TransitionTable transitions, StateSet final_states, StateSet initial_states, OutputTable output){
        super(transitions, final_states, initial_states);
        this.output = output;
    }
    /**
     * Acts just like a normal finite automaton. However, if a transitions is
     * visitied, which has an action assignedf to it, the action will be executed.
     * @param from the starting state.
     * @param with the letter.
     * @return the leading state.
     */
    @Override
    public StateSet step(StateSet from, int with) {
        StateSet to = super.step(from, with);
        to.stream()
            .filter(state -> output.contains(state, with))
            .forEach(state -> output.get(state, with).accept(state, with));
        return to;
    }
    /**
     * A builder for making Mealy automatons.
     */
    public static class Builder extends DFA.Builder{
        protected final OutputTable outputs;
        public Builder(){
            this(new Alphabet());
        }
        public Builder(Alphabet alphabet){
            super();
            outputs = new OutputTable();
        }
        /**
         * Sets an action for a specific transition. If the transition is used,
         * the action will be executed.
         * @param state the starting state.
         * @param token the id.
         * @param output the action that accepts the current state as the first and the current token as the second parameter.
         * @return the previous action or null, if no action was set before.
         */
        public BiConsumer<Integer,Integer> setOutput(int state, int token, BiConsumer<Integer,Integer> output){
            return outputs.put(state, token, output);
        }
        /**
         * @return the Mealy automaton. 
         */
        @Override
        public MealyAutomaton build(){
            return new MealyAutomaton(new TransitionTable(table),new StateSet(final_states), new StateSet(initial_states),new OutputTable(outputs));
        }
    }
    /**
     * A wrapper class that contains the output for all (state,token) pairs.
     */
    protected static class OutputTable extends ForwardingTable<Integer,Integer,BiConsumer<Integer,Integer>>{
        /**
         * The internal table.
         */
        protected final Table<Integer,Integer,BiConsumer<Integer,Integer>> table;
        /**
         * Creates an empty table.
         */
        public OutputTable(){
            this.table = HashBasedTable.create();
        }
        /**
         * Creates a table over a given input.
         * @param input all entries the table is supposed to contain.
         */
        public OutputTable(Table<Integer,Integer,BiConsumer<Integer,Integer>> input){
            this.table = HashBasedTable.create(input);
        }
        /**
         * All methods in this class will operate via this method.
         * @return the internal  table.
         */
        @Override
        protected Table<Integer, Integer, BiConsumer<Integer,Integer>> delegate() {
            return table;
        }
    }
}