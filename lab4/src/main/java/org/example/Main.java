package org.example;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(new File("files/programs/p1.txt"));
        scanner.scan();
        scanner.changeProgram(new File("files/programs/p1err.txt"));
        scanner.scan();
        scanner.changeProgram(new File("files/programs/p2.txt"));
        scanner.scan();
        scanner.changeProgram(new File("files/programs/p3.txt"));
        scanner.scan();
    }
}