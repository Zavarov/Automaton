/*
 * Copyright (c) 2019 Zavarov
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

package vartas.fa.finiteautomaton;

import vartas.fa.NondeterministicFiniteDefaultAutomaton;
import vartas.fa.State;
import vartas.fa.builder.NondeterministicFiniteDefaultAutomatonBuilder;
import vartas.fa.finiteautomaton._ast.*;
import vartas.fa.finiteautomaton._visitor.FiniteAutomatonVisitor;

import java.util.HashMap;
import java.util.Map;

/**
 * This class transforms the ast into a runnable instance.
 */
public class FiniteAutomatonCreator implements FiniteAutomatonVisitor {
    /**
     * The real visitor instance.
     */
    protected FiniteAutomatonVisitor realThis;
    /**
     * The builder that will create the finite automaton.
     */
    protected NondeterministicFiniteDefaultAutomatonBuilder builder;
    /**
     * A map that relates the name of a state to its instance in the newly created automaton .
     */
    protected Map<String, State> stateMap;

    /**
     * Creates a fresh instance of the creator.
     */
    protected FiniteAutomatonCreator(){
        realThis = this;

        stateMap = new HashMap<>();
        builder = new NondeterministicFiniteDefaultAutomatonBuilder();
    }

    /**
     * Transforms the ast into an equivalent automaton.
     * @param node the root node of the ast.
     * @return an equivalent automaton.
     */
    public static NondeterministicFiniteDefaultAutomaton createFrom(ASTFiniteAutomaton node){
        FiniteAutomatonCreator creator = new FiniteAutomatonCreator();

        node.accept(creator.getRealThis());

        return creator.builder.build();
    }

    /**
     * @param realThis the real instance to use for handling and traversing nodes.
     */
    @Override
    public void setRealThis(FiniteAutomatonVisitor realThis){
        this.realThis = realThis;
    }

    /**
     * @return the real visitor instance.
     */
    @Override
    public FiniteAutomatonVisitor getRealThis(){
        return realThis;
    }

    /**
     * Starts building the automaton starting from the initial state.
     * To allow customization, we will create a new initial state and link it to
     * all possible former initial states via epsilon transitions.
     * @param node the root node of the ast.
     */
    @Override
    public void handle(ASTFiniteAutomaton node){
        FiniteAutomatonVisitor.super.handle(node);

        State initialState = builder.addInitialState();

        stateMap.values().stream().filter(State::isInitial).forEach(oldInitialState -> {
            oldInitialState.setInitial(false);
            builder.addEpsilonTransition(initialState, oldInitialState);
        });
    }

    /**
     * Adds a state to the automaton.
     * @param node the currently visited state.
     */
    @Override
    public void handle(ASTState node){
        State state = builder.addState(node.getName());
        state.setInitial(node.isInitial());
        state.setFinal(node.isFinal());
        stateMap.put(node.getName(), state);
    }

    /**
     * Adds an epsilon transition to the automaton.
     * @param node the currently visited transition.
     */
    @Override
    public void handle(ASTEpsilonTransition node){
        State from = stateMap.computeIfAbsent(node.getFrom(), builder::addState);
        State to = stateMap.computeIfAbsent(node.getTo(), builder::addState);

        builder.addEpsilonTransition(from, to);
    }

    /**
     * Adds an 'else' transition to the automaton.
     * @param node the currently visited transition.
     */
    @Override
    public void handle(ASTDefaultTransition node){
        State from = stateMap.computeIfAbsent(node.getFrom(), builder::addState);
        State to = stateMap.computeIfAbsent(node.getTo(), builder::addState);

        builder.addDefaultTransition(from, to);
    }

    /**
     * Adds a normal transition to the automaton.
     * @param node the currently visited transition.
     */
    @Override
    public void handle(ASTBasicTransition node){
        State from = stateMap.computeIfAbsent(node.getFrom(), builder::addState);
        char with = node.getWith().getValue();
        State to = stateMap.computeIfAbsent(node.getTo(), builder::addState);

        builder.addTransition(from, with, to);
    }
}
