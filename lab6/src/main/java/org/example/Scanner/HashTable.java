package org.example.Scanner;

import java.util.ArrayList;
import java.util.List;

public class HashTable<K,V> {
    private List<LinkedListNode<K,V>> buckets;

    private int numberOfBuckets;

    public HashTable(int numberOfBuckets) {
        this.buckets = new ArrayList<>();
        this.numberOfBuckets = numberOfBuckets;

        for(int i = 0 ; i < numberOfBuckets; i++){
            buckets.add(null);
        }
    }

    public int hashCode(K key){
        int sum_chars = 0;
        char[] key_characters = ((String)key).toCharArray();
        for(char c: key_characters){
            sum_chars += c;
        }
        return sum_chars % numberOfBuckets;
    }

    public V get(K key){
        int hash = hashCode(key);
        LinkedListNode<K,V> head = buckets.get(hash);
        while(head!=null){
            if(head.key.equals(key) && head.hash == hash)
                return head.value;
            head = head.next;
        }
        return null;
    }

    public V add(K key, V value){
        int hash = hashCode(key);
        LinkedListNode<K,V> head = buckets.get(hash);
        while(head!=null){
            if(head.key.equals(key) && head.hash == hash) {
                return head.value;
            }
            head = head.next;
        }
        head = buckets.get(hash);
        LinkedListNode<K,V> newNode = new LinkedListNode<>(key,value,hash);
        if(head == null){
            buckets.set(hash,newNode);
        }else{
            while(head.next != null && head.hash == hash){
                head = head.next;
            }
            head.next = newNode;
        }
        return value;
    }

    public V remove(K key){
        int hash = hashCode(key);
        LinkedListNode<K,V> head = buckets.get(hash);
        LinkedListNode<K,V> prev = null;
        while(head != null ){
            if(head.key.equals(key) && head.hash == hash){
                break;
            }
            prev = head;
            head = head.next;
        }

        if(head == null)
            return null;

        if(prev!=null){
            prev.next = head.next;
        }
        else{
            buckets.set(hash,head.next);
        }

        return head.value;
    }
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < numberOfBuckets; i++) {
            if (buckets.get(i) != null) {
                str.append(buckets.get(i).key.toString()).append(" => ").append(i).append("\n");
            }
        }
        return str.toString();
    }
}
