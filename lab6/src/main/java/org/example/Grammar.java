package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Grammar {
    private List<String> nonTerminals ;
    private List<String> terminals;
    private String startSymbol;
    private Map<String, List<List<String>>> productions;

    private String epsilon = "epsilon";

    public Grammar() {
        nonTerminals = new ArrayList<>();
        terminals = new ArrayList<>();
        productions = new HashMap<>();
    }

    public void readFromFile(String filename) {
        String line;
        try (FileReader fileReader = new FileReader(filename); BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            while ((line = bufferedReader.readLine()) != null) {
                switch (line) {
                    case "~nonTerminals" -> nonTerminals.addAll(List.of(bufferedReader.readLine().split(" ")));
                    case "~terminals" -> terminals.addAll(List.of(bufferedReader.readLine().split(" ")));
                    case "~startSymbol" -> startSymbol = bufferedReader.readLine().strip();
                    case "~productions" -> readProductions(bufferedReader);
                }
            }
            System.out.println("Grammar successfully read from file");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readProductions(BufferedReader bufferedReader) throws IOException {
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            String source = line.split("->")[0].strip();
            String sequence = line.split("->")[1];
            if(productions.containsKey(source)) {
                productions.get(source).add(Arrays.asList(sequence.strip().split(" ")));
            }
            else {
                List<List<String>> productionList = new ArrayList<>();
                productionList.add(List.of(sequence.strip().split(" ")));
                productions.put(source, productionList);
            }
        }
    }

    public void printNonTerminals() {
        System.out.println("NON-TERMINALS:");
        for (String nonTerminal : nonTerminals) {
            System.out.println(nonTerminal);
        }
    }

    public void printTerminals() {
        System.out.println("TERMINALS:");
        for (String terminal: terminals) {
            System.out.println(terminal);
        }
    }

    public void printProductions() {
        System.out.println("PRODUCTIONS:");
        StringBuilder rightHandSide = new StringBuilder();
        for (String key : productions.keySet()) {
            List<List<String>> production = productions.get(key);
            for (List<String> productionRightHandSide : production) {
                for (String val : productionRightHandSide) {
                    rightHandSide.append(val).append(" ");
                }
                System.out.println(key + "->" + rightHandSide);
                rightHandSide.setLength(0);
            }
        }
    }

    public void printProductionsForNonterminal(String nonTerminal) {
        System.out.println("PRODUCTIONS FOR " + nonTerminal);
        StringBuilder rightHandSide = new StringBuilder();
        if(productions.containsKey(nonTerminal)) {
            List<List<String>> production = productions.get(nonTerminal);
            for (List<String> productionRightHandSide : production) {
                for (String val : productionRightHandSide) {
                    rightHandSide.append(val).append(" ");
                }
                System.out.println(nonTerminal + "->" + rightHandSide);
                rightHandSide.setLength(0);
            }
        }
        else{
            System.out.println(nonTerminal + " DOES NOT EXIST IN THE GRAMMAR");
        }
    }

    public boolean checkCFG() {
        boolean startingSymbolExists = false;
        for (String key : productions.keySet()) {
            if (Objects.equals(key, startSymbol)) {
                startingSymbolExists = true;
            }
            if (!nonTerminals.contains(key)) {
                return false;
            }
        }
        if (!startingSymbolExists) {
            return false;
        }
        for (List<List<String>> production : productions.values()) {
            for (List<String> productionRightHandSide : production) {
                for (String val : productionRightHandSide) {
                    if (!nonTerminals.contains(val) && !terminals.contains(val) && !Objects.equals(val, epsilon)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
