package org.example;

public class SymTable {
    private HashTable<String, Integer> symTable;

    public SymTable(int capacity) {
        this.symTable = new HashTable<>(capacity);
    }

    public Integer addSymbol(String key,Integer value){
        return symTable.insert(key,value);
    }

    public boolean hasSymbol(String key){
        return symTable.get(key) != null;
    }

    public Integer getSymbolPosition(String key){
        return symTable.get(key);
    }

    public void removeSymbol(String key){
         symTable.remove(key);
    }
}
