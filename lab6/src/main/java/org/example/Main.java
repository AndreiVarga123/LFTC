package org.example;


import org.example.Parser.Grammar;
import org.example.Parser.LL1;
import org.example.Parser.ParserOut;
import org.example.Scanner.LexicalAnalyzer;
import org.example.Scanner.PIFReader;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class Main {
    public static void printMenu() {
        System.out.println("1  - Print the set of nonterminals");
        System.out.println("2  - Print the set of terminals");
        System.out.println("3  - Print the set of productions");
        System.out.println("4  - Print the productions of a given nonterminal");
        System.out.println("5  - CFG check");
        System.out.println("6  - LL1 parser FIRST set");
        System.out.println("7  - LL1 parser FOLLOW set");
        System.out.println("8  - Parsing Table");
        System.out.println("9  - Parse Sequence");
        System.out.println("10 - Print parse tree for g1");
        System.out.println("11 - Print parse tree for g2");
        System.out.println("0 - EXIT");
    }

    public static void cases() throws Exception {
        Grammar g = new Grammar();
        String filename = "files/grammar/g2.in";
        g.readFromFile(filename);
        LL1 ll1 = new LL1(g);
        ll1.FIRST();
        ll1.FOLLOW();
        ll1.ParsingTable();
        LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer("problem");
        lexicalAnalyzer.scan();
        printMenu();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Input the number of your option:");
        String option = scanner.nextLine();

        while (!Objects.equals(option, "0")) {
            switch (option) {
                case "0" -> {
                    return;
                }
                case "1" -> g.printNonTerminals();
                case "2" -> g.printTerminals();
                case "3" -> g.printProductions();
                case "4" -> {
                    System.out.println("Input the nonterminal:");
                    String nonTerminal = scanner.nextLine();
                    g.printProductionsForNonterminal(nonTerminal);
                }
                case "5" -> {
                    if (g.checkCFG()) System.out.println("The given grammar is a CFG!");
                    else System.out.println("The given grammar is NOT a CFG!");
                }
                case "6" -> {
                    ll1.printFirstSet();
                }
                case "7" -> {
                    ll1.printFollowSet();
                }
                case "8" -> {
                    ll1.printParsingTable();
                }
                case "9" -> {
                    List<String> sequence = new ArrayList<>(List.of("a", "*", "(", "a", "+", "a", ")"));
                    List<Integer> productionString = ll1.parseSequence(sequence);
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < productionString.size(); i++) {
                        stringBuilder.append(productionString.get(i));
                        stringBuilder.append(" ");
                    }
                    System.out.println(stringBuilder);
                }
                case "10" -> {
                    List<String> sequence = new ArrayList<>(List.of("a", "*", "(", "a", "+", "a", ")"));
                    ParserOut parserOutput = new ParserOut(ll1, sequence, "files/out/out1.txt");
                    parserOutput.printTree();
                }
                case "11" -> {
                    if (filename.contains("g1"))
                        System.out.println("Please change grammar to use this option!  ");
                    List<String> sequence = PIFReader.readFromPifOutput("files/pif/problem_PIF.out");
                    ParserOut parserOutput = new ParserOut(ll1, sequence, "files/out/out2.txt");
                    parserOutput.printTree();
                }
            }
            System.out.println();
            printMenu();
            System.out.println("Input the number of your option:");
            option = scanner.nextLine();
        }
    }
    public static void main(String[] args) throws Exception {
        cases();
    }
}