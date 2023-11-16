package org.example;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.commons.io.FilenameUtils;

public class MyScanner {

    private SymTable symTable;
    private List<String> separators;
    private List<String> operators;
    private List<String> keywords;
    private List<Pair<String,Integer>> pif;

    private File programFile;

    private String line;
    private int index;
    private int currentLine;

    public MyScanner(File programFileIn) throws IOException {
        symTable = new SymTable(50);
        separators = new ArrayList<>();
        operators = new ArrayList<>();
        keywords = new ArrayList<>();
        pif = new ArrayList<>();
        programFile = programFileIn;
        addTokens(new File("files/token.in"));
    }

    private void addTokens(File tokenFile) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(tokenFile));
        String line;
        int lineNo = 1;

        while((line = br.readLine()) != null){
            if(lineNo>=1 && lineNo<=14){
                operators.add(line.trim());
            }
            else if(lineNo>=15 && lineNo<=23){
                separators.add(line.trim());
            }
            else{
                keywords.add(line.trim());
            }
            lineNo++;
        }
    }

    private void skipSpaces() {
        while (index < line.length() && Character.isWhitespace(line.charAt(index))) {
            index++;
        }
    }

    public boolean stringConstantCase() {
        var stringConstantRegex = Pattern.compile("^\"[\\w ]*\"");
        var matcher = stringConstantRegex.matcher(line.substring(index));

        if (!matcher.find()) {
            return false;
        }

        var stringConstant = matcher.group(0);
        index += stringConstant.length();
        symTable.addSymbol(stringConstant);
        var position = symTable.getPosition(stringConstant);
        pif.add(new Pair("stringConstant", position));
        return true;
    }

    public boolean intConstantCase() {
        var intConstantRegex = Pattern.compile("^([+-]?[1-9][0-9]*|0)");
        var matcher = intConstantRegex.matcher(line.substring(index));

        var invalidIntConstantRegex = Pattern.compile("([+-]?[1-9][0-9]*|0)[a-zA-Z_]+");
        if (!matcher.find() || invalidIntConstantRegex.matcher(line.substring(index)).find()) {
            return false;
        }

        var intConstant = matcher.group(0);
        index += intConstant.length();
        symTable.addSymbol(intConstant);
        var position = symTable.getPosition(intConstant);
        pif.add(new Pair("int_constant", position));
        return true;
    }

    public boolean identifierCase() {
        var identifierRegex = Pattern.compile("^[a-zA-Z][a-zA-Z0-9_]*");
        var matcher = identifierRegex.matcher(line.substring(index));

        if (!matcher.find()) {
            return false;
        }

        var identifier = matcher.group(0);

        if (keywords.contains(identifier) ) {
            return false;
        }

        // see if it is already in the symbol table
        var position = symTable.getPosition(identifier);
        if (symTable.hasSymbol(identifier)) {
            index += identifier.length();
            pif.add(new Pair("id", position));
            return true;
        }

        index += identifier.length();
        symTable.addSymbol(identifier);
        position = symTable.getPosition(identifier);
        pif.add(new Pair("id", position));
        return true;
    }

    public boolean tokenCase() {
        String possibleToken = line.substring(index).split(" ")[0];
        String possibleOp = "";
        for (var op: operators) {
            if (possibleToken.startsWith(op)) {
                if(possibleOp.length() < op.length())
                 possibleOp = op;
            }
        }
        if(!possibleOp.isEmpty()) {
            index += possibleOp.length();
            pif.add(new Pair(possibleOp, -1));
            return true;
        }//for multichar operator

        for (var sep: separators) {
            if (possibleToken.startsWith(sep)) {
                index += sep.length();
                pif.add(new Pair(sep, -1));
                return true;
            }
        }

        for (var kw: keywords) {
            if (possibleToken.startsWith(kw)) {
                var invalidRegex = Pattern.compile(kw + "[^\\[\\]()]+");
                if (invalidRegex.matcher(possibleToken).find() ) {
                    return false;
                }

                index += kw.length();
                pif.add(new Pair(kw, -1));
                return true;
            }
        }
        return false;
    }

    public void next() throws Exception {
        skipSpaces();
        if (index == line.length())
            return;
        if (stringConstantCase())
            return;
        if (intConstantCase())
            return;
        if (identifierCase())
            return;
        if (tokenCase())
            return;
        throw new Exception("Program " + FilenameUtils.getBaseName(programFile.getName()) +": lexical error at line " + currentLine);
    }

    public void scan() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(programFile));
            this.index = 0;
            this.currentLine = 0;
            while((line = reader.readLine()) != null){
                this.currentLine++;
                this.index = 0;
                line = line.strip();

                while(index<line.length()){
                        next();
                }
            }

            FileWriter fileWriter = new FileWriter("files/PIFs/PIF_"+ FilenameUtils.getBaseName(programFile.getName()) +".out");
            for (int i = 0; i < pif.size(); i++) {
                fileWriter.write(pif.get(i).getFirst() + " => " + pif.get(i).getSecond() + "\n");
            }
            fileWriter.close();
            fileWriter = new FileWriter("files/STs/ST_"+ FilenameUtils.getBaseName(programFile.getName()) +".out");
            fileWriter.write(symTable.toString());
            fileWriter.close();
            System.out.println("Program " + FilenameUtils.getBaseName(programFile.getName()) +": lexically correct");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void changeProgram(File file) {
        symTable = new SymTable(50);
        pif = new ArrayList<>();
        programFile = file;
    }
}
