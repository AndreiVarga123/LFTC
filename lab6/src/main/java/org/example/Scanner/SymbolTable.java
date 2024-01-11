package org.example.Scanner;

public class SymbolTable {
    private HashTable<String,Integer> symbolTable;

    public SymbolTable(int numberOfBuckets){
        symbolTable = new HashTable<>(numberOfBuckets);
    }

    public Integer addSymbol(String key, Integer value){
        return symbolTable.add(key,value);
    }

    public boolean hasSymbol(String key){
        return symbolTable.get(key) != null;
    }

    public Integer getSymbolPosition(String key){
        return symbolTable.get(key);
    }

    public Integer removeSymbol(String key){
        return symbolTable.remove(key);
    }

    public int hashCode(String key){
        return symbolTable.hashCode(key);
    }
    @Override
    public String toString(){
        return symbolTable.toString();
    }
}
