package org.example.Parser;

import java.util.*;

public class LL1 {
    private Grammar grammar;
    private Map<String, Set<String>> first;
    private Map<String, Set<String>> follow;

    //pair of row,col has a pair of rhs,production_index
    private Map<Pair, Pair> parsingTable;
    private List<List<String>> productionsRHS;

    public LL1(Grammar grammar) {
        this.grammar = grammar;
        this.first = new HashMap<>();
        this.follow = new HashMap<>();
        this.parsingTable = new HashMap<>();

    }

    public Map<String, Set<String>> getFirst() {
        return first;
    }

    public Map<String, Set<String>> getFollow() {
        return follow;
    }

    public Grammar getGrammar() {
        return grammar;
    }

    public List<String> getProductionRHSByLevel(int level) {
        var production = productionsRHS.get(level - 1);
        if (production.contains("epsilon")) {
            return List.of("epsilon");
        }
        return production;
    }

    public void ParsingTable() {
        List<String> nonTerminals = grammar.getNonTerminals();
        List<String> terminals = grammar.getTerminals();

        // one line for each symbol = non terminal + terminal + $
        List<String> rows = new ArrayList<>();
        rows.addAll(nonTerminals);
        rows.addAll(terminals);
        rows.add("$");

        //one col for each symbol = terminal + $
        List<String> cols = new ArrayList<>();
        cols.addAll(terminals);
        cols.add("$");

        // initialize table with (none, -1)
        for (var row : rows)
            for (var col : cols)
                parsingTable.put(new Pair<>(row, col), new Pair<>("none", -1));

        // now complete the pop for the terminals
        for (var col : cols)
            parsingTable.put(new Pair<>(col, col), new Pair<>("pop", -1));

        // now put the acceptance
        parsingTable.put(new Pair<>("$", "$"), new Pair<>("acc", -1));

        var productions = grammar.getProductions();
        this.productionsRHS = new ArrayList<>();

        //create list of rhs of productions
        for (String key : productions.keySet()) {
            for (List<String> prod : productions.get(key)) {
                if (prod.get(0).equals("epsilon"))
                    productionsRHS.add(new ArrayList<>(List.of("epsilon", key)));
                else
                    productionsRHS.add(new ArrayList<>(prod));
            }
        }

        for (String key : productions.keySet()) {
            for (List<String> prod : productions.get(key)) {
                String firstSymbol = prod.get(0);

                // here we treat the case where the first symbol in the rhs of a production is a terminal,
                // so we verify if it can be added to the parsing table (and add it) or if we have a
                // conflict
                if (terminals.contains(firstSymbol)) {
                    if (parsingTable.get(new Pair<>(key, firstSymbol)).getFirst().equals("none"))
                        parsingTable.put(new Pair<>(key, firstSymbol), new Pair<>(String.join(" ", prod), productionsRHS.indexOf(prod) + 1));
                    else {
                        try {
                            throw new Exception("CONFLICT: " + key + ", " + firstSymbol);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                // here we treat the case where the first symbol in the rhs of a production is a nonTerminal,
                else if (nonTerminals.contains(firstSymbol)) {

                    // if the production rhs has only one nonTerminal, we get FIRST of that nonTerminal
                    // for each element of FIRST we try to add it to table or signal conflict
                    if (prod.size() == 1) {
                        for (var symbol : first.get(firstSymbol)) {
                            if (parsingTable.get(new Pair<>(key, symbol)).getFirst().equals("none"))
                                parsingTable.put(new Pair<>(key, symbol), new Pair<>(String.join(" ", prod), productionsRHS.indexOf(prod) + 1));
                            else {
                                try {
                                    throw new Exception("CONFLICT: " + key + ", " + symbol);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    // if not, we take the next symbol in the current production
                    else {
                        var nextSymbol = prod.get(1);
                        var firstOfCurrentProduction = first.get(firstSymbol);
                        var i = 1;

                        // we keep parsing symbols until a terminal or end of production
                        // check if first of nonTerminal contains epsilon
                        // if it does add first of next symbol as well
                        while (i < prod.size() && nonTerminals.contains(nextSymbol)) {
                            var firstOfNextSymbol = first.get(nextSymbol);

                            if (firstOfCurrentProduction.contains("epsilon")) {
                                firstOfCurrentProduction.remove("epsilon");
                                firstOfCurrentProduction.addAll(firstOfNextSymbol);
                                break;
                            }

                            i++;
                            if (i < prod.size())
                                nextSymbol = prod.get(i);
                        }

                        // for each symbol try to add it or signal conflict
                        for (var symbol : firstOfCurrentProduction) {
                            if (symbol.equals("epsilon"))
                                symbol = "$";
                            if (parsingTable.get(new Pair<>(key, symbol)).getFirst().equals("none"))
                                parsingTable.put(new Pair<>(key, symbol), new Pair<>(String.join(" ", prod), productionsRHS.indexOf(prod) + 1));
                            else {
                                try {
                                    throw new Exception("CONFLICT: " + key + ", " + symbol);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }

                // otherwise, treat case where rhs is epsilon
                // for each element of FOLLOW(lhs) we try to add it to table or signal conflict
                else {
                    var followSet = follow.get(key);
                    for (var symbol : followSet) {
                        if (symbol.equals("epsilon")) {
                            symbol = "$";
                            if (parsingTable.get(new Pair<>(key, symbol)).getFirst().equals("none")) {
                                var p = new ArrayList<>(List.of("epsilon", key));
                                parsingTable.put(new Pair<>(key, symbol), new Pair<>("epsilon", productionsRHS.indexOf(p) + 1));
                            } else {
                                try {
                                    throw new Exception("CONFLICT: " + key + ", " + symbol);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else if (parsingTable.get(new Pair<>(key, symbol)).getFirst().equals("none")) {
                            var p = new ArrayList<>(List.of("epsilon", key));
                            parsingTable.put(new Pair<>(key, symbol), new Pair<>("epsilon", productionsRHS.indexOf(p) + 1));
                        } else {
                            try {
                                throw new Exception("CONFLICT: " + key + ", " + symbol);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }

    public List<Integer> parseSequence(List<String> sequence) {
        Stack<String> alpha = new Stack<>();
        Stack<String> beta = new Stack<>();
        List<Integer> result = new ArrayList<>();

        // initialize the alpha stack with $ and the sequence
        alpha.push("$");
        for (var i = sequence.size() - 1; i >= 0; i--)
            alpha.push(sequence.get(i));

        // initialize the beta stack with $ and the starting symbol
        beta.push("$");
        beta.push(grammar.getStartSymbol());

        // while we do not have reached accept
        while (!(alpha.peek().equals("$") && beta.peek().equals("$"))) {
            // we get the top of the stacks and the corresponding element from the table
            String alphaTopElement = alpha.peek();
            String betaTopElement = beta.peek();
            Pair<String, String> key = new Pair<>(betaTopElement, alphaTopElement);
            Pair<String, Integer> value = parsingTable.get(key);

            // if in the parsing table we have none it means that the sequence is not accepted by the grammar
            if (value.getFirst().equals("none")) {
                System.out.println("Syntax error for: " + key);
                result = new ArrayList<>(List.of(-1));
                return result;
            }

            // if we have pop, we have to pop the top of the stacks
            if (value.getFirst().equals("pop")) {
                alpha.pop();
                beta.pop();
            }
            // otherwise we pop the top of beta and if the value from the table is not epsilon, we add the values to
            // beta and the corresponding number of the production to the productions string
            else {
                beta.pop();
                if (!value.getFirst().equals("epsilon")) {
                    String[] symbols = value.getFirst().split(" ");
                    for (var i = symbols.length - 1; i >= 0; i--) {
                        beta.push(symbols[i]);
                    }
                }
                result.add(value.getSecond());
            }
        }
        return result;
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

    public void printParsingTable() {
        List<String> nonTerminals = grammar.getNonTerminals();
        List<String> terminals = grammar.getTerminals();

        List<String> rows = new ArrayList<>();
        rows.addAll(nonTerminals);
        rows.addAll(terminals);
        rows.add("$");

        List<String> cols = new ArrayList<>();
        cols.addAll(terminals);
        cols.add("$");

        int rowHeaderWidth = 3;
        int colHeaderWidth = 10;

        // Print column headers
        System.out.printf("%-" + rowHeaderWidth + "s |", ""); // Empty space for row header column
        for (String col : cols) {
            System.out.printf(" %-" + colHeaderWidth + "s |", col);
        }
        System.out.println();

        // Print separator
        int totalWidth = rowHeaderWidth + 3 + (colHeaderWidth + 3) * cols.size(); // 3 accounts for spaces and vertical bars
        for (int i = 0; i < totalWidth; i++) {
            System.out.print("-");
        }
        System.out.println();

        // Print rows with their respective data
        for (String row : rows) {
            System.out.printf("%-" + rowHeaderWidth + "s |", row);
            for (String col : cols) {
                Pair<String, Integer> cell = parsingTable.get(new Pair<>(row, col));
                System.out.printf(" %-" + colHeaderWidth + "s |", removeSpacesFromString(cell.getFirst()) + ", " + cell.getSecond());
            }
            System.out.println();
        }
    }

    private String removeSpacesFromString(String s) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c != ' ') {
                sb.append(c);
            }
        }

        return sb.toString();
    }
}
