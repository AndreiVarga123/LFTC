package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class Node<K,V>{
     K key;
     V value;
     Node<K,V> next;

    public Node(K key, V value) {
        this.key = key;
        this.value = value;
        next = null;
    }
}

public class HashTable<K,V> {

    private List<Node<K,V>> table;
    private final int capacity;
    private int size;

    public HashTable(int capacity) {
        this.capacity = capacity;
        table = new ArrayList<>();
        for(int i=0;i<capacity;i++){
            table.add(i,null);
        }
    }

    private int hash(K key){
        return Objects.hashCode(key) % capacity;
    }

    public V insert(K key, V value){
        size++;
        int index = hash(key);
        Node<K,V> currNode = table.get(index), prevNode=null;
        V oldVal = null;
        
        while(currNode != null && currNode.key!=key){
            prevNode = currNode;
            currNode = currNode.next;
        }//find the prev node, so we can link the new node

        if(currNode!=null){
            oldVal = currNode.value;
            currNode.value = value;
        }
        else if(prevNode!=null)
            prevNode.next = new Node<>(key,value);//if prev node exists link it to the end of chain
        else
            table.add(index, new Node<>(key, value));//if prev node doesn't exist make it head of chain

        return oldVal;
    }

    public V get(K key){
        int index = hash(key);
        Node<K,V> currNode = table.get(index);

        while(currNode != null && currNode.key!=key){
            currNode = currNode.next;
        }//find node with key

        if(currNode!=null)
            return currNode.value;
        return null;
    }

    public void remove(K key){
        size--;
        int index = hash(key);
        Node<K,V> currNode = table.get(index), prevNode=null;

        while(currNode!=null && currNode.key != key){
            prevNode = currNode;
            currNode = currNode.next;
        }

        if(prevNode!=null)
            prevNode.next = currNode!=null ? currNode.next : null;
        else
            table.remove(index);
    }

    public int getSize() {
        return size;
    }

    public boolean isEmpty(){
        return size==0;
    }
}


