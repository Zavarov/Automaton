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

package vartas.fa.finiteautomaton._visitor;

import vartas.fa.finiteautomaton._ast.ASTFiniteAutomaton;
import vartas.fa.finiteautomaton._ast.ASTState;
import vartas.fa.finiteautomaton._ast.ASTTransition;
import vartas.fa.finiteautomaton._symboltable.StateSymbol;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * This class iterates over all states in the finite automaton.
 */
public class StateVisitor implements FiniteAutomatonVisitor{
    /**
     * The real visitor instance.
     */
    protected FiniteAutomatonVisitor realThis;
    /**
     * A collection of all visited states to prevent loops.
     */
    protected Set<ASTState> visitedStates;
    /**
     * The reference automaton.
     */
    protected ASTFiniteAutomaton ast;

    /**
     * Creates a new visitor instance.
     */
    public StateVisitor(){
        this.realThis = this;
        this.visitedStates = new HashSet<>();
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
     * The core method.
     * It clears all visited states in previously runs and starts traversing at the initial state.
     * @param ast the head of the finite automaton.
     */
    @Override
    public void handle(ASTFiniteAutomaton ast){
        this.visitedStates.clear();
        this.ast = ast;

        ast.getInitialState().ifPresent(state -> state.accept(getRealThis()));
    }

    /**
     * Adds the currently visited state to the list and continues with
     * all unvisited states that can be directly reached via transitions.
     * @param state the currently visited state.
     */
    @Override
    public void handle(ASTState state){
        visitedStates.add(state);

        ast.getTransitionList().stream()
                .filter(e -> e.getFrom().equals(state.getName()))
                .map(ASTTransition::getTo)
                .map(this::resolveState)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(e -> !visitedStates.contains(e))
                .forEach(e -> e.accept(getRealThis()));
    }

    /**
     * A helper function that resolves states based on their name.
     * @param name the name of a state.
     * @return the state that is referenced by that name.
     */
    private Optional<ASTState> resolveState(String name){
        String qualifiedName = ast.getName() + "." + name;

        Optional<StateSymbol> symbol = ast.getEnclosingScope().resolve(qualifiedName, StateSymbol.KIND);

        if(symbol.isPresent())
            return symbol.get().getStateNode();
        else
            return Optional.empty();
    }
}
