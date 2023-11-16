package org.example;

public class SymTable {
    private HashTable<String, Integer> symTable;

    public SymTable(int capacity) {
        this.symTable = new HashTable<>(capacity);
    }

    public Integer addSymbol(String key){
        return symTable.insert(key,symTable.hash(key));
    }

    public boolean hasSymbol(String key){
        return symTable.get(key) != null;
    }

    public Integer getPosition(String key){
        return symTable.get(key);
    }

    public void removeSymbol(String key){
         symTable.remove(key);
    }

    public String toString(){return symTable.toString();}

    public int getSize(){
        return symTable.getSize();
    }

    public boolean isEmppty(){
        return symTable.isEmpty();
    }
}
