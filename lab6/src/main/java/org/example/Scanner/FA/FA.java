package org.example.Scanner.FA;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FA {
    private List<String> states;
    private List<String> alphabet;
    private List<Transition> transitions;
    private String initialState;
    private List<String> finalStates;
    private String fileName;

    public FA(String oFileName) throws FileNotFoundException {
        states = new ArrayList<>();
        alphabet = new ArrayList<>();
        transitions = new ArrayList<>();
        initialState = "";
        finalStates = new ArrayList<>();
        fileName = oFileName;
        getDataForFa();
    }

    private void getDataForFa() throws FileNotFoundException {
        File fileToRead = Paths.get("files","fa", fileName).toFile();
        Scanner scanner = new Scanner(fileToRead);
        Pattern pattern = Pattern.compile("\\{([^}]*)\\}");
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.startsWith("states=")) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    String[] stateArray = matcher.group(1).split(",");
                    states.addAll(Arrays.asList(stateArray));
                }
            }
            if (line.startsWith("initial_state=")) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    initialState = matcher.group(1);
                }
            }
            if (line.startsWith("final_states=")) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    String[] finalStatesArray = matcher.group(1).split(",");
                    finalStates.addAll(Arrays.asList(finalStatesArray));
                }
            }
            if (line.startsWith("alphabet=")) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    String[] alphabetArray = matcher.group(1).split(",");
                    alphabet.addAll(Arrays.asList(alphabetArray));
                }
            }
            if (line.startsWith("transitions=")) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    String[] transitionArray = matcher.group(1).split(";");
                    for (String transition : transitionArray) {
                    String[] components = transition.substring(1,transition.length()-1).split(",");
                    if(components.length == 3)
                        transitions.add(new Transition(components[0].strip(),components[1].strip(),components[2].strip()));
                    }
                }
            }
        }
        scanner.close();
    }

    public boolean checkIfGood(String sequence){
        List<String> letterList= List.of(sequence.split(""));
        String currentState = initialState;

        for(String ch: letterList){
            boolean ok = false;
            for(Transition transition: transitions)
                if(transition.getFromState().equals(currentState) && transition.getElement().equals(ch) && alphabet.contains(ch)){
                    currentState = transition.getToState();
                    ok = true;
                    break;
                }
            if(!ok)
                return false;
        }

        return finalStates.contains(currentState);
    }

    public String getStatesString() {
        return states.toString();
    }

    public String getAlphabetString() {
        return alphabet.toString();
    }

    public String getTransitionsString() {
        StringBuilder transitionStr = new StringBuilder("Transitions: {");

        for (int i = 0; i < transitions.size(); i++) {
            Transition transition = transitions.get(i);
            transitionStr
                    .append("(")
                    .append(transition.getFromState())
                    .append(", ")
                    .append(transition.getToState())
                    .append(", ")
                    .append(transition.getElement())
                    .append(")");

            if (i < transitions.size() - 1)
                transitionStr.append(";");
            transitionStr.append("}");
        }
        return transitionStr.toString();
    }


    public String getInitialStateString() {
        return initialState;
    }

    public String getFinalStatesString() {
        return finalStates.toString();
    }
}
