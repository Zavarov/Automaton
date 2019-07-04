package vartas.fa.builder;

import org.junit.Before;
import org.junit.Test;
import vartas.fa.State;

/*
 * Copyright (C) 2019 Zavarov
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
public abstract class FiniteAutomatonBuilderTest <T extends FiniteAutomatonBuilder> {
    T builder;
    State start;
    State end;

    @Before
    public void setUp(){
        start = builder.addInitialState();
        end = builder.addFinalState();
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDuplicateInitialState(){
        builder.addInitialState();
        builder.addInitialState();
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDuplicateNamedInitialState(){
        builder.addInitialState("initial state");
        builder.addInitialState("initial state");
    }
}
