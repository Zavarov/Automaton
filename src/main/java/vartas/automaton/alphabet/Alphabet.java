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

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.bytes.ByteArrays;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.nio.charset.StandardCharsets;
import java.util.AbstractSet;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import org.apache.commons.lang3.tuple.Pair;
import vartas.automaton.alphabet.Alphabet.Node;

/**
 * An memory-efficient implementation of an alphabet. Each String in this alphabet
 * will be assigned to an unique id, so that it can be used instead of the String.
 * @author Zavarov
 */
public class Alphabet extends AbstractSet<String>{
    /**
     * The id of the empty word that can't be added to the alphabet.
     */
    public static final int EPSILON = -1;
    /**
     * The id that can be assigned to all invalid tokens that aren't part of the alphabet.
     */
    public static final int ERROR = -2;
    /**
     * The last id that is assigned to a word before the alphabet is full.
     */
    public static final int LAST_ID = -3;
    /**
     * A map pointing to the end of every word to make recreating the String easier.
     */
    protected final BiMap<Node,Integer> data = HashBiMap.create();
    /**
     * The head of the internal tree.
     */
    protected final Node head = new Node(new byte[0],null);
    /**
     * The id that will be used for the next word that is added into the alphabet.
     */
    protected int next_id = 0;
    /**
     * A set of resuable ids of words that haven been removed.
     */
    protected final Deque<Integer> reusable = new ArrayDeque<>();
    /**
     * Creates an empty alphabet.
     */
    public Alphabet(){}
    /**
     * Adds a new word to the alphabet, if it isn't already in it.
     * @param value the word that is added to the alphabet.
     * @return true if the alphabet was changed as a result of this operation.
     */
    @Override
    public boolean add(String value){
        //We've gone full circle
        if(next_id == LAST_ID+1 && reusable.isEmpty()){
            return false;
        }else{
            Node node = head.next(value.getBytes(StandardCharsets.UTF_8));
            if(data.containsKey(node)){
                return false;
            }else{
                int id = reusable.isEmpty() ? next_id++ : reusable.pop();
                data.put(node, id);
                return true;
            }
        }
    }
    /**
     * @return an iterator over all words in this alphabet. 
     */
    @Override
    public Iterator<String> iterator() {
        Iterator<Node> iterator = data.keySet().iterator();
        //A wrapper that transforms each node into the word in the alphabet
        return new Iterator<String>(){
            protected Node next;
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public String next() {
                next = iterator.next();
                return next.toString();
            }

            @Override
            public void remove(){
                next.remove();
                reusable.push(data.get(next));
                iterator.remove();
            }
        };
    }
    /**
     * In case the set contains more than Integer.MAX_VALUE values,
     * Integer.MAX_VALUE is returned.
     * @return the number of elements in the set. 
     */
    @Override
    public int size() {
        return data.size();
    }
    /**
     * @return all integer that are assigned to words in this alphabet.
     */
    public Set<Integer> ids(){
        return data.values();
    }
    /**
     * @param id the unique id of a word in this alphabet.
     * @return the String that has been assigned to that specific id.
     * @throws IdNotInAlphabetException if the id doesn't correspond to a string.
     */
    public String get(int id) throws IdNotInAlphabetException{
        if(data.containsValue(id)){
            return data.inverse().get(id).toString();
        }else{
            throw new IdNotInAlphabetException(id);
        }
    }
    /**
     * @param value the word that is supposed to be in the set.
     * @return the unique id assigned to that value.
     * @throws WordNotInAlphabetException if the word is not in the alphabet. 
     */
    public int get(String value) throws WordNotInAlphabetException{
        Node node = head.next(value.getBytes(StandardCharsets.UTF_8));
        if(data.containsKey(node)){
            return data.get(node);
        }else{
            node.remove();
            throw new WordNotInAlphabetException(value);
        }
    }
    
    /**
     * A single node of the interal tree-structure of the alphabet.
     */
    public static class Node{
        /**
         * A segment of the word.
         */
        protected byte[] entry;
        /**
         * The parent node.
         */
        protected Node parent;
        /**
         * A set of all child nodes.
         */
        protected final List<Node> children = new ObjectArrayList<>();
        /**
         * @param entry a chunk of the word.
         * @param parent the parent node.
         */
        public Node(byte[] entry, Node parent){
            this.entry=entry;
            this.parent=parent;
        }
        /**
         * This method traverses over the nodes whose sequence matches the given
         * input. If necessary, some nodes will be split or added during the process.
         * @param sequence a chuck of the word.
         * @return the node that has been reached after traversing through the tree.
         */
        public Node next(byte[] sequence){
            Pair<Node,ByteArrayList> current_pair = Pair.of(this, new ByteArrayList(sequence));
            while(current_pair.getRight().size() > 0){
                current_pair = step(current_pair);
            }
            return current_pair.getLeft();
        }
        /**
         * 
         * @param current_pair a pair consisting of the current node and tsequence.
         * @return the next node and the remaining sequence.
         */
        protected Pair<Node,ByteArrayList> step(Pair<Node,ByteArrayList> current_pair){
            Node node = current_pair.getLeft(), child;
            byte[] sequence = current_pair.getRight().elements();
            
            //Search for a matching child
            ListIterator<Node> iterator = node.children.listIterator();
            while(iterator.hasNext()){
                child = iterator.next();
                //The entry of the current child is a prefix of the sequence
                if(isPrefix(sequence,child.entry)){
                    byte[] sequence_rest = ByteArrays.copy(sequence, child.entry.length, sequence.length-child.entry.length);
                    return Pair.of(child, new ByteArrayList(sequence_rest));
                //The entry of the current child and the sequence share at least one byte
                }else if(commonPrefix(sequence,child.entry) > 0){
                    int size = commonPrefix(sequence,child.entry);
                    byte[] prefix = ByteArrays.copy(sequence, 0, size);
                    byte[] child_rest = ByteArrays.copy(child.entry, size, child.entry.length-size);
                    byte[] sequence_rest = ByteArrays.copy(sequence, size, sequence.length-size);
                    Node split = new Node(prefix,child.parent);
                    iterator.set(split);
                    split.children.add(child);
                    child.entry = child_rest;
                    child.parent = split;

                    return Pair.of(split, new ByteArrayList(sequence_rest));
                }
            }
            //The sequence and all entries in the child nodes are distinct
            Node next = new Node(sequence,node);
            node.children.add(next);
            return Pair.of(next, new ByteArrayList());
        }
        /**
         * Checks if the second array is a prefix of the first array.<br>
         * i.e if the first array starts will all elements in the second array.
         * @param source the first array.
         * @param prefix the second array.
         * @return true if the first array starts with the elements of the second array.
         */
        protected boolean isPrefix(byte[] source, byte[] prefix){
            if(prefix.length > source.length){
                return false;
            }else{
                for(int i = 0 ; i < prefix.length ; ++i){
                    if(source[i] != prefix[i]){
                        return false;
                    }
                }
                return true;
            }
        }
        /**
         * Checks if the first and the second array share a common prefix.
         * @param first the first array.
         * @param second the second array.
         * @return the number of common numbers, both arrays start with.
         */
        protected int commonPrefix(byte[] first, byte[] second){
            int i = 0;
            while(i < Math.min(first.length, second.length) && first[i] == second[i]){
                ++i;
            }
            return i;
        }
        /**
         * @return the word represented by the path from the head to the current node. 
         */
        @Override
        public String toString(){
            StringBuilder builder = new StringBuilder();
            Node current = this;
            do{
                builder.insert(0, new String(current.entry,StandardCharsets.UTF_8));
                current = current.parent;
            }while(current != null);
            return builder.toString();
        }
        /**
         * Removes the current node and all nodes in the same branch.<br>
         * The head of the tree can't be removed.
         */
        public void remove(){
            Node current = this, current_parent;
            //Stop at the head or when a different branch exists
            while(current.parent != null && current.parent.children.size() == 1){
                current = current.parent;
            }
            
            current_parent = current.parent;
            //Current is the head
            if(current_parent == null){
                current.children.remove(this);
            }else{
                //Remove this branch
                current_parent.children.remove(current);
                //Reverse the split, if possible
                if(current_parent.children.size() == 1){
                    Node child = current.parent.children.remove(0);
                    //elements() would round up to the nearest power of 2
                    ByteArrayList sum = new ByteArrayList(current_parent.entry.length + child.entry.length);
                    sum.addAll(new ByteArrayList(current_parent.entry));
                    sum.addAll(new ByteArrayList(child.entry));
                    current_parent.entry = sum.elements();
                    
                    //Update the dependencies of the other nodes
                    child.children.forEach(node -> {
                        current_parent.children.add(node);
                        node.parent = current_parent;
                    });
                }
            }
        }
    }
}