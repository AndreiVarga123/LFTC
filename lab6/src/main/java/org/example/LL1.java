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

    public Map<String, Set<String>> getFirstSet() {
        return first;
    }

    public Map<String, Set<String>> getFollowSet() {
        return follow;
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
