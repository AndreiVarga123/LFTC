package org.example;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void mainMenu(){
        System.out.println("1 - FA menu");
        System.out.println("2 - Run programs");
    }
    public static void mainCases() throws IOException {
        mainMenu();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Option:");
        String option = scanner.nextLine();
        if(option.equals("1")){
            finiteAutomataCases();
        }
        else{
            MyScanner myScanner = new MyScanner(new File("files/programs/p1.txt"));
            myScanner.scan();
            myScanner.changeProgram(new File("files/programs/p1err.txt"));
            myScanner.scan();
            myScanner.changeProgram(new File("files/programs/p2.txt"));
            myScanner.scan();
            myScanner.changeProgram(new File("files/programs/p3.txt"));
            myScanner.scan();
        }
    }
    public static void printMenu(){
        System.out.println("1 - Display the set of states");
        System.out.println("2 - Display the alphabet");
        System.out.println("3 - Display the transitions");
        System.out.println("4 - Display the initial state");
        System.out.println("5 - Display the final states");
        System.out.println("6 - Display if the given sequence is accepted by the DFA");
        System.out.println("0 - Exit");
    }

    public static void finiteAutomataCases() {
        File file = new File("files/FA/FA.in");
        FiniteAutomata fa = new FiniteAutomata();
        fa.readFile(file);

        printMenu();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Option:");
        String option = scanner.nextLine();
        while (!option.equals("0")) {
            switch (option) {
                case "1" -> {
                    System.out.println("States:");
                    System.out.println(fa.getStates());
                }
                case "2" -> {
                    System.out.println("Alphabet:");
                    System.out.println(fa.getAlphabet());
                }
                case "3" -> {
                    System.out.println("Transitions:");
                    System.out.println(fa.getTransitions());
                }
                case "4" -> {
                    System.out.println("Initial State:");
                    System.out.println(fa.getInitialState());
                }
                case "5" -> {
                    System.out.println("Finale States:");
                    System.out.println(fa.getFinalStates());
                }
                case "6" -> {
                    System.out.println("Sequence:");
                    String seq = scanner.nextLine();
                    System.out.println(fa.isAccepted(seq));
                }
            }
            System.out.println();
            printMenu();
            System.out.println("Option:");
            option = scanner.nextLine();
        }
    }
    public static void main(String[] args) throws IOException {
        mainCases();
    }
}