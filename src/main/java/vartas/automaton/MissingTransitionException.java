package vartas.automaton;

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


/**
 * This exception is thrown when the tokenizer has an invalid token and no
 * default action set.
 * @author Zavarov
 */
public class MissingTransitionException extends RuntimeException{
    private static final long serialVersionUID = 0L;
    /**
     * @param from the state the automaton is currently at.
     * @param with the id of the current word.
     */
    public MissingTransitionException(int from, int with){
        super(String.format("There is no transition for %d from %d.", with, from));
    }
}
