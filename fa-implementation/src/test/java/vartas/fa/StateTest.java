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

import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class StateTest {
    State state;
    @Before
    public void setUp(){
        state = new State("state");
        state.setFinal(true);
        state.setInitial(true);
    }

    @Test
    public void testIsPresentName(){
        assertThat(state.isPresentName()).isTrue();
    }

    @Test
    public void testGetName(){
        assertThat(state.getName()).isEqualTo("state");
    }

    @Test
    public void testGetNameOpt(){
        assertThat(state.getNameOpt()).isEqualTo(Optional.of("state"));
    }

    @Test
    public void testToStringWithName(){
        assertThat(state.toString()).isEqualTo("state");
    }

    @Test
    public void testIsInitial(){
        assertThat(state.isInitial()).isTrue();
    }

    @Test
    public void testIsFinal(){
        assertThat(state.isFinal()).isTrue();
    }
}
