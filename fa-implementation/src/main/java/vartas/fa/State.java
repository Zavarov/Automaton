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

package vartas.fa;

import de.se_rwth.commons.logging.Log;

import java.util.Optional;

/**
 * This class represents a state in the finite automaton.
 * A state can have a name and can both be an initial and final state.
 */
public class State {
    /**
     * The optional name of the state.
     */
    private final Optional<String> name;
    /**
     * Indicates whether this is a final state.
     */
    private boolean isFinal;
    /**
     * Indicates whether this is an initial state.
     */
    private boolean isInitial;
    /**
     * Creates a new nameless state.
     */
    public State(){
        this.name = Optional.empty();
    }

    /**
     * Creates a new state.
     * @param name the name of the state.
     */
    public State(String name){
        this.name = Optional.of(Log.errorIfNull(name));
    }

    /**
     * @return true if this state has a name.
     */
    public boolean isPresentName(){
        return name.isPresent();
    }

    /**
     * @return the name of the state.
     */
    public String getName(){
        return name.get();
    }

    /**
     * @return the optional containing the potential name.
     */
    public Optional<String> getNameOpt(){
        return name;
    }

    /**
     * @return true if this is a final state.
     */
    public boolean isFinal(){
        return isFinal;
    }

    /**
     * @return true if this an initial state.
     */
    public boolean isInitial(){
        return isInitial;
    }

    /**
     * Sets the flag indicating whether this is a final state.
     * @param isFinal the new flag.
     */
    public void setFinal(boolean isFinal){
        this.isFinal = isFinal;
    }

    /**
     * Sets the flag indicating whether this is an initial state.
     * @param isInitial the new flag.
     */
    public void setInitial(boolean isInitial){
        this.isInitial = isInitial;
    }

    /**
     * @return the name of this state or toString() of the superclass, if this state doesn't have a name.
     */
    @Override
    public String toString(){
        if(isPresentName())
            return getName();
        else
            return super.toString();
    }
}
