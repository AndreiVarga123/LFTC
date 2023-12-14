package org.example;

import java.util.Objects;
import java.util.Scanner;

public class Main {
    public static void printMenu() {
        System.out.println("1 - Read the grammar from a file");
        System.out.println("2 - Print the set of non-terminals");
        System.out.println("3 - Print the set of terminals");
        System.out.println("4 - Print the set of productions");
        System.out.println("5 - Print the productions of a given non-terminal");
        System.out.println("6 - CFG check");
        System.out.println("7 - LL1 parser FIRST set");
        System.out.println("8 - LL1 parser FOLLOW set");
        System.out.println("0 - EXIT");
    }

    public static void cases() {
        Grammar grammar = new Grammar();
        LL1 ll1 = new LL1(grammar);
        boolean fileRead = false;
        printMenu();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Option:");
        String option = scanner.nextLine();
        while (!Objects.equals(option, "0")) {
            switch (option) {
                case "0" -> {return;}
                case "1" -> {
                    System.out.println("Input the filename:");
                    String filename = scanner.nextLine();
                    grammar.readFromFile(filename);
                    fileRead = true;
                }
                case "2" -> grammar.printNonTerminals();
                case "3" -> grammar.printTerminals();
                case "4" -> grammar.printProductions();
                case "5" -> {
                    System.out.println("Input the non-terminal:");
                    String nonTerminal = scanner.nextLine();
                    grammar.printProductionsForNonterminal(nonTerminal);
                }
                case "6" -> {
                    if (grammar.checkCFG()) System.out.println("The given grammar is a CFG");
                    else System.out.println("The given grammar is NOT a CFG");
                }
                case "7" -> {
//                    if(fileRead && ll1.getFirstSet().size()==0)
//                        ll1.FIRST();
//                    ll1.printFirstSet();
                }
                case "8" -> {
                    if(fileRead && ll1.getFollowSet().size()==0)
                        ll1.FOLLOW();
                    ll1.printFollowSet();
                }
            }
            System.out.println();
            printMenu();
            System.out.println("Option:");
            option = scanner.nextLine();
        }
    }
    public static void main(String[] args) {
        cases();
    }
}