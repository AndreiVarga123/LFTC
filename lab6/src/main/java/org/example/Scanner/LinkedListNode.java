package org.example.Scanner;

public class LinkedListNode<K,V> {
    K key;
    V value;
    int hash;
    LinkedListNode<K, V> next;

    public LinkedListNode(K key, V value, int hash) {
        this.key = key;
        this.value = value;
        this.hash = hash;
        this.next = null;
    }
}
