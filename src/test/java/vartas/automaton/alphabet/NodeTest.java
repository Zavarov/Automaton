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
package vartas.automaton.alphabet;

import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import java.nio.charset.StandardCharsets;
import org.apache.commons.lang3.tuple.Pair;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import vartas.automaton.alphabet.Alphabet.Node;

/**
 *
 * @author Zavarov
 */
public class NodeTest {
    Node head,aaa,aa,bb,cc;
    @Before
    public void setUp(){
        head = new Node(new byte[0],null);
        aaa = new Node("aaa".getBytes(StandardCharsets.UTF_8),head);
        aa = new Node("aa".getBytes(StandardCharsets.UTF_8),aaa);
        bb = new Node("bb".getBytes(StandardCharsets.UTF_8),aaa);
        cc = new Node("cc".getBytes(StandardCharsets.UTF_8),aa);
        head.children.add(aaa);
        aaa.children.add(aa);
        aaa.children.add(bb);
        aa.children.add(cc);
    }
    
    @Test
    public void nextTest(){
        Node node = head.next("aaaaacc".getBytes(StandardCharsets.UTF_8));
        assertEquals(node,cc);
        node = head.next("aaaaabb".getBytes(StandardCharsets.UTF_8));
        assertEquals(aa.children.size(),2);
        assertTrue(aa.children.contains(cc));
        assertTrue(aa.children.contains(node));
        assertArrayEquals(aa.entry,"aa".getBytes(StandardCharsets.UTF_8));
        assertArrayEquals(node.entry,"bb".getBytes(StandardCharsets.UTF_8));
    }
    
    @Test
    public void stepPrefixLeftTest(){
        Pair<Node,ByteArrayList> pair = head.step(Pair.of(head,new ByteArrayList("aaab".getBytes(StandardCharsets.UTF_8))));
        
        assertEquals(head.children.size(),1);
        assertTrue(head.children.contains(aaa));
        assertEquals(pair.getLeft(),aaa);
        assertArrayEquals(pair.getRight().elements(),"b".getBytes(StandardCharsets.UTF_8));
    }
    
    @Test
    public void stepPrefixRightTest(){
        Pair<Node,ByteArrayList> pair = head.step(Pair.of(head,new ByteArrayList("aa".getBytes(StandardCharsets.UTF_8))));
        
        assertEquals(head.children.size(),1);
        assertTrue(head.children.contains(pair.getLeft()));
        assertArrayEquals(pair.getLeft().entry,"aa".getBytes(StandardCharsets.UTF_8));
        assertEquals(pair.getLeft().parent,head);
        
        assertTrue(pair.getLeft().children.contains(aaa));
        assertTrue(head.children.contains(pair.getLeft()));
        assertArrayEquals(aaa.entry,"a".getBytes(StandardCharsets.UTF_8));
        assertEquals(aaa.parent,pair.getLeft());
        
        assertTrue(pair.getRight().isEmpty());
    }
    
    @Test
    public void stepCommonPrefixTest(){
        Pair<Node,ByteArrayList> pair = head.step(Pair.of(head,new ByteArrayList("aab".getBytes(StandardCharsets.UTF_8))));
        
        assertEquals(head.children.size(),1);
        assertTrue(head.children.contains(pair.getLeft()));
        assertArrayEquals(pair.getLeft().entry,"aa".getBytes(StandardCharsets.UTF_8));
        assertEquals(pair.getLeft().parent,head);
        
        assertTrue(pair.getLeft().children.contains(aaa));
        assertTrue(head.children.contains(pair.getLeft()));
        assertArrayEquals(aaa.entry,"a".getBytes(StandardCharsets.UTF_8));
        assertEquals(aaa.parent,pair.getLeft());
        
        assertArrayEquals(pair.getRight().elements(),"b".getBytes(StandardCharsets.UTF_8));
        
    }
    
    @Test
    public void stepDistinctTest(){
        Pair<Node,ByteArrayList> pair = head.step(Pair.of(head,new ByteArrayList("bb".getBytes(StandardCharsets.UTF_8))));
        
        assertEquals(head.children.size(),2);
        assertTrue(head.children.contains(aaa));
        assertTrue(head.children.contains(pair.getLeft()));
        assertEquals(pair.getLeft().parent,head);
        assertArrayEquals(pair.getLeft().entry,"bb".getBytes(StandardCharsets.UTF_8));
        assertTrue(pair.getRight().isEmpty());
    }
    
    @Test
    public void commonPrefixTest(){
        assertEquals(head.commonPrefix(new byte[]{1,2,3}, new byte[]{9,9,9}),0);
        assertEquals(head.commonPrefix(new byte[]{1,2,3}, new byte[]{1,9,9}),1);
        assertEquals(head.commonPrefix(new byte[]{1,2,3}, new byte[]{1,2,9}),2);
        assertEquals(head.commonPrefix(new byte[]{1,2,3}, new byte[]{1,2,3}),3);
    }
    @Test
    public void isPrefixTooLargeTest(){
        assertFalse(head.isPrefix(new byte[]{1,2,3}, new byte[]{1,2,3,4}));
    }
    @Test
    public void isPrefixTest(){
        assertTrue(head.isPrefix(new byte[]{1,2,3}, new byte[]{1,2,3}));
        assertTrue(head.isPrefix(new byte[]{1,2,3}, new byte[]{1,2}));
        assertTrue(head.isPrefix(new byte[]{1,2,3}, new byte[]{1}));
    }
    @Test
    public void isNotPrefixTest(){
        assertFalse(head.isPrefix(new byte[]{1,2,3}, new byte[]{9,9,9}));
        assertFalse(head.isPrefix(new byte[]{1,2,3}, new byte[]{1,9,9}));
        assertFalse(head.isPrefix(new byte[]{1,2,3}, new byte[]{1,2,9}));
    }
    @Test
    public void toStringTest(){
        assertEquals(head.toString(),"");
        assertEquals(aaa.toString(),"aaa");
        assertEquals(aa.toString(),"aaaaa");
        assertEquals(bb.toString(),"aaabb");
        assertEquals(cc.toString(),"aaaaacc");
    }
    @Test
    public void removeTest(){
        cc.remove();
        assertArrayEquals(aaa.entry,"aaabb".getBytes(StandardCharsets.UTF_8));
        assertTrue(aaa.children.isEmpty());
        aaa.remove();
        assertTrue(head.children.isEmpty());
        head.remove();
        assertTrue(head.children.isEmpty());
    }
    @Test
    public void removeDependenciesTest(){
        Node dd = new Node("bb".getBytes(StandardCharsets.UTF_8),bb);
        bb.children.add(dd);
        cc.remove();
        assertArrayEquals(aaa.entry,"aaabb".getBytes(StandardCharsets.UTF_8));
        assertEquals(dd.parent,aaa);
        assertEquals(aaa.children.size(),1);
        assertTrue(aaa.children.contains(dd));
    }
    @Test
    public void removeMulipleBranchesTest(){
        Node dd = new Node("bb".getBytes(StandardCharsets.UTF_8),aaa);
        aaa.children.add(dd);
        cc.remove();
        assertArrayEquals(aaa.entry,"aaa".getBytes(StandardCharsets.UTF_8));
        assertEquals(dd.parent,aaa);
        assertEquals(bb.parent,aaa);
        assertEquals(aaa.children.size(),2);
        assertTrue(aaa.children.contains(bb));
        assertTrue(aaa.children.contains(dd));
    }
}
