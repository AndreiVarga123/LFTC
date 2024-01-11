package org.example.Scanner;


import org.example.Scanner.FA.FA;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LexicalAnalyzer {
    private ArrayList<String> operators = new ArrayList<>();

    private ArrayList<String> separators = new ArrayList<>();

    private ArrayList<String> keywords = new ArrayList<>();
    private SymbolTable symbolTable;
    private ArrayList<Pair> PIF;
    private String file;
    private String lineString;
    private int line = 1;
    private int index;
    private FA faInt;
    private FA faIdentifier;

    public LexicalAnalyzer(String file) {
        this.symbolTable = new SymbolTable(100);
        this.PIF = new ArrayList<>();
        this.file = file;
        try {
            faInt = new FA("constFiniteAutomata");
            faIdentifier = new FA("identifierFiniteAutomata");
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }


    private void readOperatorsSeparatorsAndKeywords() throws FileNotFoundException {
        File tokens = Paths.get("files","tokens","tokens.in").toFile();
        Scanner scanner = new Scanner(tokens);
        String currentType = "nothing";
        while(scanner.hasNextLine()){
            String currentLine = scanner.nextLine();
            switch (currentLine) {
                case "operators:" -> currentType = "operators";
                case "separators:" -> currentType = "separators";
                case "keywords:" -> currentType = "keywords";
                default -> {
                }
            }
            if(currentType.equals("operators") && !currentLine.equals("operators:"))
                operators.add(currentLine.strip());
            if(currentType.equals("separators")&& !currentLine.equals("separators:"))
                separators.add(currentLine.strip());
            if(currentType.equals("keywords")&& !currentLine.equals("keywords:"))
                keywords.add(currentLine.strip());
        }
    }

    // scan
    // while( not (end of file)
    //detect (token) -> returns the type of the token
    //if token is a keyword or an operator or separator -> addToPIF(token, -1)
    //if token is an identifier or constant ( dupa o formula de regex for matching)
    //-----> position = st.add(token) && addToPif(id or const based on type, position)
    //else lexical error ca nu i niciuna
    public void scan() throws Exception{
        readOperatorsSeparatorsAndKeywords();
        File input = Paths.get("files","problems",file).toFile();
        Scanner scanner = new Scanner(input);
        while(scanner.hasNextLine()){
            lineString = scanner.nextLine();
            index = 0;
            while(index < lineString.length()){
                detectAndProgress();
            }
            line ++;
        }
        try{
            File pifOut = Paths.get("files","pif",file+"_PIF.out").toFile();
            pifOut.createNewFile();
            FileWriter fileWriter = new FileWriter(pifOut);
            for(Pair s : PIF){
                fileWriter.write(s.type + " -> " + s.position + "\n");
            }
            fileWriter.close();
            File STOut = Paths.get("files","st",file+"_ST.out").toFile();
            STOut.createNewFile();
            fileWriter = new FileWriter(STOut);
            fileWriter.write(symbolTable.toString());
            fileWriter.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void detectAndProgress() throws Exception {
        if(Character.isWhitespace(lineString.charAt(index))) {
            index++;
            return;
        }
        if(detectListAccess()){
            return;
        }
        if(detectList()){
            return;
        }
        if(detectString()){
            return;
        }
        if(detectInt()){
            return;
        }
        if(detectToken()){
            return;
        }
        if(detectIdentifier()){
            return;
        }
        throw new Exception("LEXICAL ERROR AT LINE: " + line + " INDEX:" + index);
    }

    private boolean detectString(){
        Pattern stringRegex = Pattern.compile("^\"([A-Za-z0-9 !@#$%^&*_]+)\"");
        Matcher matcher = stringRegex.matcher(lineString.substring(index));
        if(!matcher.find())
            return false;
        Pattern invalidStringRegex = Pattern.compile("^\"([A-Za-z0-9 !@#$%^&*_]+)");
        if(invalidStringRegex.matcher(lineString.substring(index)).find())
            return false;
        String string = matcher.group(0);
        index += string.length();
        int pos = symbolTable.addSymbol(string,symbolTable.hashCode(string));
        PIF.add(new Pair("stringConstant",pos));
        return true;
    }

    private boolean detectInt(){
        Pattern intRegex = Pattern.compile("^([-]?[1-9][0-9]*|0)");
        Matcher matcher = intRegex.matcher(lineString.substring(index));
        if(!matcher.find())
            return false;
        Pattern invalidIntRegex = Pattern.compile("^([-]?[1-9][0-9]*|0)[a-zA-Z!@#$%^&*_]");
        if(invalidIntRegex.matcher(lineString.substring(index)).find())
            return false;
        String integer = matcher.group(1);
        index += integer.length();
        int pos = symbolTable.addSymbol(integer,symbolTable.hashCode(integer));
        PIF.add(new Pair("intConstant",pos));
        return true;
    }

    private boolean detectIdentifier(){
        Pattern identifierRegex = Pattern.compile("^[a-zA-Z][a-zA-Z0-9_]*");
        Matcher matcher = identifierRegex.matcher(lineString.substring(index));
        if(!matcher.find())
            return false;
        Pattern invalidIdentifierRegex= Pattern.compile("^[a-zA-Z][a-zA-Z0-9_]*!");
        if(invalidIdentifierRegex.matcher(lineString.substring(index)).find())
            return false;
        String identifier = matcher.group(0);
        if(keywords.contains(identifier))
            return false;
        int pos = symbolTable.addSymbol(identifier,symbolTable.hashCode(identifier));
        index += identifier.length();
        PIF.add(new Pair("id",pos));
        return true;
    }

    private boolean detectIntFA(){
        int i = 1;
        boolean found = false;
        while(faInt.checkIfGood(lineString.substring(index,index+i))){
            i++;
            found = true;
        }
        if(found){
            index += i;
            String integer = lineString.substring(index,index+i);
            int pos = symbolTable.addSymbol(integer,symbolTable.hashCode(integer));
            PIF.add(new Pair("intConstant",pos));
            return true;
        }
        return false;
    }

    private boolean detectIdentifierFA(){
        int i = 1;
        boolean found = false;
        while(faIdentifier.checkIfGood(lineString.substring(index,index+i))){
            i++;
            found = true;
        }
        if(found){
            index += i;
            String identifier = lineString.substring(index,index+i);
            int pos = symbolTable.addSymbol(identifier,symbolTable.hashCode(identifier));
            PIF.add(new Pair("id",pos));
            return true;
        }
        return false;
    }
    private boolean detectToken(){
        String possibleToken = lineString.substring(index).split(" ")[0];
        for(String operator: operators){
            if(operator.equals(possibleToken)){
                index += operator.length();
                PIF.add(new Pair(operator,-1));
                return true;
            }
            else if(possibleToken.startsWith(operator)){
                index += operator.length();
                PIF.add(new Pair(operator,-1));
                return true;
            }
        }
        for(String separator: separators){
            if(separator.equals(possibleToken)){
                index += separator.length();
                PIF.add(new Pair(separator,-1));
                return true;
            }
            else if(possibleToken.startsWith(separator)){
                index += separator.length();
                PIF.add(new Pair(separator,-1));
                return true;
            }
        }
        for(String keyword: keywords){
            if (possibleToken.startsWith(keyword)){
                index += keyword.length();
                PIF.add(new Pair(keyword, -1));
                return true;
            }
        }
        return false;
    }

    private boolean detectList(){
        Pattern listRegex = Pattern.compile("^\\[\\s*\\d+(\\s*,\\s*\\d+)*\\s*\\]");
        Matcher matcher = listRegex.matcher(lineString.substring(index));
        if(!matcher.find())
            return false;
        Pattern invalidListRegex = Pattern.compile("^\\[\\s*\\d+(\\s*,\\s*\\d+)*\\s*[A-Za-z]+\\]");
        if(invalidListRegex.matcher(lineString.substring(index)).find())
            return false;
        String list = matcher.group(0);
        String[] elements = list.substring(1,list.length()-1).split(",");
        PIF.add(new Pair("[", -1));
        for(int i = 0 ; i  < elements.length -1; i++){
            int pos = symbolTable.addSymbol(elements[i],symbolTable.hashCode(elements[i]));
            PIF.add(new Pair("intConstant",pos));
            PIF.add(new Pair(",",-1));
        }
        int pos = symbolTable.addSymbol(elements[elements.length-1],symbolTable.hashCode(elements[elements.length-1]));
        PIF.add(new Pair("intConstant",pos));
        PIF.add(new Pair("]", -1));
        index += list.length();
        return true;
    }

    private boolean detectListAccess(){
        Pattern listAccessRegex = Pattern.compile("^[a-zA-Z][a-zA-Z0-9_]*\\[(?:[a-zA-Z][a-zA-Z0-9_]*|\\d+)\\]");
        Matcher matcher = listAccessRegex.matcher(lineString.substring(index));
        if(!matcher.find())
            return false;
        Pattern invalidListAccessRegex = Pattern.compile("^[0-9][a-zA-Z][a-zA-Z0-9_]*\\[(?:[a-zA-Z][a-zA-Z0-9_]*|\\d+)\\]");
        if(invalidListAccessRegex.matcher(lineString.substring(index)).find())
            return false;
        String listAccess = matcher.group(0);
        index += listAccess.length();
        String[] elements = listAccess.split("\\[");
        int pos = symbolTable.addSymbol(elements[0],symbolTable.hashCode(elements[0]));
        PIF.add(new Pair("id",pos));
        PIF.add(new Pair("[",-1));
        String identifierOrConstant = elements[1].substring(0,elements.length-1);
        if(Character.isDigit(identifierOrConstant.charAt(0))) {
            pos = symbolTable.addSymbol(identifierOrConstant, symbolTable.hashCode(identifierOrConstant));
            PIF.add(new Pair("intConstant", pos));
        }
        else{
            pos = symbolTable.addSymbol(identifierOrConstant, symbolTable.hashCode(identifierOrConstant));
            PIF.add(new Pair("id", pos));
        }
        PIF.add(new Pair("]",-1));
        return true;
    }
}
