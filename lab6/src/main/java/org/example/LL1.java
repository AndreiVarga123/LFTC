package org.example;

import java.util.*;

public class LL1 {
    private Grammar grammar;
    private Map<String, Set<String>> first;
    private Map<String, Set<String>> follow;

    public LL1(Grammar grammar) {
        this.grammar = grammar;
        this.first = new HashMap<>();
        this.follow = new HashMap<>();
    }

    public Map<String, Set<String>> getFirst() {
        return first;
    }

    public Map<String, Set<String>> getFollow() {
        return follow;
    }

    public void FIRST() {
        for (String nonTerminal : grammar.getNonTerminals()) {
            first.put(nonTerminal, new HashSet<>());
            List<List<String>> productionsForNonTerminal = grammar.getProductionForNonterminal(nonTerminal);
            for (List<String> production : productionsForNonTerminal) {
                if (grammar.getTerminals().contains(production.get(0)) || production.get(0).equals("epsilon"))
                    first.get(nonTerminal).add(production.get(0));
            }
        }
        boolean isChanged = true;
        while (isChanged) {
            isChanged = false;
            Map<String, Set<String>> currentRow = new HashMap<>();
            for (String nonTerminal : grammar.getNonTerminals()) {
                List<List<String>> productionForNonTerminal = grammar.getProductionForNonterminal(nonTerminal);
                Set<String> currentFirst = new HashSet<>(first.get(nonTerminal));
                for(List<String> production: productionForNonTerminal){
                    List<String> nonTerminals = new ArrayList<>();
                    String terminal = null;
                    for(String element: production){
                        if(grammar.getNonTerminals().contains(element))
                            nonTerminals.add(element);
                        else
                        {
                            terminal = element;
                            break;
                        }
                    }
                    currentFirst.addAll(mergeSets(nonTerminals,terminal));
                }
                if(!currentFirst.equals(first.get(nonTerminal))){
                    isChanged = true;
                }
                currentRow.put(nonTerminal,currentFirst);
            }
            first = currentRow;
        }
    }

    public Set<String> mergeSets(List<String> nonTerminals, String terminal){
        if(nonTerminals.size() < 1)
            return new HashSet<>();
        if(nonTerminals.size() == 1)
            return first.get(nonTerminals.get(0));

        Set<String> result = new HashSet<>();
        int index = 0;
        boolean allNonTerminalsContainEpsilon = true;

        for(String nonTerminal : nonTerminals){
            if(!first.get(nonTerminal).contains("epsilon"))
                allNonTerminalsContainEpsilon = false;
        }

        if(allNonTerminalsContainEpsilon){
            result.add(Objects.requireNonNullElse(terminal,"epsilon"));
        }
        while (index < nonTerminals.size()) {
            boolean nonTerminalContainsEpsilon = false;
            for (String element : first.get(nonTerminals.get(index)))
                if (element.equals("epsilon")) nonTerminalContainsEpsilon = true;
                else result.add(element);
            if (nonTerminalContainsEpsilon) index++;
            else break;
        }
        return result;
    }


    public void FOLLOW() {
        initializeFollowSet();

        boolean isChanged = true;

        while (isChanged) {
            isChanged = false;
            HashMap<String, Set<String>> updatedFollow = new HashMap<>();

            for (String nonTerminal : grammar.getNonTerminals()) {

                updatedFollow.put(nonTerminal, new HashSet<>());
                Set<String> followSetForNonterminal = new HashSet<>(follow.get(nonTerminal));

                // parse productions that contain the nonTerminal on the right
                grammar.getProductionsWithNonTerminalInRHS(nonTerminal).forEach((leftSideOfProduction, rightSideOfProduction) -> {
                    for (List<String> production : rightSideOfProduction) {
                        for (int index = 0; index < production.size(); index++) {
                            if (production.get(index).equals(nonTerminal)) {
                                // A->pB
                                if (index + 1 == production.size()) {
                                    // add FOLLOW(A)
                                    followSetForNonterminal.addAll(follow.get(leftSideOfProduction));
                                } else {
                                    String symbolToFollow = production.get(index + 1);
                                    // A->pBq, q = terminal
                                    if (grammar.getTerminals().contains(symbolToFollow)) {
                                        // add terminal
                                        followSetForNonterminal.add(symbolToFollow);
                                    // A->pBq, q = nonTerminal
                                    } else {
                                        // add { FIRST(q) – epsilon } U { FOLLOW(A), if FIRST(q) contains epsilon }
                                        for (String symbol : first.get(symbolToFollow)) {
                                            if (symbol.equals("epsilon")) {
                                                followSetForNonterminal.addAll(follow.get(leftSideOfProduction));
                                            } else {
                                                followSetForNonterminal.addAll(first.get(symbolToFollow));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                });
                if (!followSetForNonterminal.equals(follow.get(nonTerminal))) {
                    isChanged=true;
                }
                updatedFollow.put(nonTerminal, followSetForNonterminal);
            }
            follow = updatedFollow;
        }
    }

    private void initializeFollowSet() {
        for (String nonTerminal : grammar.getNonTerminals()) {
            follow.put(nonTerminal, new HashSet<>());
        }
        //initialize starting nonTerminal with epsilon
        follow.get(grammar.getStartSymbol()).add("epsilon");
    }

    public void printFollowSet() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Follow:\n");

        for (Map.Entry<String, Set<String>> entry : follow.entrySet()) {
            stringBuilder.append("Key: ").append(entry.getKey()).append(", Values: ").append(entry.getValue()).append("\n");
        }

        System.out.println(stringBuilder);
    }

    public void printFirstSet() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("First Set:\n");

        for (Map.Entry<String, Set<String>> entry : first.entrySet()) {
            stringBuilder.append("Key: ").append(entry.getKey()).append(", Values: ").append(entry.getValue()).append("\n");
        }

        System.out.println(stringBuilder);
    }
}
