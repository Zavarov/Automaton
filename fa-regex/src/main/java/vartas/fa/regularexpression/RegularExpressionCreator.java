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

package vartas.fa.regularexpression;

import org.apache.commons.lang3.tuple.Pair;
import vartas.fa.NondeterministicFiniteDefaultAutomaton;
import vartas.fa.State;
import vartas.fa.builder.NondeterministicFiniteDefaultAutomatonBuilder;
import vartas.fa.regularexpression._ast.*;
import vartas.fa.regularexpression._visitor.RegularExpressionVisitor;

import java.util.*;

public class RegularExpressionCreator implements RegularExpressionVisitor {
    /**
     * The real visitor instance.
     */
    protected RegularExpressionVisitor realThis;
    /**
     * The builder that will create the finite automaton.
     */
    protected NondeterministicFiniteDefaultAutomatonBuilder builder;
    /**
     * The hookpoints for all subexpressions.
     * The pair will contain the initial state on the left, and the final state on the right.
     */
    protected Map<ASTRegularExpression, Pair<State,State>> hookpoints;

    /**
     * Creates a fresh instance of the creator.
     */
    protected RegularExpressionCreator(){
        realThis = this;
        hookpoints = new HashMap<>();
        builder = new NondeterministicFiniteDefaultAutomatonBuilder();
    }

    /**
     * Transforms the regular expression into an equivalent automaton.
     * @param node the root node of the ast.
     * @return an equivalent automaton.
     */
    public static NondeterministicFiniteDefaultAutomaton createFrom(ASTRegularExpressionArtifact node){
        RegularExpressionCreator creator = new RegularExpressionCreator();

        node.accept(creator.getRealThis());

        return creator.builder.build();
    }

    /**
     * @param realThis the real instance to use for handling and traversing nodes.
     */
    @Override
    public void setRealThis(RegularExpressionVisitor realThis){
        this.realThis = realThis;
    }

    /**
     * @return the real visitor instance.
     */
    @Override
    public RegularExpressionVisitor getRealThis(){
        return realThis;
    }

    /**
     * Starts building the automaton starting from the first expression.
     * Since we don't mark the initial and final states in the subexpressions as such, we have to explicitly create them
     * at the root.
     * @param node the root node of the ast.
     */
    @Override
    public void handle(ASTRegularExpressionArtifact node){
        node.getRegularExpression().accept(getRealThis());

        State oldInitialState = hookpoints.get(node.getRegularExpression()).getKey();
        State oldFinalState = hookpoints.get(node.getRegularExpression()).getValue();
        State newInitialState = builder.addInitialState();
        State newFinalState = builder.addFinalState();

        builder.addEpsilonTransition(newInitialState, oldInitialState);
        builder.addEpsilonTransition(oldFinalState, newFinalState);
    }

    /**
     * Creates the concatenation of all subexpressions.
     * i.e.
     * if a and b are subexpressions, this expression will generate a+b
     * @param node the current expression.
     *             ASTRegularExpression
     */
    @Override
    public void handle(ASTConcatenationExpression node){
        //Build all subexpressions
        node.getLeftExpression().accept(getRealThis());
        node.getRightExpression().accept(getRealThis());

        Pair<State,State> leftHook = hookpoints.get(node.getLeftExpression());
        Pair<State,State> rightHook = hookpoints.get(node.getRightExpression());

        State oldLeftInitialState = leftHook.getKey();
        State oldLeftFinalState = leftHook.getValue();
        State oldRightInitialState = rightHook.getKey();
        State oldRightFinalState = rightHook.getValue();

        //Add the epsilon transition between the final states of the left and the initial state of the right subexpression
        builder.addEpsilonTransition(oldLeftFinalState, oldRightInitialState);

        //The initial state of the left subexpression will be the new initial state
        //And the final state of the right subexpression will be the new final state
        hookpoints.put(node, Pair.of(oldLeftInitialState, oldRightFinalState));
    }

    /**
     * Generates the union of all subexpressions.
     * i.e
     * if a and b are subexpressions, this expression will generate ab
     * @param node the current expression
     */
    @Override
    public void handle(ASTUnionExpression node){
        //Build all subexpressions
        node.getLeftExpression().accept(getRealThis());
        node.getRightExpression().accept(getRealThis());

        //Don't flag the states as initial and final, those will be created by the root
        State newInitialState = builder.addState();
        State newFinalState = builder.addState();

        Pair<State,State> leftHook = hookpoints.get(node.getLeftExpression());
        Pair<State,State> rightHook = hookpoints.get(node.getRightExpression());

        State oldLeftInitialState = leftHook.getKey();
        State oldLeftFinalState = leftHook.getValue();
        State oldRightInitialState = rightHook.getKey();
        State oldRightFinalState = rightHook.getValue();

        //Link the old initial states to the new initial state
        builder.addEpsilonTransition(newInitialState, oldLeftInitialState);
        builder.addEpsilonTransition(newInitialState, oldRightInitialState);

        //Link all old final states to the new final state
        builder.addEpsilonTransition(oldLeftFinalState, newFinalState);
        builder.addEpsilonTransition(oldRightFinalState, newFinalState);

        hookpoints.put(node, Pair.of(newInitialState, newFinalState));
    }

    /**
     * Generates the kleene expression of the subexpression if the kleene star exists.
     * @param node the current expressions
     */
    @Override
    public void handle(ASTKleeneExpression node){
        node.getExpression().accept(getRealThis());

        //Don't flag the states as initial and final, those will be created by the root
        State newInitialState = builder.addState();
        State newFinalState = builder.addState();

        //We only have one hookpoint from the subexpression
        Pair<State,State> hook = hookpoints.get(node.getExpression());

        State oldInitialState = hook.getKey();
        State oldFinalState = hook.getValue();

        //Add epsilon transitions from the final state to the initial state to allow repetitions
        builder.addEpsilonTransition(oldFinalState, oldInitialState);
        //Link the final state of the subexpression to the final state of the new expression
        builder.addEpsilonTransition(oldFinalState, newFinalState);
        //Link the initial state of the new expression to the initial state of the subexpression
        builder.addEpsilonTransition(newInitialState, oldInitialState);
        //Add an epsilon transition between the new initial and final state to match the empty word
        builder.addEpsilonTransition(newInitialState, newFinalState);

        hookpoints.put(node, Pair.of(newInitialState, newFinalState));
    }

    /**
     * Adds a wildcard transition to the automaton.
     * @param node the currently visited transition.
     */
    @Override
    public void handle(ASTWildcardExpression node){
        //Don't flag the states as initial and final, those will be created by the root
        State newInitialState = builder.addState();
        State newFinalState = builder.addState();

        builder.addDefaultTransition(newInitialState, newFinalState);
        hookpoints.put(node, Pair.of(newInitialState, newFinalState));
    }

    /**
     * Adds a transition for each character in the interval to the automaton.
     * @param node the currently visited transition.
     */
    @Override
    public void handle(ASTIntervalExpression node){
        //Don't flag the states as initial and final, those will be created by the root
        State newInitialState = builder.addState();
        State newFinalState = builder.addState();

        char current = node.getStart().getValue();
        char end = node.getEnd().getValue();

        while(current <= end){
            builder.addTransition(newInitialState, current, newFinalState);
            ++current;
        }
        hookpoints.put(node, Pair.of(newInitialState, newFinalState));
    }

    /**
     * Adds a normal transition to the automaton.
     * @param node the currently visited transition.
     */
    @Override
    public void handle(ASTCharacterExpression node){
        //Don't flag the states as initial and final, those will be created by the root
        State newInitialState = builder.addState();
        State newFinalState = builder.addState();

        builder.addTransition(newInitialState, node.getValue(), newFinalState);

        hookpoints.put(node, Pair.of(newInitialState, newFinalState));
    }
}
